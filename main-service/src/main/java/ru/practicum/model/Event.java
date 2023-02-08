package ru.practicum.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.practicum.status.EventStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotNull
    private String annotation;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;
    private LocalDateTime createdOn;
    private String description;
    @NotNull
    private LocalDateTime eventDate;
    @OneToOne
    @JoinColumn(name = "location_id")
    private Location location;
    @NotNull
    private boolean paid;
    private Long participantLimit;
    private boolean requestModeration;
    private LocalDateTime publishedOn;
    private EventStatus state;
    @NotNull
    private String title;


}
