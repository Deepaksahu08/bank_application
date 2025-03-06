package com.example.ICICI_PROJECT.example.bank_user.model;


import com.example.ICICI_PROJECT.example.bank_user.dto.Gender;
import com.example.ICICI_PROJECT.example.organization.model.Bank;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;


@Data
@Entity
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "bank_user_dank")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "first_name")
    private String firstName;

    private String uuid;

    @Column(name = "last_name")
    private String lastName;
    @Column(name = "user_name")

    private String userName;

    @Column(name = "mobile_number")
    private String mobileNumber;
    private String password;
    private Gender gender;
    private String email;

    @Column(name = "bank_code")
    private String bankCode;
}
