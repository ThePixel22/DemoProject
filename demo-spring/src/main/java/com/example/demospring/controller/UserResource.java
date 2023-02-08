package com.example.demospring.controller;

import com.example.demospring.model.ResultData;
import com.example.demospring.model.SignInBean;
import com.example.demospring.model.SignUpBean;
import com.example.demospring.model.domain.Client;
import com.example.demospring.repositories.ClientRepository;
import com.example.demospring.util.PasswordEncoder;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ResultData> signUp(@RequestBody SignUpBean request){
        if(!request.getConfirmPassword().equals(request.getPassword())) {
            ResultData resultData = new ResultData();
            resultData.setErrorMessage("error! Password don't match!");
            resultData.setErrorCode("E501");
            return ResponseEntity.ok().body(resultData);
        }

        String encodePassword = PasswordEncoder.encode(request.getPassword());

        Client newClient = new Client();
        newClient.setLastUpdateDate(new Date());
        newClient.setUserName(request.getUserName());
        newClient.setPassword(encodePassword);
        newClient.setStatus("A");

        clientRepository.save(newClient);

        ResultData resultData = new ResultData();
        resultData.setErrorMessage("ok");
        resultData.setErrorCode("00");
        resultData.setData(newClient);
        return ResponseEntity.ok(resultData);
    }

    @PostMapping("/signIn")
    public ResponseEntity<ResultData> signIn(@RequestBody SignInBean request){

        Client client = clientRepository.getClientByUserNameAndStatus(request.getUserName(), "A");
        if(client == null){
            ResultData resultData = new ResultData();
            resultData.setErrorMessage("error! Client don't exists!");
            resultData.setErrorCode("E502");
            return ResponseEntity.ok().body(resultData);
        }

        if(PasswordEncoder.matches(request.getPassword(),client.getPassword())){
            ResultData resultData = new ResultData();
            resultData.setErrorMessage("ok");
            resultData.setErrorCode("00");
            resultData.setData(client);
            return ResponseEntity.ok(resultData);
        } else {
            ResultData resultData = new ResultData();
            resultData.setErrorMessage("error! Password isn't correct!");
            resultData.setErrorCode("E503");
            return ResponseEntity.ok().body(resultData);
        }
    }
}
