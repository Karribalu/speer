package com.speer.notes.controllers;

import com.speer.notes.models.CreateNotesDto;
import com.speer.notes.models.SharedUser;
import com.speer.notes.services.JWTUtils;
import com.speer.notes.services.NotesService;
import com.speer.notes.services.RateLimitService;
import io.github.bucket4j.Bucket;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NotesControllerTests {

    @InjectMocks
    private NotesController notesController;

    @Mock
    private NotesService notesService;
    @Mock
    JWTUtils jwtUtils;


    RateLimitService rateLimitService = new RateLimitService(1);

    @Mock
    RateLimitService mockRateLimit;
    Bucket bucket = rateLimitService.resolveBucket("test");
    HttpHeaders headers = new HttpHeaders();
    @Before
    public void setUp(){
        when(mockRateLimit.resolveBucket(anyString())).thenReturn(bucket);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth("Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0IiwiZXhwIjoxNzA1MDE3NjQ2fQ.3kDWFvV-RMBA54NlxfT0_4A2lWPUURH3sbgn27geMEQ");
    }

    @Test
    public void testCreateNotes(){
        when(jwtUtils.authorizeToken(any())).thenReturn(new String[]{"true", "test"});
        when(notesService.createNotes(anyString(), any())).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        assertTrue(notesController.createNotes(headers, new CreateNotesDto("test", "test")).getStatusCode().is2xxSuccessful());
    }
    @Test
    public void testCreateNotesUnauthroized(){
        when(jwtUtils.authorizeToken(any())).thenReturn(new String[]{"false", "test"});
        assertTrue(notesController.createNotes(headers, new CreateNotesDto("test", "test")).getStatusCode().is4xxClientError());
    }

    @Test
    public void testGetAllNotes(){
        when(jwtUtils.authorizeToken(any())).thenReturn(new String[]{"true", "test"});
        when(notesService.getAllNotes(anyString())).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        assertTrue(notesController.getAllNotes(headers).getStatusCode().is2xxSuccessful());
    }
    @Test
    public void testGetAllNotesUnauthorized(){
        when(jwtUtils.authorizeToken(any())).thenReturn(new String[]{"false", "test"});
        assertTrue(notesController.getAllNotes(headers).getStatusCode().is4xxClientError());
    }
    @Test
    public void testGetNotesById(){
        when(jwtUtils.authorizeToken(any())).thenReturn(new String[]{"true", "test"});
        when(notesService.getNotesById(anyString(), anyString())).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        assertTrue(notesController.getNotesById(headers, anyString()).getStatusCode().is2xxSuccessful());
    }
    @Test
    public void testGetNotesByIdUnauthorized(){
        when(jwtUtils.authorizeToken(any())).thenReturn(new String[]{"false", "test"});
        assertTrue(notesController.getNotesById(headers, anyString()).getStatusCode().is4xxClientError());
    }

    @Test
    public void testShareNotes(){
        when(jwtUtils.authorizeToken(any())).thenReturn(new String[]{"true", "test"});
        when(notesService.shareNotes(anyString(), anyString(), any())).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        assertTrue(notesController.shareNotes(headers, anyString(), new SharedUser("test")).getStatusCode().is2xxSuccessful());
    }
    @Test
    public void testShareNotesUnauthorized(){
        when(jwtUtils.authorizeToken(any())).thenReturn(new String[]{"false", "test"});
        assertTrue(notesController.shareNotes(headers, anyString(), new SharedUser("test")).getStatusCode().is4xxClientError());
    }

    @Test
    public void testUpdateNotesById(){
        when(jwtUtils.authorizeToken(any())).thenReturn(new String[]{"true", "test"});
        when(notesService.updateNotesById(anyString(), anyString(), any())).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        assertTrue(notesController.updateNotesById(headers, anyString(), new CreateNotesDto("test", "test")).getStatusCode().is2xxSuccessful());
    }

    @Test
    public void testUpdateNotesByIdUnauthorized(){
        when(jwtUtils.authorizeToken(any())).thenReturn(new String[]{"false", "test"});
        assertTrue(notesController.updateNotesById(headers, anyString(), new CreateNotesDto("test", "test")).getStatusCode().is4xxClientError());
    }

    @Test
    public void testDeleteNotesById(){
        when(jwtUtils.authorizeToken(any())).thenReturn(new String[]{"true", "test"});
        when(notesService.deleteNotesById(anyString(), anyString())).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        assertTrue(notesController.deleteNotesById(headers, anyString()).getStatusCode().is2xxSuccessful());
    }

    @Test
    public void testDeleteNotesByIdUnauthorized(){
        when(jwtUtils.authorizeToken(any())).thenReturn(new String[]{"false", "test"});
        assertTrue(notesController.deleteNotesById(headers, anyString()).getStatusCode().is4xxClientError());
    }
}
