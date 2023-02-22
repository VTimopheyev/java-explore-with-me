package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.status.EventStatus;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {
    private String annotation;
    private Long category;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp eventDate;
    private LocationDto location;
    private boolean paid;
    private int participantLimit;
    private boolean requestModeration;
    private String title;
    private EventStatus state;
    private String stateAction;
}
