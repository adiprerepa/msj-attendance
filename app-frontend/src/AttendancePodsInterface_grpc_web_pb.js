/**
 * @fileoverview gRPC-Web generated client stub for 
 * @enhanceable
 * @public
 */

// GENERATED CODE -- DO NOT EDIT!


/* eslint-disable */
// @ts-nocheck



const grpc = {};
grpc.web = require('grpc-web');

const proto = require('./AttendancePodsInterface_pb.js');

/**
 * @param {string} hostname
 * @param {?Object} credentials
 * @param {?Object} options
 * @constructor
 * @struct
 * @final
 */
proto.StudentRecordsServiceClient =
    function(hostname, credentials, options) {
  if (!options) options = {};
  options['format'] = 'text';

  /**
   * @private @const {!grpc.web.GrpcWebClientBase} The client
   */
  this.client_ = new grpc.web.GrpcWebClientBase(options);

  /**
   * @private @const {string} The hostname
   */
  this.hostname_ = hostname;

};


/**
 * @param {string} hostname
 * @param {?Object} credentials
 * @param {?Object} options
 * @constructor
 * @struct
 * @final
 */
proto.StudentRecordsServicePromiseClient =
    function(hostname, credentials, options) {
  if (!options) options = {};
  options['format'] = 'text';

  /**
   * @private @const {!grpc.web.GrpcWebClientBase} The client
   */
  this.client_ = new grpc.web.GrpcWebClientBase(options);

  /**
   * @private @const {string} The hostname
   */
  this.hostname_ = hostname;

};


/**
 * @const
 * @type {!grpc.web.MethodDescriptor<
 *   !proto.RecordsRequest,
 *   !proto.RecordsResponse>}
 */
const methodDescriptor_StudentRecordsService_getPeriodRecords = new grpc.web.MethodDescriptor(
  '/StudentRecordsService/getPeriodRecords',
  grpc.web.MethodType.UNARY,
  proto.RecordsRequest,
  proto.RecordsResponse,
  /**
   * @param {!proto.RecordsRequest} request
   * @return {!Uint8Array}
   */
  function(request) {
    return request.serializeBinary();
  },
  proto.RecordsResponse.deserializeBinary
);


/**
 * @const
 * @type {!grpc.web.AbstractClientBase.MethodInfo<
 *   !proto.RecordsRequest,
 *   !proto.RecordsResponse>}
 */
const methodInfo_StudentRecordsService_getPeriodRecords = new grpc.web.AbstractClientBase.MethodInfo(
  proto.RecordsResponse,
  /**
   * @param {!proto.RecordsRequest} request
   * @return {!Uint8Array}
   */
  function(request) {
    return request.serializeBinary();
  },
  proto.RecordsResponse.deserializeBinary
);


/**
 * @param {!proto.RecordsRequest} request The
 *     request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @param {function(?grpc.web.Error, ?proto.RecordsResponse)}
 *     callback The callback function(error, response)
 * @return {!grpc.web.ClientReadableStream<!proto.RecordsResponse>|undefined}
 *     The XHR Node Readable Stream
 */
proto.StudentRecordsServiceClient.prototype.getPeriodRecords =
    function(request, metadata, callback) {
  return this.client_.rpcCall(this.hostname_ +
      '/StudentRecordsService/getPeriodRecords',
      request,
      metadata || {},
      methodDescriptor_StudentRecordsService_getPeriodRecords,
      callback);
};


/**
 * @param {!proto.RecordsRequest} request The
 *     request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @return {!Promise<!proto.RecordsResponse>}
 *     A native promise that resolves to the response
 */
proto.StudentRecordsServicePromiseClient.prototype.getPeriodRecords =
    function(request, metadata) {
  return this.client_.unaryCall(this.hostname_ +
      '/StudentRecordsService/getPeriodRecords',
      request,
      metadata || {},
      methodDescriptor_StudentRecordsService_getPeriodRecords);
};


/**
 * @const
 * @type {!grpc.web.MethodDescriptor<
 *   !proto.RegisterRoomRequest,
 *   !proto.RegisterRoomResponse>}
 */
const methodDescriptor_StudentRecordsService_registerRoom = new grpc.web.MethodDescriptor(
  '/StudentRecordsService/registerRoom',
  grpc.web.MethodType.UNARY,
  proto.RegisterRoomRequest,
  proto.RegisterRoomResponse,
  /**
   * @param {!proto.RegisterRoomRequest} request
   * @return {!Uint8Array}
   */
  function(request) {
    return request.serializeBinary();
  },
  proto.RegisterRoomResponse.deserializeBinary
);


/**
 * @const
 * @type {!grpc.web.AbstractClientBase.MethodInfo<
 *   !proto.RegisterRoomRequest,
 *   !proto.RegisterRoomResponse>}
 */
const methodInfo_StudentRecordsService_registerRoom = new grpc.web.AbstractClientBase.MethodInfo(
  proto.RegisterRoomResponse,
  /**
   * @param {!proto.RegisterRoomRequest} request
   * @return {!Uint8Array}
   */
  function(request) {
    return request.serializeBinary();
  },
  proto.RegisterRoomResponse.deserializeBinary
);


/**
 * @param {!proto.RegisterRoomRequest} request The
 *     request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @param {function(?grpc.web.Error, ?proto.RegisterRoomResponse)}
 *     callback The callback function(error, response)
 * @return {!grpc.web.ClientReadableStream<!proto.RegisterRoomResponse>|undefined}
 *     The XHR Node Readable Stream
 */
proto.StudentRecordsServiceClient.prototype.registerRoom =
    function(request, metadata, callback) {
  return this.client_.rpcCall(this.hostname_ +
      '/StudentRecordsService/registerRoom',
      request,
      metadata || {},
      methodDescriptor_StudentRecordsService_registerRoom,
      callback);
};


/**
 * @param {!proto.RegisterRoomRequest} request The
 *     request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @return {!Promise<!proto.RegisterRoomResponse>}
 *     A native promise that resolves to the response
 */
proto.StudentRecordsServicePromiseClient.prototype.registerRoom =
    function(request, metadata) {
  return this.client_.unaryCall(this.hostname_ +
      '/StudentRecordsService/registerRoom',
      request,
      metadata || {},
      methodDescriptor_StudentRecordsService_registerRoom);
};


module.exports = proto;

