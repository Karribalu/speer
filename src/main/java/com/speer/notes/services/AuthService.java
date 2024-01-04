package com.speer.notes.services;

import com.speer.notes.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    Logger logger = LoggerFactory.getLogger(AuthService.class);

    private MongoTemplate mongoTemplate;
    private PasswordEncoder passwordEncoder;
    private JWTUtils jwtUtils;

    @Autowired
    public AuthService(
            MongoTemplate mongoTemplate, PasswordEncoder passwordEncoder, JWTUtils jwtUtils) {
        this.mongoTemplate = mongoTemplate;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<?> register(RegisterDto registerDto) {
        Query query = new Query(Criteria.where("email").is(registerDto.getEmail()));
        UserEntity user = mongoTemplate.findOne(query, UserEntity.class, "users");
        if (user != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse("User Already registered"));
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(registerDto.getEmail());
        userEntity.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        userEntity.setUsername(registerDto.getUsername());
        String token = jwtUtils.generateToken(userEntity);
        mongoTemplate.save(userEntity, "users");
        return ResponseEntity.status(HttpStatus.CREATED).body(new TokenResponse("User Registered Successfully", token));
    }

    public ResponseEntity<?> login(LoginDto user) {
        Query query = new Query(Criteria.where("username").is(user.getUsername()));
        var userEntity = mongoTemplate.findOne(query, UserEntity.class, "users");
        if (userEntity == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("User does not exist"));
        } else {
            if (passwordEncoder.matches(user.getPassword(), userEntity.getPassword())) {
                String token = jwtUtils.generateToken(userEntity);
                return ResponseEntity.status(HttpStatus.OK).body(new TokenResponse("User Logged in Successfully", token));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Wrong Password"));
            }
        }
    }

}
