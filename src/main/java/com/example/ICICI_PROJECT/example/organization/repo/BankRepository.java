package com.example.ICICI_PROJECT.example.organization.repo;


import com.example.ICICI_PROJECT.example.organization.model.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BankRepository extends JpaRepository<Bank, Long> {

    Bank findByBankCode(String bankCode);
}
