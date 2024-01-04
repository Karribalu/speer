package com.speer.notes.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class RegisterDto {
    private String username;
    private String email;
    private String password;

    public String toJson() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(this);
    }
}
