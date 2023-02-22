package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventCompilationDisplayDto {
    private Long id;
    private List<EventFullDto> events;
    private boolean pinned;
    private String title;

}
