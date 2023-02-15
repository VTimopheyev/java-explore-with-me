package ru.practicum.model;

import lombok.*;

import javax.persistence.*;

@Data
@Entity
@Table(name = "compilations")
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class EventCompilation {
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.springframework.data.annotation.Id
    private Long id;
    @Column(name = "event_ids")
    private String eventIds;
    @Column(name = "pinned")
    private boolean pinned;
    @Column(name = "title")
    private String title;
}
