package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.status.ParticipationRequestStatus;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationRequestDto {

    private long id;
    private LocalDateTime created;
    @NotNull
    private long requester;
    @NotNull
    private long event;
    private ParticipationRequestStatus status;

}
