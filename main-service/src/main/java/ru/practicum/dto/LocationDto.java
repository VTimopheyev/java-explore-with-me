package ru.practicum.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class LocationDto {
    private long id;
    @NotNull
    private float longitude;
    @NotNull
    private float latitude;
}

