package ru.practicum.mappers;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.dto.EventCompilationDisplayDto;
import ru.practicum.dto.EventFullDto;
import ru.practicum.model.EventCompilation;

import java.util.List;


@Component
@NoArgsConstructor
@Slf4j
public class EventCompilationMapper {

    public EventCompilationDisplayDto toFullDto(EventCompilation ec, List<EventFullDto> events){
        return new EventCompilationDisplayDto(events, ec.isPinned(), ec.getTitle());
    }

}
