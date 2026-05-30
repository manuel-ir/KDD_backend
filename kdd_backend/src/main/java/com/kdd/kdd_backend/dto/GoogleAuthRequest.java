package com.kdd.kdd_backend.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoogleAuthRequest {
    private String idToken;
}
