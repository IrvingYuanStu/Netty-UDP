package com.irving.udp.channel;

import java.net.ProtocolFamily;
import java.net.StandardProtocolFamily;

import io.netty.channel.socket.InternetProtocolFamily;

public class ProtocolFamilyConverter {

    private ProtocolFamilyConverter() {
        // Utility class
    }

    /**
     * Convert the {@link InternetProtocolFamily}. This MUST only be called on jdk version >= 7.
     */
    public static ProtocolFamily convert(InternetProtocolFamily family) {
        switch (family) {
        case IPv4:
            return StandardProtocolFamily.INET;
        case IPv6:
            return StandardProtocolFamily.INET6;
        default:
            throw new IllegalArgumentException();
        }
    }
}
