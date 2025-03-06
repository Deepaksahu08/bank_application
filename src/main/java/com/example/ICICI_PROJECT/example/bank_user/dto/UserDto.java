package com.example.ICICI_PROJECT.example.bank_user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.NonNull;

public record UserDto() {

    @Builder
    public record LoginResponse(
          @JsonProperty("jwt_token") String jwtToken
    ){}

    @Builder
    public record UserResponse(
            @JsonProperty("mobile_number") String mobileNumber,
            @JsonProperty("first_name") String firstName,
            @JsonProperty("user_name") String userName,
            @JsonProperty("last_name") String lastName,
            @JsonProperty("email_id") String emailId,
            Long id,
            Gender gender,
            String uuid
    ){}

    public record LoginRequest(
            @JsonProperty("mobile_number") String mobileNumber,
            @JsonProperty("password") String password
    ){}

    public record UserRequest(
            @NonNull @JsonProperty("mobile_number") String mobileNumber,
            @NonNull String password,
            @NonNull @JsonProperty("first_name") String firstName,
            @NonNull @JsonProperty("last_name") String lastName,
            @NonNull @JsonProperty("email_id") String emailId,
            @NonNull Gender gender) {
    }

    public record UpdateUserRequest(
             @JsonProperty("first_name") String firstName,
             @JsonProperty("last_name") String lastName) {
    }

    public record UpdatePasswordRequest(
            @JsonProperty("mobile_number") String mobileNumber,
            String email,
            @JsonProperty("new_password") String newPassword) {
    }
}
