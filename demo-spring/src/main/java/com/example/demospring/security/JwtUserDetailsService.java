package com.example.demospring.security;

import com.example.demospring.model.domain.Client;
import com.example.demospring.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    ClientRepository clientRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Client c = clientRepository.getClientByUserNameAndStatus(username, "A");

        if(c != null){
            return new User(c.getUserName(),c.getPassword(), new ArrayList<>() );
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }
}
