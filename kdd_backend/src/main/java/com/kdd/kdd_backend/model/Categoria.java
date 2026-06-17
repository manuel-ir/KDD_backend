package com.kdd.kdd_backend.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad que representa la categoria de un plan.
 *
 * Mapea la tabla "categorias". Las categorias se crean automaticamente
 * cuando se crea un plan con una categoria nueva (ej: Deportes, Musica...).
 * El campo "tipo" almacena el nombre de la categoria como texto.
 */
@Entity
@Table(name = "categorias")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Long id;

    @Column(nullable = false)
    private String tipo;
}
