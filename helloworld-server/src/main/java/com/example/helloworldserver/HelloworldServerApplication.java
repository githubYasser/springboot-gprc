package com.example.helloworldserver;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class HelloworldServerApplication {

    private final int port;
    private final Server server;

    public static void main(String[] args) throws IOException, InterruptedException {
        //SpringApplication.run(HelloworldServerApplication.class, args);
        HelloworldServerApplication server = new HelloworldServerApplication(8980, ServerBuilder.forPort(8980));
        server.start();
        server.blockUntilShutdown();
    }

    public HelloworldServerApplication(int port, ServerBuilder<?> serverBuilder) throws IOException {
        this.port = port;
        server = serverBuilder.addService(new GreetingService()).build();
    }

    /**
     * Start serving requests.
     */
    public void start() throws IOException {
        server.start();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may has been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                HelloworldServerApplication.this.stop();
                System.err.println("*** server shut down");
            }
        });
    }

    /**
     * Stop serving requests and shutdown resources.
     */
    public void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

}
