package com.example.helloworldserver;

import io.grpc.Server;
import io.grpc.ServerInterceptors;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.ClientAuth;
import io.grpc.netty.shaded.io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;

@SpringBootApplication
public class HelloworldServerApplication {

    private final int port;
    private final Server server;

    public static void main(String[] args) throws IOException, InterruptedException {
        // Without authentication
        //HelloworldServerApplication server = new HelloworldServerApplication(8980, ServerBuilder.forPort(8980));

        // with mtls authentication
        String caChain = "ca-chain.cert.pem";
        String serverCertificate = "localhost.cert.pem";
        String serverPrivateKey = "pkcs8_key.pem";

        ClassLoader classLoader = ClassLoader.getSystemClassLoader();

        File caChainFile = new File(classLoader.getResource(caChain).getFile());
        File serverCertificateFile = new File(classLoader.getResource(serverCertificate).getFile());
        File serverPrivateKeyFile = new File(classLoader.getResource(serverPrivateKey).getFile());

        HelloworldServerApplication server = new HelloworldServerApplication(8980, NettyServerBuilder.forPort(8980), serverCertificateFile, serverPrivateKeyFile, caChainFile);
        server.start();
        server.blockUntilShutdown();
    }

    // Without authentication

    /**
     * public HelloworldServerApplication(int port, ServerBuilder<?> serverBuilder) throws IOException {
     * this.port = port;
     * server = serverBuilder.addService(new GreetingService()).build();
     * }
     **/

    // With mtls authentication
    public HelloworldServerApplication(int port, NettyServerBuilder nettyServerBuilder, File certChainFile, File privateKeyFile, File clientCertChainFile) throws IOException {
        this.port = port;
        server = nettyServerBuilder.sslContext(GrpcSslContexts.forServer(certChainFile, privateKeyFile)
                //.trustManager(clientCertChainFile) only if u want to have certificate validation.
                .trustManager(InsecureTrustManagerFactory.INSTANCE) // disable certificate validation
                .clientAuth(ClientAuth.REQUIRE)
                .build()).addService(ServerInterceptors.intercept(new GreetingService(), new CertificateInterceptor())).build();

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
