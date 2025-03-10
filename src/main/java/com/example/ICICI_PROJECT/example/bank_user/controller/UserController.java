package com.example.ICICI_PROJECT.example.bank_user.controller;


import com.example.ICICI_PROJECT.example.bank_user.dto.UserDto;
import com.example.ICICI_PROJECT.example.bank_user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/user/login")
    public UserDto.LoginResponse login(
             @RequestBody UserDto.LoginRequest loginRequest) {
        if (ObjectUtils.isEmpty(loginRequest)
                || StringUtils.isEmpty(loginRequest.mobileNumber())
                || StringUtils.isEmpty(loginRequest.password())) {
            throw new IllegalArgumentException("error.WrongCredentials");
        }
        System.out.println("test");
        return userService.login(loginRequest);

    }

    @PostMapping("/data/user/create")
    @ResponseStatus(HttpStatus.CREATED)
    public void createBankUser(@RequestBody UserDto.UserRequest userRequest, @RequestHeader(HttpHeaders.USER_AGENT) String userAgent) {
        userService.createBankUser(userRequest, userAgent);
    }

    @GetMapping("/user/getUser")
    public List<UserDto.UserResponse> getUsers(
            @RequestParam(value = "user_uuid", required = false) String userUUID
    ) {
        return userService.getUserDetails(userUUID);
    }

    @PutMapping("/user/update")
    public UserDto.UserResponse updateUser(
            @RequestBody UserDto.UpdateUserRequest updateUserRequest,
            @RequestParam(value = "user_uuid") String userUUID
    ){
        return userService.updateUserDetails(userUUID,updateUserRequest);
    }


    @PutMapping("/user/update/password")
    @ResponseStatus(HttpStatus.CREATED)
    public void updatePassword(
            @RequestBody UserDto.UpdatePasswordRequest updatePasswordRequest,
            @RequestParam(value = "user_uuid") String userUUID
    ){
        userService.updatePassword(userUUID,updatePasswordRequest);
    }

}
