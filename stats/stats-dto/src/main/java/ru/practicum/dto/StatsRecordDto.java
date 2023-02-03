package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.sql.Timestamp;

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
    @JsonFormat(pattern = "YYYY-MM-dd HH:mm:ss")
    Timestamp timestamp;
}
