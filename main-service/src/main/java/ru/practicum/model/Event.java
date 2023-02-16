package ru.practicum.model;

import lombok.*;
import ru.practicum.status.EventStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "events")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Event {
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.springframework.data.annotation.Id
    private Long id;
    @NotNull
    @Column(name = "annotation")
    private String annotation;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    @Column(name = "description")
    private String description;
    @NotNull
    @Column(name = "event_date")
    private LocalDateTime eventDate;
    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;
    @NotNull
    @Column(name = "paid")
    private boolean paid;
    @Column(name = "participant_limit")
    private int participantLimit;
    @Column(name = "request_moderation")
    private boolean requestModeration;
    @Column(name = "published_on")
    private LocalDateTime publishedOn;
    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private EventStatus state;
    @NotNull
    @Column(name = "title")
    private String title;
}
