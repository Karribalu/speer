package com.speer.notes.services;

import com.mongodb.client.MongoCollection;
import com.speer.notes.models.*;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotesService {
    Logger logger = LoggerFactory.getLogger(NotesService.class);
    private MongoTemplate mongoTemplate;

    @Autowired
    public NotesService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public ResponseEntity<?> createNotes(String username, CreateNotesDto notesDto) {
        try {
            NotesEntity notes = new NotesEntity();
            notes.setTitle(notesDto.getTitle());
            notes.setContent(notesDto.getContent());
            notes.setCreatedAt(LocalDateTime.now());
            notes.setUpdatedAt(LocalDateTime.now());
            notes.setUsername(username);
            NotesEntity entity = mongoTemplate.save(notes, "notes");
            return ResponseEntity.status(HttpStatus.CREATED).body(new NotesResponse("Notes created successfully", entity));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Something went wrong please try again"));
        }
    }

    public ResponseEntity<?> getAllNotes(String username) {
        MongoCollection<Document> notesCollection = mongoTemplate.getCollection("notes");
        try {
            List<Document> aggregation = List.of(new Document("$search",
                    new Document("index", "search-index")
                            .append("text",
                                    new Document("query", "test")
                                            .append("path", "title"))));

            List<Document> results = notesCollection.aggregate(aggregation, Document.class).into(new ArrayList<>());
            List<NotesEntity> notes = results.stream().map(result -> mongoTemplate.getConverter().read(NotesEntity.class, result)).collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.OK).body(notes);
        } catch (Exception e) {
            logger.info("exception", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Something Went Wrong"));
        }
    }

    public ResponseEntity<?> getNotesById(String username, String id) {
        try {
            Query query = new Query(Criteria.where("id").is(id));
            NotesEntity notes = mongoTemplate.findOne(query, NotesEntity.class, "notes");
            if (notes != null && (notes.getUsername().equals(username) || notes.getSharedList().contains(username))) {
                return ResponseEntity.status(HttpStatus.OK).body(notes);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("No notes found with the given id"));
            }
        } catch (Exception e) {
            logger.info("exception in get notes by Id", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Something Went Wrong"));
        }
    }

    public ResponseEntity<?> updateNotesById(String username, String id, CreateNotesDto notesDto) {
        try {
            Query query = new Query(Criteria.where("id").is(id));
            if (notesDto == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Please provide at least one criteria to update"));
            }
            NotesEntity existingNotes = mongoTemplate.findOne(query, NotesEntity.class, "notes");
            if (existingNotes == null || !existingNotes.getUsername().equals(username)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("No notes found with the given id to update"));
            } else {
                if (notesDto.getTitle() != null) {
                    existingNotes.setTitle(notesDto.getTitle());
                }
                if (notesDto.getContent() != null) {
                    existingNotes.setContent(notesDto.getTitle());
                }
                existingNotes.setUpdatedAt(LocalDateTime.now());

                mongoTemplate.save(existingNotes, "notes");
                return ResponseEntity.status(HttpStatus.OK).body(new NotesResponse("Document updated successfully", existingNotes));
            }
        } catch (Exception e) {
            logger.info("exception in update notes by Id", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Something Went Wrong"));
        }
    }

    public ResponseEntity<?> deleteNotesById(String username, String id) {
        try {
            Query query = new Query(Criteria.where("id").is(id));
            NotesEntity existingNotes = mongoTemplate.findOne(query, NotesEntity.class, "notes");
            if (existingNotes == null || !existingNotes.getUsername().equals(username)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("No notes found to delete"));
            } else {
                mongoTemplate.remove(existingNotes, "notes");
                return ResponseEntity.status(HttpStatus.OK).body(new NotesResponse("Document deleted successfully", existingNotes));
            }
        } catch (Exception e) {
            logger.info("exception in update notes by Id", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Something Went Wrong"));
        }
    }

    public ResponseEntity<?> shareNotes(String username, String notesId, SharedUser sharedUser) {
        try {
            Query query = new Query(Criteria.where("id").is(notesId));
            NotesEntity notes = mongoTemplate.findOne(query, NotesEntity.class, "notes");
            if (notes == null || !notes.getUsername().equals(username)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("No notes found with the given id to share"));
            } else {
                notes.getSharedList().add(sharedUser.getUsername());
                notes.setUpdatedAt(LocalDateTime.now());
                mongoTemplate.save(notes, "notes");
                return ResponseEntity.status(HttpStatus.OK).body(new NotesResponse("Note having id " + notesId + "is shared with user " + sharedUser.getUsername(), notes));
            }
        } catch (Exception e) {
            logger.info("exception in get notes by Id", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Something Went Wrong"));
        }
    }
}
