package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {
    @NotNull
    private String annotation;
    @NotNull
    private long category;
    @NotNull
    private String description;
    @NotNull
    @Future
    private LocalDateTime eventDate;
    @NotNull
    private LocationDto location;
    private boolean paid;
    private long participantLimit;
    private boolean requestModeration;
    private String title;
}
