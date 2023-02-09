package com.example.demospring.repositories;

import com.example.demospring.model.domain.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    public Client getClientByUserNameAndStatus(String userName, String status);

    public boolean existsByUserNameAndStatus(String userName, String status);
}
