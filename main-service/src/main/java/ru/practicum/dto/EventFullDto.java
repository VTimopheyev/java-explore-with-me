package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.model.Category;
import ru.practicum.model.Location;
import ru.practicum.status.EventStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventFullDto {
    private long id;
    private String annotation;
    private Category category;
    private int confirmedRequests;
    private LocalDateTime createdOn;
    private String description;
    private LocalDateTime eventDate;
    private UserDto initiator;
    private Location location;
    private boolean paid;
    private Long participantLimit;
    private LocalDateTime publishedOn;
    private boolean requestModeration;
    private EventStatus state;
    private String title;
    private int views;

}
