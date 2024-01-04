package com.speer.notes.services;

import com.speer.notes.models.UserEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class JWTUtilsTest {

    private JWTUtils jwtUtils = new JWTUtils(60, "testingwithveyveryveryveryverybig256byteskey");
    String exampleToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0IiwiZXhwIjoxNzA1MDE3NjQ2fQ.3kDWFvV-RMBA54NlxfT0_4A2lWPUURH3sbgn27geMEQ";

    @Test
    public void testGenerateToken(){
        UserEntity userEntity = new UserEntity("test", "test","test","test");
        assertNotNull(jwtUtils.generateToken(userEntity));
    }

    @Test
    public void testGetUsernameFromTokenSuccess(){
        HttpHeaders headers = new HttpHeaders();
        headers.add("authorization", "Bearer "+exampleToken);
        assertEquals("test",jwtUtils.authorizeToken(headers)[1]);
    }
    @Test
    public void testGetUsernameFromTokenNotProvided(){
        HttpHeaders headers = new HttpHeaders();
        assertEquals("Authorization token not provided",jwtUtils.authorizeToken(headers)[1]);
    }
    @Test
    public void testGetUsernameFromTokenExpired(){
        String notValidToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0IiwiZXhwIjoxNzA0Mzg3MTc3fQ.TvKZPsnq-oOQXP_Rkt9ftr01QAr9n04g9qnRFLtNBrc";
        HttpHeaders headers = new HttpHeaders();
        headers.add("authorization", "Bearer "+notValidToken);
        assertEquals("Token expired, please generate new one",jwtUtils.authorizeToken(headers)[1]);
    }
    @Test
    public void testGetUsernameFromTokenNotValid(){
        String notValidToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0IiwiZXhwIjoxNzA0Mzg3MTc3fQ.TvKZPnq-oOQXP_Rkt9ftr01QAr9n04g9qnRFLtNBrc";
        HttpHeaders headers = new HttpHeaders();
        headers.add("authorization", "Bearer "+notValidToken);
        assertEquals("Authorization Token provided is not valid",jwtUtils.authorizeToken(headers)[1]);
    }
    @Test
    public void testGetUsernameFromTokenSomethingWentWrong(){
        String notValidToken = "test";
        HttpHeaders headers = new HttpHeaders();
        headers.add("authorization", "Bearer "+notValidToken);
        assertEquals("Something Went wrong",jwtUtils.authorizeToken(headers)[1]);
    }
}
