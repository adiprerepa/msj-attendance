package com.msj.generated;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.20.0)",
    comments = "Source: AttendancePodsInterface.proto")
public final class StudentRecordsServiceGrpc {

  private StudentRecordsServiceGrpc() {}

  public static final String SERVICE_NAME = "StudentRecordsService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.msj.generated.RecordsRequest,
      com.msj.generated.RecordsResponse> getGetPeriodRecordsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "getPeriodRecords",
      requestType = com.msj.generated.RecordsRequest.class,
      responseType = com.msj.generated.RecordsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.msj.generated.RecordsRequest,
      com.msj.generated.RecordsResponse> getGetPeriodRecordsMethod() {
    io.grpc.MethodDescriptor<com.msj.generated.RecordsRequest, com.msj.generated.RecordsResponse> getGetPeriodRecordsMethod;
    if ((getGetPeriodRecordsMethod = StudentRecordsServiceGrpc.getGetPeriodRecordsMethod) == null) {
      synchronized (StudentRecordsServiceGrpc.class) {
        if ((getGetPeriodRecordsMethod = StudentRecordsServiceGrpc.getGetPeriodRecordsMethod) == null) {
          StudentRecordsServiceGrpc.getGetPeriodRecordsMethod = getGetPeriodRecordsMethod = 
              io.grpc.MethodDescriptor.<com.msj.generated.RecordsRequest, com.msj.generated.RecordsResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "StudentRecordsService", "getPeriodRecords"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.msj.generated.RecordsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.msj.generated.RecordsResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new StudentRecordsServiceMethodDescriptorSupplier("getPeriodRecords"))
                  .build();
          }
        }
     }
     return getGetPeriodRecordsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.msj.generated.RegisterRoomRequest,
      com.msj.generated.RegisterRoomResponse> getRegisterRoomMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "registerRoom",
      requestType = com.msj.generated.RegisterRoomRequest.class,
      responseType = com.msj.generated.RegisterRoomResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.msj.generated.RegisterRoomRequest,
      com.msj.generated.RegisterRoomResponse> getRegisterRoomMethod() {
    io.grpc.MethodDescriptor<com.msj.generated.RegisterRoomRequest, com.msj.generated.RegisterRoomResponse> getRegisterRoomMethod;
    if ((getRegisterRoomMethod = StudentRecordsServiceGrpc.getRegisterRoomMethod) == null) {
      synchronized (StudentRecordsServiceGrpc.class) {
        if ((getRegisterRoomMethod = StudentRecordsServiceGrpc.getRegisterRoomMethod) == null) {
          StudentRecordsServiceGrpc.getRegisterRoomMethod = getRegisterRoomMethod = 
              io.grpc.MethodDescriptor.<com.msj.generated.RegisterRoomRequest, com.msj.generated.RegisterRoomResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "StudentRecordsService", "registerRoom"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.msj.generated.RegisterRoomRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.msj.generated.RegisterRoomResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new StudentRecordsServiceMethodDescriptorSupplier("registerRoom"))
                  .build();
          }
        }
     }
     return getRegisterRoomMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static StudentRecordsServiceStub newStub(io.grpc.Channel channel) {
    return new StudentRecordsServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static StudentRecordsServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new StudentRecordsServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static StudentRecordsServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new StudentRecordsServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class StudentRecordsServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void getPeriodRecords(com.msj.generated.RecordsRequest request,
        io.grpc.stub.StreamObserver<com.msj.generated.RecordsResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getGetPeriodRecordsMethod(), responseObserver);
    }

    /**
     * <pre>
     * todo 3/16 implement
     * </pre>
     */
    public void registerRoom(com.msj.generated.RegisterRoomRequest request,
        io.grpc.stub.StreamObserver<com.msj.generated.RegisterRoomResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getRegisterRoomMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getGetPeriodRecordsMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.msj.generated.RecordsRequest,
                com.msj.generated.RecordsResponse>(
                  this, METHODID_GET_PERIOD_RECORDS)))
          .addMethod(
            getRegisterRoomMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.msj.generated.RegisterRoomRequest,
                com.msj.generated.RegisterRoomResponse>(
                  this, METHODID_REGISTER_ROOM)))
          .build();
    }
  }

  /**
   */
  public static final class StudentRecordsServiceStub extends io.grpc.stub.AbstractStub<StudentRecordsServiceStub> {
    private StudentRecordsServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private StudentRecordsServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected StudentRecordsServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new StudentRecordsServiceStub(channel, callOptions);
    }

    /**
     */
    public void getPeriodRecords(com.msj.generated.RecordsRequest request,
        io.grpc.stub.StreamObserver<com.msj.generated.RecordsResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetPeriodRecordsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * todo 3/16 implement
     * </pre>
     */
    public void registerRoom(com.msj.generated.RegisterRoomRequest request,
        io.grpc.stub.StreamObserver<com.msj.generated.RegisterRoomResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRegisterRoomMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class StudentRecordsServiceBlockingStub extends io.grpc.stub.AbstractStub<StudentRecordsServiceBlockingStub> {
    private StudentRecordsServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private StudentRecordsServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected StudentRecordsServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new StudentRecordsServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.msj.generated.RecordsResponse getPeriodRecords(com.msj.generated.RecordsRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetPeriodRecordsMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * todo 3/16 implement
     * </pre>
     */
    public com.msj.generated.RegisterRoomResponse registerRoom(com.msj.generated.RegisterRoomRequest request) {
      return blockingUnaryCall(
          getChannel(), getRegisterRoomMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class StudentRecordsServiceFutureStub extends io.grpc.stub.AbstractStub<StudentRecordsServiceFutureStub> {
    private StudentRecordsServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private StudentRecordsServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected StudentRecordsServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new StudentRecordsServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.msj.generated.RecordsResponse> getPeriodRecords(
        com.msj.generated.RecordsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetPeriodRecordsMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * todo 3/16 implement
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.msj.generated.RegisterRoomResponse> registerRoom(
        com.msj.generated.RegisterRoomRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getRegisterRoomMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_PERIOD_RECORDS = 0;
  private static final int METHODID_REGISTER_ROOM = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final StudentRecordsServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(StudentRecordsServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_PERIOD_RECORDS:
          serviceImpl.getPeriodRecords((com.msj.generated.RecordsRequest) request,
              (io.grpc.stub.StreamObserver<com.msj.generated.RecordsResponse>) responseObserver);
          break;
        case METHODID_REGISTER_ROOM:
          serviceImpl.registerRoom((com.msj.generated.RegisterRoomRequest) request,
              (io.grpc.stub.StreamObserver<com.msj.generated.RegisterRoomResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class StudentRecordsServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    StudentRecordsServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.msj.generated.AttendancePods.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("StudentRecordsService");
    }
  }

  private static final class StudentRecordsServiceFileDescriptorSupplier
      extends StudentRecordsServiceBaseDescriptorSupplier {
    StudentRecordsServiceFileDescriptorSupplier() {}
  }

  private static final class StudentRecordsServiceMethodDescriptorSupplier
      extends StudentRecordsServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    StudentRecordsServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (StudentRecordsServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new StudentRecordsServiceFileDescriptorSupplier())
              .addMethod(getGetPeriodRecordsMethod())
              .addMethod(getRegisterRoomMethod())
              .build();
        }
      }
    }
    return result;
  }
}
