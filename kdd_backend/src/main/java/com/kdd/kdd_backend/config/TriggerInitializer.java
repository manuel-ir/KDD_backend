package com.kdd.kdd_backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Crea los triggers de validacion en la base de datos al arrancar.
 * Detecta el perfil activo para usar la sintaxis correcta:
 * - prod: PostgreSQL
 * - por defecto: MySQL
 */
@Component
@RequiredArgsConstructor
public class TriggerInitializer {

    private final JdbcTemplate jdbcTemplate;
    private final Environment env;

    @EventListener(ApplicationReadyEvent.class)
    public void crearTriggers() {
        boolean esPostgres = Arrays.asList(env.getActiveProfiles()).contains("prod");
        List<String> sentencias = esPostgres ? triggersPostgres() : triggersMysql();

        for (String sql : sentencias) {
            try {
                jdbcTemplate.execute(sql.trim());
            } catch (Exception e) {
                System.err.println("Error al crear trigger: " + e.getMessage());
            }
        }
    }

    // -----------------------------------------------------------------------
    // Sintaxis PostgreSQL
    // -----------------------------------------------------------------------
    private List<String> triggersPostgres() {
        return List.of(

            // --- Validar fecha del plan ---
            """
            CREATE OR REPLACE FUNCTION fn_validar_fecha_plan()
            RETURNS TRIGGER AS $$
            BEGIN
                IF NEW.fecha_evento IS NOT NULL AND NEW.fecha_evento < CURRENT_DATE THEN
                    RAISE EXCEPTION 'La fecha del evento no puede ser en el pasado';
                END IF;
                RETURN NEW;
            END;
            $$ LANGUAGE plpgsql
            """,

            "DROP TRIGGER IF EXISTS validar_fecha_plan ON planes",

            """
            CREATE TRIGGER validar_fecha_plan
            BEFORE INSERT ON planes
            FOR EACH ROW EXECUTE FUNCTION fn_validar_fecha_plan()
            """,

            // --- Validar edad para unirse a un plan (el creador queda exento) ---
            """
            CREATE OR REPLACE FUNCTION fn_validar_edad_participacion()
            RETURNS TRIGGER AS $$
            DECLARE
                edad_usuario INT;
                edad_min_plan INT;
                edad_max_plan INT;
                creador_plan BIGINT;
            BEGIN
                SELECT id_creador INTO creador_plan FROM planes WHERE id_plan = NEW.id_plan;
                IF NEW.id_usuario = creador_plan THEN
                    RETURN NEW;
                END IF;

                SELECT DATE_PART('year', AGE(CURRENT_DATE, u.fecha_nacimiento))
                INTO edad_usuario
                FROM usuarios u WHERE u.id_usuario = NEW.id_usuario;

                SELECT p.edad_min, p.edad_max
                INTO edad_min_plan, edad_max_plan
                FROM planes p WHERE p.id_plan = NEW.id_plan;

                IF edad_usuario IS NOT NULL AND edad_min_plan IS NOT NULL AND edad_usuario < edad_min_plan THEN
                    RAISE EXCEPTION 'No cumples la edad minima para unirte a este plan';
                END IF;
                IF edad_usuario IS NOT NULL AND edad_max_plan IS NOT NULL AND edad_usuario > edad_max_plan THEN
                    RAISE EXCEPTION 'Superas la edad maxima para unirte a este plan';
                END IF;
                RETURN NEW;
            END;
            $$ LANGUAGE plpgsql
            """,

            "DROP TRIGGER IF EXISTS validar_edad_participacion ON participaciones",

            """
            CREATE TRIGGER validar_edad_participacion
            BEFORE INSERT ON participaciones
            FOR EACH ROW EXECUTE FUNCTION fn_validar_edad_participacion()
            """,

            // --- Validar edad para unirse a una comunidad (el creador queda exento) ---
            """
            CREATE OR REPLACE FUNCTION fn_validar_edad_comunidad()
            RETURNS TRIGGER AS $$
            DECLARE
                edad_usuario INT;
                edad_min_com INT;
                edad_max_com INT;
                admin_com BIGINT;
            BEGIN
                SELECT id_admin INTO admin_com FROM comunidades WHERE id_comunidad = NEW.id_comunidad;
                IF NEW.id_usuario = admin_com THEN
                    RETURN NEW;
                END IF;

                SELECT DATE_PART('year', AGE(CURRENT_DATE, u.fecha_nacimiento))
                INTO edad_usuario
                FROM usuarios u WHERE u.id_usuario = NEW.id_usuario;

                SELECT c.edad_min, c.edad_max
                INTO edad_min_com, edad_max_com
                FROM comunidades c WHERE c.id_comunidad = NEW.id_comunidad;

                IF edad_usuario IS NOT NULL AND edad_min_com IS NOT NULL AND edad_usuario < edad_min_com THEN
                    RAISE EXCEPTION 'No cumples la edad minima para unirte a esta comunidad';
                END IF;
                IF edad_usuario IS NOT NULL AND edad_max_com IS NOT NULL AND edad_usuario > edad_max_com THEN
                    RAISE EXCEPTION 'Superas la edad maxima para unirte a esta comunidad';
                END IF;
                RETURN NEW;
            END;
            $$ LANGUAGE plpgsql
            """,

            "DROP TRIGGER IF EXISTS validar_edad_comunidad ON pertenencias_comunidad",

            """
            CREATE TRIGGER validar_edad_comunidad
            BEFORE INSERT ON pertenencias_comunidad
            FOR EACH ROW EXECUTE FUNCTION fn_validar_edad_comunidad()
            """
        );
    }

    // -----------------------------------------------------------------------
    // Sintaxis MySQL (entorno local)
    // -----------------------------------------------------------------------
    private List<String> triggersMysql() {
        return List.of(
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
            bloque_plan: BEGIN
                DECLARE edad_usuario INT;
                DECLARE edad_min_plan INT;
                DECLARE edad_max_plan INT;
                DECLARE creador_plan BIGINT;
                SELECT id_creador INTO creador_plan FROM planes WHERE id_plan = NEW.id_plan;
                IF NEW.id_usuario = creador_plan THEN
                    LEAVE bloque_plan;
                END IF;
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
            bloque_com: BEGIN
                DECLARE edad_usuario INT;
                DECLARE edad_min_com INT;
                DECLARE edad_max_com INT;
                DECLARE admin_com BIGINT;
                SELECT id_admin INTO admin_com FROM comunidades WHERE id_comunidad = NEW.id_comunidad;
                IF NEW.id_usuario = admin_com THEN
                    LEAVE bloque_com;
                END IF;
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
    }
}
