package ru.practicum.mappers;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.UserDto;
import ru.practicum.model.Event;

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
                event.getCreatedOn(),
                event.getDescription(),
                event.getEventDate(),
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
