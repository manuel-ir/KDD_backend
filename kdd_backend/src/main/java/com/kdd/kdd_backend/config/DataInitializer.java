package com.kdd.kdd_backend.config;

import com.kdd.kdd_backend.model.Categoria;
import com.kdd.kdd_backend.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final CategoriaRepository categoriaRepository;

    private static final List<String> CATEGORIAS = List.of(
            "Deportes", "Naturaleza", "Fiesta", "Música", "Arte y Cultura",
            "Gastronomía", "Viajes", "Tecnología", "Cine y Series", "Fotografía",
            "Juegos", "Lectura", "Idiomas", "Voluntariado", "Personalizada"
    );

    @Override
    public void run(ApplicationArguments args) {
        Set<String> existentes = categoriaRepository.findAll()
                .stream()
                .map(Categoria::getTipo)
                .collect(Collectors.toSet());

        CATEGORIAS.stream()
                .filter(tipo -> !existentes.contains(tipo))
                .forEach(tipo -> {
                    Categoria c = new Categoria();
                    c.setTipo(tipo);
                    categoriaRepository.save(c);
                });
    }
}
