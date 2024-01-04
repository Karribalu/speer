package com.speer.notes.services;

import com.mongodb.client.MongoCollection;
import com.speer.notes.models.ErrorResponse;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class SearchService {
    private MongoTemplate mongoTemplate;

    @Autowired
    public SearchService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public ResponseEntity<?> searchByKeywords(String username, String qParam) {
        MongoCollection<Document> notesCollection = mongoTemplate.getCollection("notes");
        try {
            String[] keywords = qParam.split(":");
            List<Document> aggregation = Arrays.asList(new Document("$search",
                            new Document("index", "search-index")
                                    .append("text",
                                            new Document("query", Arrays.asList(keywords))
                                                    .append("path", "content"))
                                    .append("highlight",
                                            new Document("path", "content"))),
                    new Document("$project",
                            new Document("_id", 1L)
                                    .append("username", 1L)
                                    .append("title", 1L)
                                    .append("content", 1L)
                                    .append("createdAt", 1L)
                                    .append("updatedAt", 1L)
                                    .append("highlights",
                                            new Document("$meta", "searchHighlights"))));
            List<Document> results = notesCollection.aggregate(aggregation, Document.class).into(new ArrayList<>());

            return ResponseEntity.status(HttpStatus.OK).body(results);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Something Went Wrong"));
        }
    }
}
