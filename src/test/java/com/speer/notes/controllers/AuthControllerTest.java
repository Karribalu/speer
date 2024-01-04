package com.speer.notes.controllers;

import com.speer.notes.models.LoginDto;
import com.speer.notes.models.RegisterDto;
import com.speer.notes.services.AuthService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static com.mongodb.assertions.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AuthControllerTest {
    @Mock
    private AuthService authService;
    @InjectMocks
    private AuthController authController;

    @Test
    public void testRegister() {
        when(authService.register(any())).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        RegisterDto registerDto = new RegisterDto("test", "test@gmail.com", "testPassword");
        assertTrue(authController.signup(registerDto).getStatusCode().is2xxSuccessful());
    }

    @Test
    public void testLogin() {
        when(authService.login(any())).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        LoginDto loginDto = new LoginDto("test", "testPassword");
        assertTrue(authController.login(loginDto).getStatusCode().is2xxSuccessful());
    }
}
