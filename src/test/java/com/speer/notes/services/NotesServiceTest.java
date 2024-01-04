package com.speer.notes.services;

import com.mongodb.MongoClientException;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.speer.notes.models.CreateNotesDto;
import com.speer.notes.models.NotesEntity;
import com.speer.notes.models.SharedUser;
import org.bson.Document;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.assertions.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NotesServiceTest {
    @Mock
    MongoTemplate mongoTemplate;
    @Mock
    MongoCollection<Document> mockNotesCollection;
    @InjectMocks
    NotesService notesService;

    @Before
    public void setUp() {
        NotesEntity notesEntity = new NotesEntity("test", "test", "test", "test", new ArrayList<>(), LocalDateTime.now(), LocalDateTime.now());
        AggregateIterable iterable = PowerMockito.mock(AggregateIterable.class);
        when(mockNotesCollection.aggregate(anyList(), any())).thenReturn(iterable);
        when(iterable.into(anyList())).thenReturn(List.of(new Document()));
        when(mongoTemplate.getCollection(anyString())).thenReturn(mockNotesCollection);
        when(mongoTemplate.save(any(), anyString())).thenReturn(notesEntity);
        when(mongoTemplate.getConverter()).thenReturn(mock(MongoConverter.class));
        when(mongoTemplate.getConverter().read(any(), any())).thenReturn(notesEntity);
        when(mongoTemplate.findOne(any(), eq(NotesEntity.class), anyString())).thenReturn(notesEntity);
    }

    @Test
    public void testCreateNotes() {
        CreateNotesDto createNotesDto = new CreateNotesDto("test", "test");
        ResponseEntity<?> responseEntity = notesService.createNotes("test", createNotesDto);
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void testCreateNotesError() {
        when(mongoTemplate.save(any(), anyString())).thenReturn(new Error("test"));
        CreateNotesDto createNotesDto = new CreateNotesDto("test", "test");
        ResponseEntity<?> responseEntity = notesService.createNotes("test", createNotesDto);
        assertTrue(responseEntity.getStatusCode().is5xxServerError());
    }

    @Test
    public void testGetAllNotes() {
        ResponseEntity<?> responseEntity = notesService.getAllNotes("test");
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void testGetAllNotesError() {
        when(mongoTemplate.getConverter().read(any(), any())).thenReturn(new Error("test"));
        ResponseEntity<?> responseEntity = notesService.getAllNotes("test");
        assertTrue(responseEntity.getStatusCode().is5xxServerError());
    }

    @Test
    public void testGetNotesById() {
        ResponseEntity<?> responseEntity = notesService.getNotesById("test", "testId");
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void testGetNotesByIdNotFound() {
        ResponseEntity<?> responseEntity = notesService.getNotesById("someUser", "testId");
        assertTrue(responseEntity.getStatusCode().is4xxClientError());
    }

    @Test
    public void testUpdateNotesById() {
        ResponseEntity<?> responseEntity = notesService.updateNotesById("test", "testId", new CreateNotesDto("test", "test"));
        verify(mongoTemplate).save(any(), anyString());
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void testUpdateNotesByIdNotFound() {
        when(mongoTemplate.findOne(any(), eq(NotesEntity.class), anyString())).thenReturn(null);
        ResponseEntity<?> responseEntity = notesService.updateNotesById("test", "testId", new CreateNotesDto("test", "test"));
        assertTrue(responseEntity.getStatusCode().is4xxClientError());
    }

    @Test
    public void testDeleteNotesById() {
        ResponseEntity<?> responseEntity = notesService.deleteNotesById("test", "testId");
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void testDeleteNotesByIdNotFound() {
        when(mongoTemplate.findOne(any(), eq(NotesEntity.class), anyString())).thenReturn(null);
        ResponseEntity<?> responseEntity = notesService.deleteNotesById("test", "testId");
        assertTrue(responseEntity.getStatusCode().is4xxClientError());
    }

    @Test
    public void testShareNotes() {
        ResponseEntity<?> responseEntity = notesService.shareNotes("test", "testId", new SharedUser("test"));
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void testShareNotesNotFound() {
        ResponseEntity<?> responseEntity = notesService.shareNotes("testss", "testId", new SharedUser("test"));
        assertTrue(responseEntity.getStatusCode().is4xxClientError());
    }

}
