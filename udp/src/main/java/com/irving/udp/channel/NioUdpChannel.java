package com.irving.udp.channel;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.MembershipKey;
import java.nio.channels.SelectionKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.irving.udp.proto.UDPPacket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultAddressedEnvelope;
import io.netty.channel.EventLoop;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.nio.AbstractNioMessageChannel;
import io.netty.channel.socket.DatagramChannelConfig;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;

/**
 * 自定义NioUdpChannel，重写了config,ProtocolFamilyConverter,read/write方法
 * @author yuanyc
 */
public class NioUdpChannel 
	extends AbstractNioMessageChannel implements io.netty.channel.socket.DatagramChannel {
	
    private static final ChannelMetadata METADATA = new ChannelMetadata(true);

    private final DatagramChannelConfig config;
    private final Map<InetAddress, List<MembershipKey>> memberships =
            new HashMap<InetAddress, List<MembershipKey>>();

    private RecvByteBufAllocator.Handle allocHandle;
	
	/**
	 * 构造方法相关方法
	 * @return
	 */
    private static DatagramChannel newSocket() {
        try {
            return DatagramChannel.open();
        } catch (IOException e) {
            throw new ChannelException("Failed to open a socket.", e);
        }
    }

    private static DatagramChannel newSocket(InternetProtocolFamily ipFamily) {
        if (ipFamily == null) {
            return newSocket();
        }

        if (PlatformDependent.javaVersion() < 7) {
            throw new UnsupportedOperationException();
        }

        try {
            return DatagramChannel.open(ProtocolFamilyConverter.convert(ipFamily));
        } catch (IOException e) {
            throw new ChannelException("Failed to open a socket.", e);
        }
    }

    public NioUdpChannel(EventLoop eventLoop) {
        this(eventLoop, newSocket());
    }

    /**
     * Create a new instance using the given {@link InternetProtocolFamily}. If {@code null} is used it will depend
     * on the Operation Systems default which will be chosen.
     */
    public NioUdpChannel(EventLoop eventLoop, InternetProtocolFamily ipFamily) {
        this(eventLoop, newSocket(ipFamily));
    }

    /**
     * Create a new instance from the given {@link DatagramChannel}.
     */
    public NioUdpChannel(EventLoop eventLoop, DatagramChannel socket) {
        super(null, eventLoop, socket, SelectionKey.OP_READ);
        config = new NioUdpChannelConfig(this, socket);
    }

	@SuppressWarnings("deprecation")
	@Override
	public boolean isActive() {
		DatagramChannel ch = javaChannel();
        return ch.isOpen() && (
                (config.getOption(ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION) && isRegistered())
                || ch.socket().isBound());
	}

    @Override
    public ChannelMetadata metadata() {
        return METADATA;
    }

    @Override
    public DatagramChannelConfig config() {
        return config;
    }

    @Override
    public boolean isConnected() {
        return javaChannel().isConnected();
    }

    @Override
    protected DatagramChannel javaChannel() {
        return (DatagramChannel) super.javaChannel();
    }

    @Override
    protected SocketAddress localAddress0() {
        return javaChannel().socket().getLocalSocketAddress();
    }

    @Override
    protected SocketAddress remoteAddress0() {
        return javaChannel().socket().getRemoteSocketAddress();
    }

    @Override
    protected void doBind(SocketAddress localAddress) throws Exception {
        javaChannel().socket().bind(localAddress);
    }

    @Override
    protected boolean doConnect(SocketAddress remoteAddress,
            SocketAddress localAddress) throws Exception {
        if (localAddress != null) {
            javaChannel().socket().bind(localAddress);
        }

        boolean success = false;
        try {
            javaChannel().connect(remoteAddress);
            success = true;
            return true;
        } finally {
            if (!success) {
                doClose();
            }
        }
    }

    @Override
    protected void doFinishConnect() throws Exception {
        throw new Error();
    }

    @Override
    protected void doDisconnect() throws Exception {
        javaChannel().disconnect();
    }

    @Override
    protected void doClose() throws Exception {
        javaChannel().close();
    }

    @Override
    protected int doReadMessages(List<Object> buf) throws Exception {
        DatagramChannel ch = javaChannel();
        DatagramChannelConfig config = config();
        RecvByteBufAllocator.Handle allocHandle = this.allocHandle;
        if (allocHandle == null) {
            this.allocHandle = allocHandle = config.getRecvByteBufAllocator().newHandle();
        }
        
        // buf直接将data(Protobuf数据) add之后return即可
        ByteBuf data = allocHandle.allocate(config.getAllocator());
        boolean free = true;
        try {
            ByteBuffer nioData = data.internalNioBuffer(data.writerIndex(), data.writableBytes());
            int pos = nioData.position();
            
            // 读取数据并得到地址
            InetSocketAddress remoteAddress = (InetSocketAddress) ch.receive(nioData);
            if (remoteAddress == null) {
                return 0;
            }

            int readBytes = nioData.position() - pos;
            data.writerIndex(data.writerIndex() + readBytes);
            allocHandle.record(readBytes);
            
            String src = remoteAddress.toString().substring(1, remoteAddress.toString().length());
            String dst = localAddress().toString().substring(1,localAddress().toString().length());

            buf.add(new UDPPacket(src, dst, data));
//            buf.add(data);
            
            free = false;
            return 1;
        } catch (Throwable cause) {
            PlatformDependent.throwException(cause);
            return -1;
        }  finally {
            if (free) {
                data.release();
            }
        }
    }

    @Override
    protected boolean doWriteMessage(Object msg, ChannelOutboundBuffer in) throws Exception {
        final Object m;	//具体发送的数据
        final SocketAddress remoteAddress;
        ByteBuf data;
        
        /**
         * Handler最后传入的类型UDPPacket，其中content是protobuf编码的byte数组
         */
        if (msg instanceof UDPPacket) {
        	UDPPacket packet = (UDPPacket) msg;
        	String[] address = packet.getDst().split(":");
        	String hostname = address[0];
        	int port = Integer.parseInt(address[1]);
            remoteAddress = new InetSocketAddress(hostname, port);
            m = packet.getContent();
        } else {
            m = msg;
            remoteAddress = null;
        }

        if (m instanceof ByteBufHolder) {	//不太确定Protobuf编码以后是ByteBufHolder还是byte
            data = ((ByteBufHolder) m).content();
        } else if (m instanceof ByteBuf) {	
            data = (ByteBuf) m;
        } else {
            throw new UnsupportedOperationException("unsupported message type: " + StringUtil.simpleClassName(msg));
        }

        int dataLen = data.readableBytes();
        if (dataLen == 0) {
            return true;
        }

        ByteBufAllocator alloc = alloc();
        boolean needsCopy = data.nioBufferCount() != 1;
        if (!needsCopy) {
            if (!data.isDirect() && alloc.isDirectBufferPooled()) {
                needsCopy = true;
            }
        }
        ByteBuffer nioData;
        if (!needsCopy) {
            nioData = data.nioBuffer();
        } else {
            data = alloc.directBuffer(dataLen).writeBytes(data);
            nioData = data.nioBuffer();
        }

        final int writtenBytes;
        if (remoteAddress != null) {
            writtenBytes = javaChannel().send(nioData, remoteAddress);
        } else {
            writtenBytes = javaChannel().write(nioData);
        }

        boolean done =  writtenBytes > 0;
        if (needsCopy) {
            // This means we have allocated a new buffer and need to store it back so we not need to allocate it again
            // later
            if (remoteAddress == null) {
                // remoteAddress is null which means we can handle it as ByteBuf directly
                in.current(data);
            } else {
                if (!done) {
                    // store it back with all the needed informations
                    in.current(new DefaultAddressedEnvelope<ByteBuf, SocketAddress>(data, remoteAddress));
                } else {
                    // Just store back the new create buffer so it is cleaned up once in.remove() is called.
                    in.current(data);
                }
            }
        }
        return done;
    }

    @Override
    public InetSocketAddress localAddress() {
        return (InetSocketAddress) super.localAddress();
    }

    @Override
    public InetSocketAddress remoteAddress() {
        return (InetSocketAddress) super.remoteAddress();
    }

    @Override
    public ChannelFuture joinGroup(InetAddress multicastAddress) {
        return joinGroup(multicastAddress, newPromise());
    }

    @Override
    public ChannelFuture joinGroup(InetAddress multicastAddress, ChannelPromise promise) {
        try {
            return joinGroup(
                    multicastAddress,
                    NetworkInterface.getByInetAddress(localAddress().getAddress()),
                    null, promise);
        } catch (SocketException e) {
            promise.setFailure(e);
        }
        return promise;
    }

    @Override
    public ChannelFuture joinGroup(
            InetSocketAddress multicastAddress, NetworkInterface networkInterface) {
        return joinGroup(multicastAddress, networkInterface, newPromise());
    }

    @Override
    public ChannelFuture joinGroup(
            InetSocketAddress multicastAddress, NetworkInterface networkInterface,
            ChannelPromise promise) {
        return joinGroup(multicastAddress.getAddress(), networkInterface, null, promise);
    }

    @Override
    public ChannelFuture joinGroup(
            InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress source) {
        return joinGroup(multicastAddress, networkInterface, source, newPromise());
    }

    @Override
    public ChannelFuture joinGroup(
            InetAddress multicastAddress, NetworkInterface networkInterface,
            InetAddress source, ChannelPromise promise) {
        if (PlatformDependent.javaVersion() >= 7) {
            if (multicastAddress == null) {
                throw new NullPointerException("multicastAddress");
            }

            if (networkInterface == null) {
                throw new NullPointerException("networkInterface");
            }

            try {
                MembershipKey key;
                if (source == null) {
                    key = javaChannel().join(multicastAddress, networkInterface);
                } else {
                    key = javaChannel().join(multicastAddress, networkInterface, source);
                }

                synchronized (this) {
                    List<MembershipKey> keys = memberships.get(multicastAddress);
                    if (keys == null) {
                        keys = new ArrayList<MembershipKey>();
                        memberships.put(multicastAddress, keys);
                    }
                    keys.add(key);
                }

                promise.setSuccess();
            } catch (Throwable e) {
                promise.setFailure(e);
            }
        } else {
            throw new UnsupportedOperationException();
        }
        return promise;
    }

    @Override
    public ChannelFuture leaveGroup(InetAddress multicastAddress) {
        return leaveGroup(multicastAddress, newPromise());
    }

    @Override
    public ChannelFuture leaveGroup(InetAddress multicastAddress, ChannelPromise promise) {
        try {
            return leaveGroup(
                    multicastAddress, NetworkInterface.getByInetAddress(localAddress().getAddress()), null, promise);
        } catch (SocketException e) {
            promise.setFailure(e);
        }
        return promise;
    }

    @Override
    public ChannelFuture leaveGroup(
            InetSocketAddress multicastAddress, NetworkInterface networkInterface) {
        return leaveGroup(multicastAddress, networkInterface, newPromise());
    }

    @Override
    public ChannelFuture leaveGroup(
            InetSocketAddress multicastAddress,
            NetworkInterface networkInterface, ChannelPromise promise) {
        return leaveGroup(multicastAddress.getAddress(), networkInterface, null, promise);
    }

    @Override
    public ChannelFuture leaveGroup(
            InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress source) {
        return leaveGroup(multicastAddress, networkInterface, source, newPromise());
    }

    @Override
    public ChannelFuture leaveGroup(
            InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress source,
            ChannelPromise promise) {
        if (PlatformDependent.javaVersion() < 7) {
            throw new UnsupportedOperationException();
        }
        if (multicastAddress == null) {
            throw new NullPointerException("multicastAddress");
        }
        if (networkInterface == null) {
            throw new NullPointerException("networkInterface");
        }

        synchronized (this) {
            if (memberships != null) {
                List<MembershipKey> keys = memberships.get(multicastAddress);
                if (keys != null) {
                    Iterator<MembershipKey> keyIt = keys.iterator();

                    while (keyIt.hasNext()) {
                        MembershipKey key = keyIt.next();
                        if (networkInterface.equals(key.networkInterface())) {
                           if (source == null && key.sourceAddress() == null ||
                               source != null && source.equals(key.sourceAddress())) {
                               key.drop();
                               keyIt.remove();
                           }
                        }
                    }
                    if (keys.isEmpty()) {
                        memberships.remove(multicastAddress);
                    }
                }
            }
        }

        promise.setSuccess();
        return promise;
    }

    /**
     * Block the given sourceToBlock address for the given multicastAddress on the given networkInterface
     */
    @Override
    public ChannelFuture block(
            InetAddress multicastAddress, NetworkInterface networkInterface,
            InetAddress sourceToBlock) {
        return block(multicastAddress, networkInterface, sourceToBlock, newPromise());
    }

    /**
     * Block the given sourceToBlock address for the given multicastAddress on the given networkInterface
     */
    @Override
    public ChannelFuture block(
            InetAddress multicastAddress, NetworkInterface networkInterface,
            InetAddress sourceToBlock, ChannelPromise promise) {
        if (PlatformDependent.javaVersion() < 7) {
            throw new UnsupportedOperationException();
        } else {
            if (multicastAddress == null) {
                throw new NullPointerException("multicastAddress");
            }
            if (sourceToBlock == null) {
                throw new NullPointerException("sourceToBlock");
            }

            if (networkInterface == null) {
                throw new NullPointerException("networkInterface");
            }
            synchronized (this) {
                if (memberships != null) {
                    List<MembershipKey> keys = memberships.get(multicastAddress);
                    for (MembershipKey key: keys) {
                        if (networkInterface.equals(key.networkInterface())) {
                            try {
                                key.block(sourceToBlock);
                            } catch (IOException e) {
                                promise.setFailure(e);
                            }
                        }
                    }
                }
            }
            promise.setSuccess();
            return promise;
        }
    }

    /**
     * Block the given sourceToBlock address for the given multicastAddress
     *
     */
    @Override
    public ChannelFuture block(InetAddress multicastAddress, InetAddress sourceToBlock) {
        return block(multicastAddress, sourceToBlock, newPromise());
    }

    /**
     * Block the given sourceToBlock address for the given multicastAddress
     *
     */
    @Override
    public ChannelFuture block(
            InetAddress multicastAddress, InetAddress sourceToBlock, ChannelPromise promise) {
        try {
            return block(
                    multicastAddress,
                    NetworkInterface.getByInetAddress(localAddress().getAddress()),
                    sourceToBlock, promise);
        } catch (SocketException e) {
            promise.setFailure(e);
        }
        return promise;
    }
}
