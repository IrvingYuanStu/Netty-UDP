#!/bash
./protoc --proto_path=../src/main/resource/ --java_out=../src/main/java ../src/main/resource/sign_up_req_proto.proto
./protoc --proto_path=../src/main/resource/ --java_out=../src/main/java ../src/main/resource/sign_up_resp_proto.proto