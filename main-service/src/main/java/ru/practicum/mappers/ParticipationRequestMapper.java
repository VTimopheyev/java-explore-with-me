package ru.practicum.mappers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.model.ParticipationRequest;

@Component
@RequiredArgsConstructor
@Slf4j
public class ParticipationRequestMapper {
    public ParticipationRequestDto toDto(ParticipationRequest pr) {
        return new ParticipationRequestDto(
                pr.getId(),
                pr.getCreated(),
                pr.getRequester().getId(),
                pr.getEvent().getId(),
                pr.getStatus()
        );
    }

    /*public ParticipationRequest toPr(ParticipationRequestDto dto) {
        return new ParticipationRequest(
                dto.getId(),
                dto.getName(),
                dto.getEmail()
        );
    }*/
}
