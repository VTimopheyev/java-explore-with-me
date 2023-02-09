package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.dto.EventCompilationDisplayDto;
import ru.practicum.dto.EventCompilationDto;
import ru.practicum.dto.EventFullDto;
import ru.practicum.exceptions.CompilationNotFoundException;
import ru.practicum.mappers.EventCompilationMapper;
import ru.practicum.mappers.EventMapper;
import ru.practicum.mappers.UserMapper;
import ru.practicum.model.EventCompilation;
import ru.practicum.repositories.EventCompilationRepository;
import ru.practicum.repositories.EventRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventCompilationServiceImpl implements EventCompilationService {
    private final EventCompilationRepository eventCompilationRepository;
    private final EventCompilationMapper eventCompilationMapper;
    private final EventRepository eventRepository;
    private final EventServiceImpl eventService;
    private final UserMapper userMapper;
    private final EventMapper eventMapper;

    public EventCompilationDisplayDto createNewEventCompilation(EventCompilationDto eventCompilationDto) {
        String eventIds = eventCompilationDto.getEvents().stream().map(Object::toString)
                .collect(Collectors.joining(", "));

        EventCompilation eventCompilation = new EventCompilation();
        eventCompilation.setEventIds(eventIds);
        eventCompilation.setPinned(eventCompilationDto.isPinned());
        eventCompilation.setTitle(eventCompilationDto.getTitle());

        List<EventFullDto> events = eventRepository.findByIdIn(eventCompilationDto.getEvents())
                .stream()
                .map(e -> eventMapper.toFullDto(e, eventService.getConfirmedRequests(e),
                        userMapper.toUserDto(e.getInitiator()),
                        eventService.getViewsOFEvent(e)))
                .collect(Collectors.toList());

        return eventCompilationMapper.toFullDto(eventCompilationRepository.save(eventCompilation), events);
    }

    public EventCompilationDisplayDto updateEventCompilationByAdmin(EventCompilationDto eventCompilationDto, long compId) {

        EventCompilation evComp = eventCompilationRepository.findById(compId)
                .orElseThrow(CompilationNotFoundException::new);

        List<EventFullDto> events = new ArrayList<>();

        if (!eventCompilationDto.getEvents().isEmpty()){
            String eventIds = eventCompilationDto.getEvents().stream().map(Object::toString)
                    .collect(Collectors.joining(", "));

            evComp.setEventIds(eventIds);
            events = eventRepository.findByIdIn(eventCompilationDto.getEvents())
                    .stream()
                    .map(e -> eventMapper.toFullDto(e, eventService.getConfirmedRequests(e),
                            userMapper.toUserDto(e.getInitiator()),
                            eventService.getViewsOFEvent(e)))
                    .collect(Collectors.toList());
        }else{
            List<Long> ids = new ArrayList<>();
            String[] idsAsArray = evComp.getEventIds().split(",");
            for (String s : idsAsArray){
                ids.add(Long.parseLong(s));
            }
            events = eventRepository.findByIdIn(ids)
                    .stream()
                    .map(e -> eventMapper.toFullDto(e, eventService.getConfirmedRequests(e),
                            userMapper.toUserDto(e.getInitiator()),
                            eventService.getViewsOFEvent(e)))
                    .collect(Collectors.toList());
        }

        evComp.setPinned(eventCompilationDto.isPinned());
        evComp.setTitle(eventCompilationDto.getTitle());

        return eventCompilationMapper.toFullDto(eventCompilationRepository.save(evComp), events);
    }

    public EventCompilation deleteEventCompilation(long compId) {
        EventCompilation evComp = eventCompilationRepository.findById(compId)
                .orElseThrow(CompilationNotFoundException::new);

        eventCompilationRepository.delete(evComp);
        return evComp;
    }

    public Collection<EventCompilationDisplayDto> getCompilationsByAnyUser(boolean pinned, int from, int size) {
        PageRequest pr = PageRequest.of((from / size), size);

       return eventCompilationRepository.findByPinnedEquals(pinned, pr)
                .stream()
                .filter(e -> e.isPinned() == pinned)
                .map(e -> eventCompilationMapper.toFullDto(e,
                        convertStringToFullEventDto(e.getEventIds())))
                .collect(Collectors.toList());
    }

    private List<EventFullDto> convertStringToFullEventDto(String eventsId){
        List<Long> ids = new ArrayList<>();
        String[] idsAsArray = eventsId.split(",");
        for (String s : idsAsArray){
            ids.add(Long.parseLong(s));
        }
        return eventRepository.findByIdIn(ids)
                .stream()
                .map(e -> eventMapper.toFullDto(e, eventService.getConfirmedRequests(e),
                        userMapper.toUserDto(e.getInitiator()),
                        eventService.getViewsOFEvent(e)))
                .collect(Collectors.toList());
    }

    public EventCompilationDisplayDto getParticularCompilationByAnyUser(long compId) {
        EventCompilation evComp = eventCompilationRepository.findById(compId)
                .orElseThrow(CompilationNotFoundException::new);

        return eventCompilationMapper.toFullDto(evComp, convertStringToFullEventDto(evComp.getEventIds()));
    }
}
