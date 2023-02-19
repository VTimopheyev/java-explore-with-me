package ru.practicum.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class StatsDto {
    String app;
    String uri;
    int hits;

}
