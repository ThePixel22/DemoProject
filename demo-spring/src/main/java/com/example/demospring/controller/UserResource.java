package com.example.demospring.controller;

import com.example.demospring.model.SignUpBean;
import com.example.demospring.model.domain.Client;
import com.example.demospring.repositories.ClientRepository;
import com.example.demospring.util.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class UserResource {

    @Autowired
    ClientRepository clientRepository;

    @PostMapping("/signUp")
    public String signUp(@RequestBody SignUpBean request){
        if(!request.getConfirmPassword().equals(request.getPassword())) {
            return "error! Password don't match!";
        }

        String encodePassword = PasswordEncoder.encode(request.getPassword());

        Client newClient = new Client();
        newClient.setRecordNo(1L);
        newClient.setLastUpdateDate(new Date());
        newClient.setUserName(request.getUserName());
        newClient.setPassword(encodePassword);
        newClient.setStatus("A");

        clientRepository.save(newClient);
        return "ok";
    }

    @GetMapping("/signUp")
    public String abc(){
        return "ok";
    }

    @GetMapping("/abd")
    public String abd(){
        return "abd";
    }
}
