package com.BookMyShow.dto.admin;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateShowRequest(
        @NotNull  Long movieId,
        @NotNull Long theatreId,
        @NotNull @Future LocalDateTime startsAt,
        @NotNull @Positive BigDecimal ticketPrice,
        @Min(1) @Max(26) int rows,          // rows A..Z
        @Min(1) @Max(30) int seatsPerRow
) {
}
