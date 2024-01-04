package com.speer.notes.controllers;

import com.speer.notes.models.CreateNotesDto;
import com.speer.notes.models.ErrorResponse;
import com.speer.notes.models.SharedUser;
import com.speer.notes.services.JWTUtils;
import com.speer.notes.services.NotesService;
import com.speer.notes.services.RateLimitService;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notes")
public class NotesController {
    @Autowired
    private NotesService notesService;
    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private RateLimitService rateLimitService;

    @PostMapping(path = "/",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createNotes(@RequestHeader HttpHeaders httpHeaders, @RequestBody CreateNotesDto createNotesDto) {
        String[] authResponse = jwtUtils.authorizeToken(httpHeaders);
        if (authResponse[0].equals("true")) {
            Bucket bucket = rateLimitService.resolveBucket(authResponse[1]);
            ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
            if (probe.isConsumed()) {
                return notesService.createNotes(authResponse[1], createNotesDto);
            } else {
                long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
                return rateLimitError(waitForRefill);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(authResponse[1]));
        }
    }

    @GetMapping(path = "/",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllNotes(@RequestHeader HttpHeaders httpHeaders) {
        String[] authResponse = jwtUtils.authorizeToken(httpHeaders);
        if (authResponse[0].equals("true")) {
            Bucket bucket = rateLimitService.resolveBucket(authResponse[1]);
            ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
            if (probe.isConsumed()) {
                return notesService.getAllNotes(authResponse[1]);
            } else {
                long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
                return rateLimitError(waitForRefill);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(authResponse[1]));
        }
    }

    @GetMapping(path = "/{notesId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getNotesById(@RequestHeader HttpHeaders httpHeaders, @PathVariable String notesId) {
        String[] authResponse = jwtUtils.authorizeToken(httpHeaders);
        if (authResponse[0].equals("true")) {
            Bucket bucket = rateLimitService.resolveBucket(authResponse[1]);
            ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
            if (probe.isConsumed()) {
                return notesService.getNotesById(authResponse[1], notesId);
            } else {
                long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
                return rateLimitError(waitForRefill);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(authResponse[1]));
        }
    }

    @PostMapping(path = "/{notesId}/share",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> shareNotes(@RequestHeader HttpHeaders httpHeaders, @PathVariable String notesId, @RequestBody SharedUser sharedUser) {
        String[] authResponse = jwtUtils.authorizeToken(httpHeaders);
        if (authResponse[0].equals("true")) {
            Bucket bucket = rateLimitService.resolveBucket(authResponse[1]);
            ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
            if (probe.isConsumed()) {
                return notesService.shareNotes(authResponse[1], notesId, sharedUser);
            } else {
                long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
                return rateLimitError(waitForRefill);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(authResponse[1]));
        }
    }

    @PutMapping(path = "/{notesId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateNotesById(@RequestHeader HttpHeaders httpHeaders, @PathVariable String notesId, @RequestBody CreateNotesDto notesDto) {
        String[] authResponse = jwtUtils.authorizeToken(httpHeaders);
        if (authResponse[0].equals("true")) {
            Bucket bucket = rateLimitService.resolveBucket(authResponse[1]);
            ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
            if (probe.isConsumed()) {
                return notesService.updateNotesById(authResponse[1], notesId, notesDto);
            } else {
                long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
                return rateLimitError(waitForRefill);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(authResponse[1]));
        }
    }

    @DeleteMapping(path = "/{notesId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteNotesById(@RequestHeader HttpHeaders httpHeaders, @PathVariable String notesId) {
        String[] authResponse = jwtUtils.authorizeToken(httpHeaders);
        if (authResponse[0].equals("true")) {
            Bucket bucket = rateLimitService.resolveBucket(authResponse[1]);
            ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
            if (probe.isConsumed()) {
                return notesService.deleteNotesById(authResponse[1], notesId);
            } else {
                long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
                return rateLimitError(waitForRefill);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(authResponse[1]));
        }
    }

    private ResponseEntity<ErrorResponse> rateLimitError(long seconds) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header("X-Rate-Limit-Retry-After-Seconds", String.valueOf(seconds))
                .body(new ErrorResponse("Too many Requests, Please wait for " + seconds + " seconds before trying again"));
    }
}
