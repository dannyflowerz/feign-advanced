package io.github.dannyflowerz.feignadvanced.configuration;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import feign.Client;
import feign.Logger;
import feign.codec.ErrorDecoder;
import feign.hystrix.HystrixFeign;
import io.github.dannyflowerz.feignadvanced.domain.exception.KeyStoreInitializationException;

@Configuration
public class FeignClientConfiguration {

    @Value("${feign.clients.withTls:false}")
    private boolean withTls;
    @Value("${feign.secrets.trustStoreResource:}")
    private String trustStoreResource;
    @Value("${feign.secrets.keyStoreResource:}")
    private String keyStoreResource;
    @Value("${feign.secrets.keyStorePassword:}")
    private String keyStorePassword;

    @Bean
    HystrixFeign.Builder customFeignBuilder() {
        HystrixFeign.Builder builder = HystrixFeign.builder();
        if (withTls) {
            Client client = new Client.Default(getClientSSLSocketFactory(), null);
            builder.client(client);
        }
        return builder;
    }

    @Bean
    Logger customFeignLogger() {
        return new CustomFeignLogger();
    }

    @Bean
    Logger.Level customFeignLoggerLevel() {
        return Logger.Level.BASIC;
    }

    @Bean
    ErrorDecoder customErrorDecoder() {
        return new CustomErrorDecoder();
    }

    protected SSLSocketFactory getClientSSLSocketFactory() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");

            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(this.getClass().getResourceAsStream(trustStoreResource), null);
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);

            KeyManager[] keyManagers = getKeyManagers();

            sslContext.init(keyManagers, trustManagerFactory.getTrustManagers(), null);
            return sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException | UnrecoverableKeyException | CertificateException | IOException e) {
            throw new KeyStoreInitializationException("Unable to load TLS configuration for the Feign client", e);
        }
    }

    protected KeyManager[] getKeyManagers()
            throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, IOException, CertificateException {
        if (StringUtils.isEmpty(keyStoreResource)) {
            return new KeyManager[]{};
        }
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(this.getClass().getResourceAsStream(keyStoreResource), keyStorePassword.toCharArray());
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, keyStorePassword.toCharArray());
        return keyManagerFactory.getKeyManagers();
    }

}
