package ru.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "compilations")
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class EventCompilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String eventIds;
    private boolean pinned;
    private String title;
}
