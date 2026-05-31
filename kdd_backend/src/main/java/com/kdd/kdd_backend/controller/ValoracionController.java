package com.kdd.kdd_backend.controller;

import com.kdd.kdd_backend.dto.ValoracionDto;
import com.kdd.kdd_backend.service.ValoracionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/valoraciones")
@RequiredArgsConstructor
public class ValoracionController {

    private final ValoracionService valoracionService;

    @PostMapping
    public ResponseEntity<Void> valorar(Authentication auth,
                                         @RequestBody ValoracionDto dto) {
        Long userId = (Long) auth.getPrincipal();
        valoracionService.valorar(userId, dto);
        return ResponseEntity.ok().build();
    }
}
