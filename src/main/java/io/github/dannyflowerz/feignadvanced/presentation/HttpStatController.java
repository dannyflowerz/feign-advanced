package io.github.dannyflowerz.feignadvanced.presentation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import feign.Response;
import io.github.dannyflowerz.feignadvanced.service.client.HttpStatClient;
import io.github.dannyflowerz.feignadvanced.util.ResponseConverterUtil;

@RestController
class HttpStatController {

    private final HttpStatClient httpStatClient;

    @Autowired
    public HttpStatController(HttpStatClient httpStatClient) {
        this.httpStatClient = httpStatClient;
    }

    @GetMapping(path = "/200")
    public ResponseEntity<String> get200(@RequestParam(required = false) Integer sleep) {
        Response clientResponse = httpStatClient.get200(sleep);
        return ResponseConverterUtil.convertToResponseEntity(clientResponse);
    }

    @GetMapping(path = "/400")
    public ResponseEntity<String> get400(@RequestParam(required = false) Integer sleep) {
        Response clientResponse = httpStatClient.get400(sleep);
        return ResponseConverterUtil.convertToResponseEntity(clientResponse);
    }

}
