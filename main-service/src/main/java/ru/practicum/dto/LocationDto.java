package ru.practicum.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class LocationDto {
    @NotNull
    private float lon;
    @NotNull
    private float lat;
}

