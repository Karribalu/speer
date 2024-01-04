package com.speer.notes.services;

import com.speer.notes.models.LoginDto;
import com.speer.notes.models.RegisterDto;
import com.speer.notes.models.UserEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AuthServiceTest {

    @Mock
    JWTUtils jwtUtils;

    @Mock
    MongoTemplate mongoTemplate;
    @Mock
    PasswordEncoder passwordEncoder;
    @InjectMocks
    AuthService authService;


    @Test
    public void testRegister() {
        RegisterDto dto = new RegisterDto("test", "test", "test@gmail.com");
        when(mongoTemplate.findOne(any(), any(), anyString())).thenReturn(null);
        ResponseEntity<?> responseEntity = authService.register(dto);
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }
    @Test
    public void testRegisterConflict(){
        RegisterDto dto = new RegisterDto("test", "test", "test@gmail.com");
        when(mongoTemplate.findOne(any(), any(), anyString())).thenReturn(new UserEntity("test", "test", "test", "test"));
        ResponseEntity<?> responseEntity = authService.register(dto);
        assertTrue(responseEntity.getStatusCode().is4xxClientError());
    }
    @Test
    public void testLoginSuccess(){
        LoginDto loginDto = new LoginDto("test","test");
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(mongoTemplate.findOne(any(), any(), anyString())).thenReturn(new UserEntity("test", "test", "test", "test"));
        ResponseEntity<?> responseEntity = authService.login(loginDto);
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }
    @Test
    public void testLoginFailure(){
        LoginDto loginDto = new LoginDto("test","test");
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        when(mongoTemplate.findOne(any(), any(), anyString())).thenReturn(new UserEntity("test", "test", "test", "test"));
        ResponseEntity<?> responseEntity = authService.login(loginDto);
        assertTrue(responseEntity.getStatusCode().is4xxClientError());
    }
    @Test
    public void testLoginUserDoesNotExist(){
        LoginDto loginDto = new LoginDto("test","test");
        when(mongoTemplate.findOne(any(), any(), anyString())).thenReturn(null);
        ResponseEntity<?> responseEntity = authService.login(loginDto);
        assertTrue(responseEntity.getStatusCode().is4xxClientError());
    }

}
