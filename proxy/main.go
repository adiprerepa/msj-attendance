package main

import (
  "context"
  "flag"
  "fmt"
  "github.com/golang/glog"
  "github.com/grpc-ecosystem/grpc-gateway/runtime"
  "google.golang.org/grpc"
  "net/http"
)

import gw "attendance_gateway"

var (
  grpcServerEndpoint = flag.String("attendance-server-endpoint", "localhost:2000", "Attendance gRPC service implementation")
)

func run() error {
  ctx := context.Background()
  ctx, cancel := context.WithCancel(ctx)
  defer cancel()

  // Register Attendance gRPC service with provider
  mux := runtime.NewServeMux()
  opts := []grpc.DialOption{grpc.WithInsecure()}
  err := gw.RegisterStudentRecordsServiceHandlerFromEndpoint(ctx, mux, *grpcServerEndpoint, opts)
  if err != nil {
    return err
  }
  return http.ListenAndServe(":2005", mux)
}

func main() {
  fmt.Println("Attempting to start Attendance grpc-gateway proxy")
  defer glog.Flush()
  err := run()
  if err != nil {
    glog.Fatal(err)
  }
}
