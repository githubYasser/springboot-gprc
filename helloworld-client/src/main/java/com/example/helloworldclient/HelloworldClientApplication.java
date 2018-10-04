package com.example.helloworldclient;

import com.example.demo.GreeterGrpc;
import com.example.demo.HelloReply;
import com.example.demo.HelloRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.net.ssl.SSLException;
import java.io.File;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class HelloworldClientApplication {


    private static final Logger logger = LoggerFactory.getLogger(HelloworldClientApplication.class);

    private final ManagedChannel channel;
    private GreeterGrpc.GreeterBlockingStub blockingStub;


    // Without authentication
    /**public HelloworldClientApplication(String host, int port) {
        channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        blockingStub = GreeterGrpc.newBlockingStub(channel);
    }**/

    // with mtls
    public HelloworldClientApplication(String host, int port, File trustCertCollectionFilePath, File certChainFilePath, File privateKeyFilePath) throws SSLException {
        channel = NettyChannelBuilder.forAddress(host, port).sslContext(GrpcSslContexts.forClient()
                //.trustManager(trustCertCollectionFilePath)
                .trustManager(InsecureTrustManagerFactory.INSTANCE) // disable certificate validation
                .keyManager(certChainFilePath, privateKeyFilePath).build()).build();
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
            logger.debug(String.valueOf(response));
        } catch (StatusRuntimeException e) {
            return;
        }

    }

    /**
     * Issues several different requests and then exits.
     */
    public static void main(String[] args) throws InterruptedException {

        String caChain = "ca-chain.cert.pem";
        String clientCertificate = "localhost.cert.pem";
        String clientPrivateKey = "pkcs8_key.pem";

        ClassLoader classLoader = ClassLoader.getSystemClassLoader();

        File caChainFile = new File(classLoader.getResource(caChain).getFile());
        File clientCertificateFile = new File(classLoader.getResource(clientCertificate).getFile());
        File clientPrivateKeyFile = new File(classLoader.getResource(clientPrivateKey).getFile());

        HelloworldClientApplication client = null;
        try {
            client = new HelloworldClientApplication("localhost", 8980, caChainFile, clientCertificateFile, clientPrivateKeyFile);
        } catch (SSLException e) {
            logger.info(e.getMessage());
        }
        try {
            client.callSayHello();

        } finally {
            client.shutdown();
        }

    }


}
