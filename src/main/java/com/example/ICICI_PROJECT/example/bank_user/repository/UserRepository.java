package com.example.ICICI_PROJECT.example.bank_user.repository;


import com.example.ICICI_PROJECT.example.bank_user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {


    User findByEmailAndMobileNumber(String email, String mobileNumber);

    User findByMobileNumber(String mobileNumber);
    User findByEmail(String email);

    User findByUuid(String uuid);
}