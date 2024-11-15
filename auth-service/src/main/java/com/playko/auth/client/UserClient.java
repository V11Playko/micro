package com.playko.auth.client;

import com.playko.auth.client.dto.User;
import com.playko.auth.client.interceptor.FeignClientInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "micro-service",
        url = "http://localhost:8091",
        configuration = FeignClientInterceptor.class)
public interface UserClient {
    @GetMapping("/public/getUserByEmail")
    User getUser(@RequestParam("correo") String email);
}
