package io.github.dannyflowerz.feignadvanced.configuration;

import feign.Response;
import feign.codec.ErrorDecoder;
import io.github.dannyflowerz.feignadvanced.domain.exception.ServiceInvocationException;

public class CustomErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        return new ServiceInvocationException("Received a " + response.status() + " response from: "
                + response.request().httpMethod().name() + " " + response.request().url());
    }

}
