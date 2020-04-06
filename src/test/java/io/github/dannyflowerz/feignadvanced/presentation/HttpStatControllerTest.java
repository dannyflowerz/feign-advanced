package io.github.dannyflowerz.feignadvanced.presentation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.nio.charset.Charset;
import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import feign.Request;
import feign.RequestTemplate;
import feign.Response;
import info.solidsoft.mockito.java8.api.WithBDDMockito;
import io.github.dannyflowerz.feignadvanced.service.client.HttpStatClient;

@WebMvcTest(controllers = {HttpStatController.class})
class HttpStatControllerTest implements WithBDDMockito {

    private static final String URL = "http://hero-service/heroes";
    private static final Integer STATUS_200 = 200;
    private static final String BODY = "{\"test\":\"test\"}";

    @MockBean
    private HttpStatClient httpStatClient;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("SHOULD return the response received from httpstat WHEN invoking GET /200")
    void get200() throws Exception {
        // given
        Response clientResponse = Response.builder()
                .status(STATUS_200)
                .body(BODY, Charset.defaultCharset())
                .request(createMockRequest())
                .build();
        given(httpStatClient.get200(null)).willReturn(clientResponse);

        // when
        MockHttpServletResponse response = mockMvc.perform(get("/200")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn()
                .getResponse();

        // then
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(BODY, response.getContentAsString());
        verify(httpStatClient).get200(null);
        verifyNoMoreInteractions(httpStatClient);
    }

    @Test
    @DisplayName("SHOULD return the response received from httpstat WHEN invoking GET /400")
    void get400() throws Exception {
        // given
        Response clientResponse = Response.builder()
                .status(STATUS_200)
                .body(BODY, Charset.defaultCharset())
                .request(createMockRequest())
                .build();
        given(httpStatClient.get400(null)).willReturn(clientResponse);

        // when
        MockHttpServletResponse response = mockMvc.perform(get("/400")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn()
                .getResponse();

        // then
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(BODY, response.getContentAsString());
        verify(httpStatClient).get400(null);
        verifyNoMoreInteractions(httpStatClient);
    }

    private Request createMockRequest() {
        return Request.create(Request.HttpMethod.GET, URL, Collections.emptyMap(), Request.Body.empty(), new RequestTemplate());
    }

}