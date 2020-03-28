package io.github.dannyflowerz.feignadvanced.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.dannyflowerz.feignadvanced.service.client.HttpStatClient;

@Service
class PingServiceImpl implements PingService {

    private final HttpStatClient httpStatClient;

    @Autowired
    public PingServiceImpl(HttpStatClient httpStatClient) {
        this.httpStatClient = httpStatClient;
    }

    @Override
    public void get200() {
        httpStatClient.get200(100);
        httpStatClient.get200(2000);
    }

    @Override
    public void get400() {
        httpStatClient.get400();
    }

}
