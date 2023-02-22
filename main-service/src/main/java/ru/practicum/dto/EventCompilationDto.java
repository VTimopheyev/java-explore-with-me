package ru.practicum.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class EventCompilationDto {
    @NotNull
    private List<Long> events;
    @NotNull
    private boolean pinned;
    @NotNull
    private String title;
}
