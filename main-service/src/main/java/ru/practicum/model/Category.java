package ru.practicum.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
@Data
@Entity
@Table(name = "categories")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Category {
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.springframework.data.annotation.Id
    private Long id;
    @NotNull
    @NotEmpty
    @Column(name = "name")
    private String name;
}
