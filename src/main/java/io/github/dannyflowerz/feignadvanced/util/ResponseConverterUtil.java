package io.github.dannyflowerz.feignadvanced.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;

import feign.Response;

/**
 * Utility class to help convert Feign Response objects to Spring ResponseEntity objects
 */
public final class ResponseConverterUtil {

    private ResponseConverterUtil() {}

    /**
     * Converts a {@link Response} object to a {@link ResponseEntity} object
     *
     * @param feignResponse the Feign response to convert
     * @return the converted Spring response
     */
    public static ResponseEntity<String> convertToResponseEntity(Response feignResponse) {
        Map<String, List<String>> feignResponseHeaders = new HashMap<>();
        feignResponse.headers().forEach((key, value) -> feignResponseHeaders.put(key, List.copyOf(value)));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.putAll(feignResponseHeaders);

        try {
            return ResponseEntity.status(feignResponse.status())
                    .headers(httpHeaders)
                    .body(StreamUtils.copyToString(feignResponse.body().asInputStream(), Charset.defaultCharset()));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
