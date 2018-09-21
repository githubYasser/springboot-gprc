package com.example.helloworldclient;

import com.example.demo.GreeterGrpc;
import com.example.demo.HelloReply;
import com.example.demo.HelloRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@SpringBootApplication
public class HelloworldClientApplication {


    private static final Logger logger = Logger.getLogger(HelloworldClientApplication.class.getName());

    private final ManagedChannel channel;
    private GreeterGrpc.GreeterBlockingStub blockingStub;


    /**
     * Construct client for accessing RouteGuide server at {@code host:port}.
     */
    public HelloworldClientApplication(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port).usePlaintext());
    }

    /**
     * Construct client for accessing RouteGuide server using the existing channel.
     */
    public HelloworldClientApplication(ManagedChannelBuilder<?> channelBuilder) {
        channel = channelBuilder.build();
        blockingStub = GreeterGrpc.newBlockingStub(channel);

    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }


    public void callSayHello() {
        HelloRequest request = HelloRequest.newBuilder().setName("grpccccccccc callllllllllllllllll").build();
        HelloReply response;
        try {
            response = blockingStub.sayHello(request);
            logger.info(String.valueOf(response));
        } catch (StatusRuntimeException e) {
            return;
        }

    }

    /**
     * Issues several different requests and then exits.
     */
    public static void main(String[] args) throws InterruptedException {

        HelloworldClientApplication client = new HelloworldClientApplication("localhost", 8980);
        try {
            client.callSayHello();

        } finally {
            client.shutdown();
        }

    }


}
