package com.odde.atddv2;

import io.cucumber.java.Before;
import lombok.SneakyThrows;
import org.mockserver.client.MockServerClient;
import org.springframework.beans.factory.annotation.Value;

import java.net.URL;

public class MockServer {
    private MockServerClient mockServerClient;

    @Value("${mock-server.endpoint}")
    private String endpoint;


    @Before(order = 0)
    @SneakyThrows
    public void resetMockServer() {
        mockServerClient = createMockServerClient(new URL(endpoint));
        mockServerClient.reset();
    }

    private MockServerClient createMockServerClient(URL url) {
        return mockServerClient = new MockServerClient(url.getHost(), url.getPort()) {
            @Override
            public void close() {
            }
        };
    }
}