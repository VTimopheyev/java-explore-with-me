package ru.practicum.model;

import lombok.*;
import ru.practicum.status.ParticipationRequestStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "requests")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class ParticipationRequest {
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.springframework.data.annotation.Id
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ParticipationRequestStatus status;
    @Column(name = "created", nullable = false)
    private LocalDateTime created;
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
    @ManyToOne
    @JoinColumn(name = "requester_id")
    private User requester;

}
