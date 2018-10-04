package com.example.helloworldserver;

import com.google.common.collect.Lists;
import io.grpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;

public class CertificateInterceptor implements ServerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(CertificateInterceptor.class);

    public final static Context.Key<SSLSession> SSL_SESSION_CONTEXT = Context.key("SSLSession");

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
        SSLSession sslSession = call.getAttributes().get(Grpc.TRANSPORT_ATTR_SSL_SESSION);
        if (sslSession == null) {
            throw Status.UNAUTHENTICATED.withDescription("wrong key").asRuntimeException();
        }
        try {
            List<Certificate> certificates = Lists.newArrayList();
            certificates = Arrays.asList(sslSession.getPeerCertificates());
            X509Certificate x509cert = (X509Certificate) certificates.get(0);

            //Certificate[]  localCertificates = sslSession.getLocalCertificates();
            //X509Certificate[]  peerCertificate = sslSession.getPeerCertificateChain();
        } catch (SSLPeerUnverifiedException e) {
            logger.debug(e.getMessage());
        }
        return next.startCall(call, headers);
    }
}
