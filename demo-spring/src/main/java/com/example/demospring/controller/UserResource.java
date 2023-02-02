package com.example.demospring.controller;

import com.example.demospring.model.SignUpBean;
import com.example.demospring.util.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserResource {
    @PostMapping("/signUp")
    public String signUp(@RequestBody SignUpBean request){
        if(!request.getConfirmPassword().equals(request.getPassword())) {
            return "error! Password don't match!";
        }

        String encodePassword = PasswordEncoder.encode(request.getPassword());
        return "ok";
    }
}
