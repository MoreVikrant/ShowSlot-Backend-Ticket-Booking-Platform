package com.BookMyShow.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

// For admin we are Creating this

public record CreateMovieRequest(@NotBlank @Size(max = 200) String title,
                                 @NotBlank String language,
                                 @NotBlank String genre,
                                 @NotNull @Positive Integer durationInMin,
                                 @Size(max = 255) String description,
                                 String certificate, String posterUrl, String trailerUrl) {

}
