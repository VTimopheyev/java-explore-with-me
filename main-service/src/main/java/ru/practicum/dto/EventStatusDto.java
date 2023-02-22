package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.status.ParticipationRequestStatus;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventStatusDto {

    private List<Long> requestIds;
    @Enumerated(EnumType.STRING)
    private ParticipationRequestStatus status;
}
