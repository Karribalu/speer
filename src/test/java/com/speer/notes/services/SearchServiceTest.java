package com.speer.notes.services;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static com.mongodb.assertions.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SearchServiceTest {
    @InjectMocks
    SearchService searchService;
    @Mock
    MongoTemplate mongoTemplate;
    @Mock
    MongoCollection<Document> mongoCollection;

    @Test
    public void testSearchByKeywords(){
        AggregateIterable iterable = PowerMockito.mock(AggregateIterable.class);
        when(mongoTemplate.getCollection(anyString())).thenReturn(mongoCollection);
        when(mongoCollection.aggregate(anyList(), any())).thenReturn(iterable);
        when(iterable.into(anyList())).thenReturn(List.of(new Document()));
        ResponseEntity<?> response= searchService.searchByKeywords("test", "hello:search:text");
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }
    @Test
    public void testSearchByKeywordsErrror(){
        when(mongoTemplate.getCollection(anyString())).thenReturn(mongoCollection);
        when(mongoCollection.aggregate(anyList(), any())).thenReturn(null);
        ResponseEntity<?> response= searchService.searchByKeywords("test", "hello:search:text");
        assertTrue(response.getStatusCode().is5xxServerError());
    }
}
