package io.github.dannyflowerz.feignadvanced.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.Charset;
import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import feign.Request;
import feign.RequestTemplate;
import feign.Response;

class ResponseConverterUtilTest {

    private static final Integer STATUS_200 = 200;
    private static final String BODY = "test";

    @Test
    @DisplayName("SHOULD convert Feign response to Spring response")
    void convertToResponseEntity() {
        // given
        Request request = Request.create(Request.HttpMethod.GET, "http://consumable-service/consumable-resource", Collections.emptyMap(), Request.Body.empty(), new RequestTemplate());
        Response response = Response.builder()
                .status(STATUS_200)
                .headers(Collections.singletonMap(HttpHeaders.CONTENT_TYPE, Collections.singletonList(MediaType.APPLICATION_JSON_VALUE)))
                .body(BODY, Charset.defaultCharset())
                .request(request)
                .build();

        // when
        ResponseEntity<String> responseEntity = ResponseConverterUtil.convertToResponseEntity(response);

        // then
        assertEquals(STATUS_200, responseEntity.getStatusCodeValue());
        assertTrue(responseEntity.getHeaders().containsKey(HttpHeaders.CONTENT_TYPE));
        assertEquals(MediaType.APPLICATION_JSON_VALUE, responseEntity.getHeaders().get(HttpHeaders.CONTENT_TYPE).get(0));
        assertEquals(BODY, responseEntity.getBody());
    }

}