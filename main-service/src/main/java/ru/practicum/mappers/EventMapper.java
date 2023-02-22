package ru.practicum.mappers;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.UserDto;
import ru.practicum.model.Event;

import java.sql.Timestamp;

@Component
@NoArgsConstructor
@Slf4j
public class EventMapper {

    public EventFullDto toFullDto(Event event,
                                  int confirmedRequests,
                                  UserDto initiator,
                                  int views) {
        return new EventFullDto(
                event.getId(),
                event.getAnnotation(),
                event.getCategory(),
                confirmedRequests,
                Timestamp.valueOf(event.getCreatedOn()),
                event.getDescription(),
                Timestamp.valueOf(event.getEventDate()),
                initiator,
                event.getLocation(),
                event.isPaid(),
                event.getParticipantLimit(),
                event.getPublishedOn(),
                event.isRequestModeration(),
                event.getState(),
                event.getTitle(),
                views
        );
    }
}
