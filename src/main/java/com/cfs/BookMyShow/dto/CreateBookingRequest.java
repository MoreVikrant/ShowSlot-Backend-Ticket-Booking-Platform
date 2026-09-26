package com.cfs.BookMyShow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

// Suppose the frontend sends a request to your backend:
//So this record represents the input required to create a booking.
// profile id , seatLabel

public record CreateBookingRequest(

        @NotNull Long profileId, // this represents which profile doing the booking means must not be null

        @NotEmpty @Size(max = 8) List<@NotBlank String> seatLabels ) {
   // The list cannot be null and cannot be empty.
    //The customer can book a maximum of 8 seats in one booking.
  //  The List contains String values, and every individual String must satisfy @NotBlank.
  /*  @NotBlank - checks that a string:
    is not null
    is not empty
    is not only whitespace*/


}

/*  @NotNull Long profileId,
    @NotEmpty @Size(max = 8) List<@NotBlank String> seatLabels
 */
