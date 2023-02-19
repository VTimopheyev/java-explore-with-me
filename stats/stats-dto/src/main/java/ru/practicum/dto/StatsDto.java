package ru.practicum.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class StatsDto {
    String app;
    String uri;
    int hits;

}
