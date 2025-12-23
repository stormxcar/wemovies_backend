package com.example.demo.models.auth;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class GoogleLoginRequest {
    private String idToken;
}
