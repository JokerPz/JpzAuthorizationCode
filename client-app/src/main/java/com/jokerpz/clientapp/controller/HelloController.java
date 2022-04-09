package com.jokerpz.clientapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Controller
public class HelloController {

    @Autowired
    RestTemplate restTemplate;

    @GetMapping("/index.html")
    public String index(String code, Model model) {
        if (code != null) {
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("code", code);
            map.add("client_id", "joker");
            map.add("client_secret", "1");
            map.add("redirect_uri", "http://localhost:8082/index.html");
            map.add("grant_type", "authorization_code");
            Map<String, String> resp = restTemplate.postForObject("http://localhost:8080/oauth/token", map, Map.class);
            System.out.println("resp = " + resp);

            HttpHeaders header = new HttpHeaders();
            header.add("Authorization", "Bearer" + resp.get("access_token"));
            HttpEntity<?> httpEntity = new HttpEntity<>(header);
            ResponseEntity<String> responseEntity = restTemplate.exchange("http://localhost:8081/hello", HttpMethod.GET, httpEntity, String.class);
            model.addAttribute("msg", responseEntity.getBody());
        }
        return "index";
    }

    @PostMapping("/login")
    public String login(String username, String password, Model model) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("username", username);
        map.add("password", password);
        map.add("client_secret", "1");
        map.add("client_id", "joker");
        map.add("grant_type", "password");
        Map<String, String> resp = restTemplate.postForObject("http://localhost:8084/oauth/token", map, Map.class);
        String access_token = resp.get("access_token");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("authorization", "Bearer " + access_token);
        HttpEntity<Object> httpEntity = new HttpEntity<>(httpHeaders);
        ResponseEntity<String> entity = restTemplate.exchange("http://localhost:8081/admin/hello", HttpMethod.GET, httpEntity, String.class);
        model.addAttribute("msg", entity.getBody());
        return "password";
    }

    @GetMapping("/password.html")
    public String password() {
        return "password";
    }
}
