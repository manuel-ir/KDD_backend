package com.kdd.kdd_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la aplicacion Spring Boot de KDD.
 *
 * Al ejecutar este archivo arranca el servidor web embebido (Tomcat),
 * carga toda la configuracion de Spring y pone la API REST en marcha.
 * Punto de entrada para ejecutar el backend desde IntelliJ o desde linea de comandos.
 */
@SpringBootApplication
public class KddBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(KddBackendApplication.class, args);
    }
}
