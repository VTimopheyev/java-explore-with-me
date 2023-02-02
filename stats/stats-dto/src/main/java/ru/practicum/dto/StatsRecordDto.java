package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StatsRecordDto {
    @NotNull
    String app;
    @NotNull
    String uri;
    @NotNull
    String ip;
    @PastOrPresent
    LocalDateTime timestamp;
}
