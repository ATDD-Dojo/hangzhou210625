package com.odde.atddv2;

import com.odde.atddv2.entity.User;
import com.odde.atddv2.repo.UserRepo;
import io.cucumber.java.Before;
import io.cucumber.java.zh_cn.当;
import io.cucumber.java.zh_cn.那么;
import lombok.SneakyThrows;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

public class ApiSteps {
    private final RestTemplate restTemplate = new RestTemplate();
    private String token;
    private String res;

    @Autowired
    private ServerProperties serverProperties;

    @Autowired
    private UserRepo userRepo;

    @Before("@api-login")
    public void apiLogin() {
        userRepo.deleteAll();
        User defaultUser = new User().setUserName("j").setPassword("j");
        userRepo.save(defaultUser);
        token = restTemplate.postForEntity(makeUri("/users/login"), defaultUser, User.class)
                .getHeaders().get("token").get(0);
    }

    @SneakyThrows
    @那么("返回如下订单")
    public void 返回如下订单(String msg) {
        JSONAssert.assertEquals(msg, res, false);
    }

    @SneakyThrows
    private URI makeUri(String path) {
        return URI.create(String.format("http://127.0.0.1:%s%s", serverProperties.getPort(), path));
    }

    @当("API查询订单时")
    public void api查询订单时() throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("token",token);

        HttpEntity entity = new HttpEntity(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:10081/api/orders", HttpMethod.GET, entity, String.class);

        res = response.getBody();
        System.out.println(response.getBody());
    }
}
