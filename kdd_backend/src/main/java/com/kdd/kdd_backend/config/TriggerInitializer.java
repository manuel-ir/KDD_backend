package com.kdd.kdd_backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TriggerInitializer {

    private final JdbcTemplate jdbcTemplate;

    @EventListener(ApplicationReadyEvent.class)
    public void crearTriggers() {
        List<String> sentencias = List.of(
            "DROP TRIGGER IF EXISTS validar_fecha_plan",

            """
            CREATE TRIGGER validar_fecha_plan
            BEFORE INSERT ON planes
            FOR EACH ROW
            BEGIN
                IF NEW.fecha_evento IS NOT NULL AND NEW.fecha_evento < CURDATE() THEN
                    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La fecha del evento no puede ser en el pasado';
                END IF;
            END
            """,

            "DROP TRIGGER IF EXISTS validar_edad_participacion",

            """
            CREATE TRIGGER validar_edad_participacion
            BEFORE INSERT ON participaciones
            FOR EACH ROW
            BEGIN
                DECLARE edad_usuario INT;
                DECLARE edad_min_plan INT;
                DECLARE edad_max_plan INT;
                SELECT TIMESTAMPDIFF(YEAR, u.fecha_nacimiento, CURDATE()) INTO edad_usuario
                FROM usuarios u WHERE u.id_usuario = NEW.id_usuario;
                SELECT p.edad_min, p.edad_max INTO edad_min_plan, edad_max_plan
                FROM planes p WHERE p.id_plan = NEW.id_plan;
                IF edad_usuario IS NOT NULL AND edad_min_plan IS NOT NULL AND edad_usuario < edad_min_plan THEN
                    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'No cumples la edad minima para unirte a este plan';
                END IF;
                IF edad_usuario IS NOT NULL AND edad_max_plan IS NOT NULL AND edad_usuario > edad_max_plan THEN
                    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Superas la edad maxima para unirte a este plan';
                END IF;
            END
            """,

            "DROP TRIGGER IF EXISTS validar_edad_comunidad",

            """
            CREATE TRIGGER validar_edad_comunidad
            BEFORE INSERT ON pertenencias_comunidad
            FOR EACH ROW
            BEGIN
                DECLARE edad_usuario INT;
                DECLARE edad_min_com INT;
                DECLARE edad_max_com INT;
                SELECT TIMESTAMPDIFF(YEAR, u.fecha_nacimiento, CURDATE()) INTO edad_usuario
                FROM usuarios u WHERE u.id_usuario = NEW.id_usuario;
                SELECT c.edad_min, c.edad_max INTO edad_min_com, edad_max_com
                FROM comunidades c WHERE c.id_comunidad = NEW.id_comunidad;
                IF edad_usuario IS NOT NULL AND edad_min_com IS NOT NULL AND edad_usuario < edad_min_com THEN
                    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'No cumples la edad minima para unirte a esta comunidad';
                END IF;
                IF edad_usuario IS NOT NULL AND edad_max_com IS NOT NULL AND edad_usuario > edad_max_com THEN
                    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Superas la edad maxima para unirte a esta comunidad';
                END IF;
            END
            """
        );

        for (String sql : sentencias) {
            try {
                jdbcTemplate.execute(sql.trim());
            } catch (Exception e) {
                System.err.println("Error al crear trigger: " + e.getMessage());
            }
        }
    }
}
