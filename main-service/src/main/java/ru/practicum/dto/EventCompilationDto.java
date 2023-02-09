package ru.practicum.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class EventCompilationDto {
    private List<Long> events;
    private boolean pinned;
    private String title;

}
