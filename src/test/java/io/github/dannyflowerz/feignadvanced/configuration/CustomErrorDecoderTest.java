package io.github.dannyflowerz.feignadvanced.configuration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import feign.Request;
import feign.RequestTemplate;
import feign.Response;

class CustomErrorDecoderTest {

    private static final Integer STATUS_500 = 500;

    private CustomErrorDecoder underTest = new CustomErrorDecoder();

    @Test
    @DisplayName("SHOULD return ServiceInvocationException WHEN decoding response")
    void decode() {
        // given
        String url = "http://consumable-service/consumable-resource";
        Request request = Request.create(Request.HttpMethod.GET, url, Collections.emptyMap(), Request.Body.empty(), new RequestTemplate());
        Response response = Response.builder()
                .status(STATUS_500)
                .request(request)
                .build();

        // when
        Exception exception = underTest.decode("testKey", response);

        // then
        assertNotNull(exception);
        assertTrue(exception.getMessage().contains(Request.HttpMethod.GET.name()));
        assertTrue(exception.getMessage().contains(url));
        assertTrue(exception.getMessage().contains(STATUS_500.toString()));
    }

}