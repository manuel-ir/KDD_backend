package com.kdd.kdd_backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class TriggerInitializer {

    private final JdbcTemplate jdbcTemplate;

    @EventListener(ApplicationReadyEvent.class)
    public void crearTriggers() {
        try {
            ClassPathResource resource = new ClassPathResource("triggers.sql");
            String sql = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            Arrays.stream(sql.split("(?<=END);"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .forEach(statement -> {
                        try {
                            jdbcTemplate.execute(statement);
                        } catch (Exception e) {
                            System.err.println("Error al crear trigger: " + e.getMessage());
                        }
                    });
        } catch (IOException e) {
            System.err.println("No se pudo leer triggers.sql: " + e.getMessage());
        }
    }
}
