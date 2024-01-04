package com.speer.notes.controllers;

import com.speer.notes.models.ErrorResponse;
import com.speer.notes.services.JWTUtils;
import com.speer.notes.services.RateLimitService;
import com.speer.notes.services.SearchService;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search")
public class SearchController {
    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private SearchService searchService;
    @Autowired
    private RateLimitService rateLimitService;

    @GetMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getNotesById(@RequestHeader HttpHeaders httpHeaders, String q) {
        String[] authResponse = jwtUtils.authorizeToken(httpHeaders);
        if (authResponse[0].equals("true")) {
            Bucket bucket = rateLimitService.resolveBucket(authResponse[1]);
            ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
            if (probe.isConsumed()) {
                return searchService.searchByKeywords(authResponse[1], q);
            } else {
                long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                        .header("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitForRefill))
                        .body(new ErrorResponse("Too many Requests, Please wait for " + waitForRefill + " seconds before trying again"));
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(authResponse[1]));
        }
    }
}
