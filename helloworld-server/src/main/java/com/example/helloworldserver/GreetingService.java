package com.example.helloworldserver;

import com.example.demo.GreeterGrpc;
import com.example.demo.HelloReply;
import com.example.demo.HelloRequest;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component()
public class GreetingService extends GreeterGrpc.GreeterImplBase {

    private static final Logger LOG = LoggerFactory.getLogger(GreetingService.class);

    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        LOG.debug("sayHello endpoint received request from " + request.getName());
        HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + request.getName()).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    @Override
    public void sayHelloServerStream(HelloRequest request, StreamObserver<HelloReply> responseObserver) {

    }

    @Override
    public io.grpc.stub.StreamObserver<HelloRequest> sayHelloClientStream(StreamObserver<HelloReply> responseObserver) {
        return null;
    }

    @Override
    public io.grpc.stub.StreamObserver<HelloRequest> sayHelloBidirectionalStream(StreamObserver<HelloReply> responseObserver) {
        return null;
    }

}
