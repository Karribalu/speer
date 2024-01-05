package com.speer.notes.controllers;

import com.speer.notes.services.JWTUtils;
import com.speer.notes.services.RateLimitService;
import com.speer.notes.services.SearchService;
import io.github.bucket4j.Bucket;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static com.mongodb.assertions.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SearchControllerTest {
    @InjectMocks
    SearchController searchController;
    @Mock
    SearchService searchService;
    @Mock
    JWTUtils jwtUtils;


    RateLimitService rateLimitService = new RateLimitService(1);

    @Mock
    RateLimitService mockRateLimit;

    @Test
    public void testGetNotesById() {
        Bucket bucket = rateLimitService.resolveBucket("test");
        when(searchService.searchByKeywords(anyString(), anyString())).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        when(mockRateLimit.resolveBucket(anyString())).thenReturn(bucket);
        when(jwtUtils.authorizeToken(any())).thenReturn(new String[]{"true", "test"});
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth("Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0IiwiZXhwIjoxNzA1MDE3NjQ2fQ.3kDWFvV-RMBA54NlxfT0_4A2lWPUURH3sbgn27geMEQ");
        assertTrue(searchController.getNotesById(headers, "testId").getStatusCode().is2xxSuccessful());
    }

    @Test
    public void testGetNotesByIdNotAuthorized() {
        when(jwtUtils.authorizeToken(any())).thenReturn(new String[]{"false", "Error"});
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth("Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0IiwiZXhwIjoxNzA1MDE3NjQ2fQ.3kDWFvV-RMBA54NlxfT0_4A2lWPUURH3sbgn27geMEQ");
        assertTrue(searchController.getNotesById(headers, "testId").getStatusCode().is4xxClientError());
    }
}
