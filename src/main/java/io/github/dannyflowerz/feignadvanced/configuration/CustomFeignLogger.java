package io.github.dannyflowerz.feignadvanced.configuration;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.util.StreamUtils;

import feign.Logger;
import feign.Request;
import feign.Response;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomFeignLogger extends Logger {

    @Override
    protected void log(String configKey, String format, Object... args) {
        // NOOP
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
        if (responseStatus.isError()) {
            if (log.isDebugEnabled()) {
                log.debug("Response <--({}ms)-- Status: {}, Headers {}, Body: {}", elapsedTime, responseStatus.toString(),
                        response.headers(), StreamUtils.copyToString(response.body().asInputStream(), Charset.defaultCharset()));
            } else {
                log.info("Response <--({}ms)-- Status: {}, Body: {}", elapsedTime, responseStatus.toString(),
                        StreamUtils.copyToString(response.body().asInputStream(), Charset.defaultCharset()));
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Response <--({}ms)-- Status: {}, Headers {}", elapsedTime, responseStatus.toString(), response.headers());
            } else {
                log.info("Response <--({}ms)-- Status: {}", elapsedTime, responseStatus.toString());
            }
        }
        return response;
    }

}
