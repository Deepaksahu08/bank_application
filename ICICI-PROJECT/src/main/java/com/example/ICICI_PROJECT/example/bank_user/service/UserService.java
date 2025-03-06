package com.example.ICICI_PROJECT.example.bank_user.service;


import com.example.ICICI_PROJECT.example.bank_user.dto.UserDto;
import com.example.ICICI_PROJECT.example.bank_user.model.User;
import com.example.ICICI_PROJECT.example.bank_user.repository.UserRepository;
import com.example.ICICI_PROJECT.example.organization.repo.BankRepository;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.example.ICICI_PROJECT.example.security.config.JwtConstants.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BankRepository bankRepository;
    public static final String LOGIN_ERROR_MESSAGE = "Unable to process login credentials. Please logout and login again";

    @Value("${jwt.tokenSecret}")
    private String tokenSecret;

    @Value("${jwt.validityInDays:60}")
    private int validityInDays;


    public void createBankUser(UserDto.UserRequest request, String userAgent) {
        var bank = bankRepository.findAll();
        UUID uuid = UUID.randomUUID();
        validateUser(request);
        User user = new User();
        user.setEmail(request.emailId());
        user.setUuid(uuid.toString());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setMobileNumber(request.mobileNumber());
        user.setGender(request.gender());
        user.setBankCode(bank.getFirst().getBankCode());
        user.setPassword(getPassword(request.password()));
        user.setUserName(request.firstName() + " " + request.lastName());
        userRepository.save(user);
    }

    public List<UserDto.UserResponse> getUserDetails(String userUUID) {
        if (userUUID != null && !userUUID.isEmpty()) {
            User user = userRepository.findByUuid(userUUID);

            if (user == null) {
                throw new IllegalArgumentException("Invalid User UUID.");
            }

            return List.of(buildUserResponse(user));
        } else {
            List<User> users = userRepository.findAll();

            return users.stream().map(this::buildUserResponse).toList();
        }
    }

    public UserDto.UserResponse updateUserDetails(String userUUID, UserDto.UpdateUserRequest request) {

            User user = userRepository.findByUuid(userUUID);
            user.setFirstName(request.firstName());
            user.setLastName(request.lastName());
            user.setUserName(request.firstName()+" "+request.lastName());
            userRepository.save(user);
            User userRepositoryByUuid= userRepository.findByUuid(userUUID);
            return buildUserResponse(userRepositoryByUuid);
    }
    public void updatePassword(String userUUID, UserDto.UpdatePasswordRequest request) {

        User user = userRepository.findByUuid(userUUID);
        if (user == null) {
            throw new IllegalArgumentException("Invalid User UUID.");
        }
        User userByEmail=userRepository.findByEmail(request.email());
        if (userByEmail == null) {
            throw new IllegalArgumentException("Invalid User Email.");
        }
        User userByMobileNumber=userRepository.findByMobileNumber(request.mobileNumber());
        if (userByMobileNumber == null) {
            throw new IllegalArgumentException("Invalid User MobileNumber.");
        }
        if (user.getUuid().equals(userByEmail.getUuid()) &&
                userByEmail.getUuid().equals(userByMobileNumber.getUuid())) {
            user.setPassword(getPassword(request.newPassword()));
            userRepository.save(user);
        }
    }

    private UserDto.UserResponse buildUserResponse(User user) {
        return UserDto.UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .mobileNumber(user.getMobileNumber())
                .userName(user.getUserName())
                .uuid(user.getUuid())
                .emailId(user.getEmail())
                .gender(user.getGender())
                .build();
    }


    public UserDto.LoginResponse login(UserDto.LoginRequest request) {

        User userData = userRepository.findByMobileNumber(request.mobileNumber());
        if (userData == null) {
            throw new IllegalArgumentException("Invalid User Please Enter Mobile Number");

        }

        String jwt = loginUser(request.mobileNumber(), request.password());
        var user = retrieveUserFromToken(jwt);
        String accessToken = generateUserAccessToken(user);
        return UserDto.LoginResponse.builder().jwtToken(accessToken).build();
    }

    private String getPassword(String password) {
        return passwordEncoder.encode(password);
    }

    private void validateUser(UserDto.UserRequest request) {
        User user = userRepository.findByEmailAndMobileNumber(request.emailId(), request.mobileNumber());
        if (user != null) {
            throw new IllegalArgumentException("A user with this email " + request.emailId() + " and with this number " + request.mobileNumber() + " already exists");
        }
    }


    public String generateUserAccessToken(User user) {
        try {
            return getUserJwtBuilder(user).claim(IS_PREMIUM, true).claim(MOBILE_NUMBER, user.getMobileNumber()).compact();
        } catch (Exception e) {
            throw new IllegalArgumentException("error.ExpiredOrInvalidToken", e);
        }
    }

    private JwtBuilder getUserJwtBuilder(User user) {
        var tokenSignInKey = new SecretKeySpec(Base64.getDecoder().decode(tokenSecret), SignatureAlgorithm.HS256.getJcaName());
        var currentDate = Instant.now();
        var jwtBuilder = Jwts.builder().claim(ID, user.getId()).claim(FIRST_NAME, user.getFirstName()).claim(LAST_NAME, user.getLastName()).claim(EMAIL, user.getEmail()).claim(ORGANIZATION, user.getBankCode()).claim(GUID, user.getUuid()).setSubject(user.getMobileNumber()).setId(String.valueOf(user.getId())).setIssuedAt(Date.from(currentDate)).setExpiration(Date.from(currentDate.plus(validityInDays, ChronoUnit.DAYS))).signWith(tokenSignInKey, SignatureAlgorithm.HS256);

        if (Objects.nonNull(user.getUuid())) {
            jwtBuilder.claim(GUID, user.getUuid());
        }

        return jwtBuilder;
    }

    public User retrieveUserFromToken(String tokenString) {
        var username = retrieveUserNameFromToken(tokenString);
        var user = userRepository.findByMobileNumber(username);

        if (user == null) {
            throw new IllegalArgumentException("error.AuthUserID.Exist");
        }
        return user;
    }

    public String retrieveUserNameFromToken(String token) {
        Key signingKey = new SecretKeySpec(Base64.getDecoder().decode(tokenSecret), SignatureAlgorithm.HS256.getJcaName());
        try {
            var jwtClaims = Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token).getBody();
            return jwtClaims.getSubject();
        } catch (Exception exception) {
            throw new IllegalArgumentException(LOGIN_ERROR_MESSAGE, exception);
        }
    }

    public String loginUser(String username, String password) {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            throw new IllegalArgumentException("error.invalidInput");
        }
        return createJwtToken(username, password);
    }

    public String createJwtToken(String username, String password) {
        final User user = userRepository.findByMobileNumber(username);
        if (user == null) {
            throw new IllegalArgumentException(LOGIN_ERROR_MESSAGE);
        }
        final String encodedPassword = user.getPassword();

        if (!passwordEncoder.matches(password, encodedPassword)) {
            throw new IllegalArgumentException(LOGIN_ERROR_MESSAGE);
        }
        return getIdTokenForUser(user);
    }

    private String getIdTokenForUser(User user) {
        var tokenSignInKey = new SecretKeySpec(Base64.getDecoder().decode(tokenSecret), SignatureAlgorithm.HS256.getJcaName());
        var currentDate = Instant.now();
        return Jwts.builder().setSubject(user.getMobileNumber()).setId(String.valueOf(user.getId())).setIssuedAt(Date.from(currentDate)).setExpiration(Date.from(currentDate.plus(10, ChronoUnit.MINUTES))).signWith(tokenSignInKey, SignatureAlgorithm.HS256).compact();
    }

}

