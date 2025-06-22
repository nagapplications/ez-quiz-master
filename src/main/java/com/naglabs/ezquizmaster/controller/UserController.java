package com.naglabs.ezquizmaster.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class UserController {

    @GetMapping("/user")
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
        return principal.getAttributes(); // returns user info from Google
    }

    @GetMapping("/")
    public String home() {
        return "<h1>Welcome!</h1><p><a href='/ezquizmaster/user'>View Profile</a></p>";
    }
}

