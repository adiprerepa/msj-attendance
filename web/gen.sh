protoc -I=. AttendancePodsInterface.proto --js_out=import_style=commonjs:.
protoc -I=. AttendancePodsInterface.proto --grpc-web_out=import_style=commonjs,mode=grpcwebtext:.