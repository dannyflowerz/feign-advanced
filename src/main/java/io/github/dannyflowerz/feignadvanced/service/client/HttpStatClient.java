package io.github.dannyflowerz.feignadvanced.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import feign.Response;
import io.github.dannyflowerz.feignadvanced.domain.exception.ServiceInvocationException;

@FeignClient(name = "${httpStatClient.name}", url = "${httpStatClient.url}", fallback = HttpStatClient.HttpStatClientFallback.class)
public interface HttpStatClient {

    @Component
    class HttpStatClientFallback implements HttpStatClient {

        @Override
        public Response get200(Integer sleep) {
            throw new ServiceInvocationException("boo-hoo");
        }

        @Override
        public Response get400(Integer sleep) {
            throw new ServiceInvocationException("boo-hoo");
        }

    }

    @GetMapping(path = "/200")
    Response get200(@RequestParam(required = false) Integer sleep);

    @GetMapping(path = "/400")
    Response get400(@RequestParam(required = false) Integer sleep);

}
