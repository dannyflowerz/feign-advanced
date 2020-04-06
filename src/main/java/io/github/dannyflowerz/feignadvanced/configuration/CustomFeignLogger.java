package io.github.dannyflowerz.feignadvanced.configuration;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Objects;

import org.springframework.http.HttpStatus;

import feign.Logger;
import feign.Request;
import feign.Response;
import feign.Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomFeignLogger extends Logger {

    @Override
    protected void log(String configKey, String format, Object... args) {
        // No action required
    }

    @Override
    protected void logRequest(String configKey, Level logLevel, Request request) {
        if (log.isDebugEnabled()) {
            log.debug("Request ---> Method: {}, URI: {}, Headers: {}", request.httpMethod().name(), request.url(), request.headers());
        } else {
            log.info("Request ---> Method: {}, URI: {}", request.httpMethod().name(), request.url());
        }
    }

    @Override
    protected Response logAndRebufferResponse(String configKey, Level logLevel, Response response, long elapsedTime) throws IOException {
        HttpStatus responseStatus = Objects.requireNonNull(HttpStatus.resolve(response.status()), "Unable to interpret response status");
        Response.Builder responseBuilder = response.toBuilder();
        if (responseStatus.isError()) {
            byte[] bodyData = Util.toByteArray(response.body().asInputStream());
            if (log.isDebugEnabled()) {
                log.debug("Response <--({}ms)-- Status: {}, Headers {}, Body: {}", elapsedTime, responseStatus.toString(), response.headers(),
                        new String(bodyData, Charset.defaultCharset()));
            } else {
                log.info("Response <--({}ms)-- Status: {}, Body: {}", elapsedTime, responseStatus.toString(),
                        new String(bodyData, Charset.defaultCharset()));
            }
            responseBuilder.body(bodyData);
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Response <--({}ms)-- Status: {}, Headers {}", elapsedTime, responseStatus.toString(), response.headers());
            } else {
                log.info("Response <--({}ms)-- Status: {}", elapsedTime, responseStatus.toString());
            }
        }
        return responseBuilder.build();
    }

}
