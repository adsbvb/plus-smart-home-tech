package ru.yandex.practicum.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DimensionDto {
    @NotNull
    @DecimalMin(value = "1.0")
    Double width;

    @NotNull
    @DecimalMin(value = "1.0")
    Double height;

    @NotNull
    @DecimalMin(value = "1.0")
    Double depth;
}
