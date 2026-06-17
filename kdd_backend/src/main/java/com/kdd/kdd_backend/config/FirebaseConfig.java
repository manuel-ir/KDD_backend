package com.kdd.kdd_backend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

/**
 * Configuracion de Firebase Admin SDK.
 *
 * Inicializa la conexion con Firebase usando el archivo de credenciales
 * del servidor (firebase-service-account.json). Este archivo NUNCA debe
 * subirse a GitHub. Se usa para verificar los tokens de Google Sign-In
 * que llegan desde la aplicacion Android.
 */
@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initFirebase() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            InputStream serviceAccount = null;

            // Entorno local: cargar desde archivo en resources
            serviceAccount = getClass().getClassLoader()
                    .getResourceAsStream("firebase-service-account.json");

            // Produccion (Render): cargar desde variable de entorno en base64
            if (serviceAccount == null) {
                String credencialesBase64 = System.getenv("FIREBASE_CREDENTIALS_JSON");
                if (credencialesBase64 != null && !credencialesBase64.isBlank()) {
                    byte[] credencialesBytes = Base64.getDecoder().decode(credencialesBase64);
                    serviceAccount = new ByteArrayInputStream(credencialesBytes);
                }
            }

            if (serviceAccount == null) {
                throw new IllegalStateException(
                        "No se encontraron credenciales de Firebase. " +
                        "En local: añade firebase-service-account.json a src/main/resources/. " +
                        "En produccion: configura la variable de entorno FIREBASE_CREDENTIALS_JSON."
                );
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);
            System.out.println("Firebase Admin SDK inicializado correctamente");
        }
    }
}
