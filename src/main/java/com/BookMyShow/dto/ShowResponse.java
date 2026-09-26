package com.BookMyShow.dto;

import com.BookMyShow.entity.Show;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

//This is the response that your API sends to the client.
//ShowResponse contains information that the frontend needs.

public record ShowResponse(Long id, MovieResponse movie, TheatreResponse theatre,
                           LocalDateTime startAt,
                           LocalDateTime endAt, BigDecimal ticketPrice, int totalSeats,
                           int availableSeats,
                           List<String> availableSeatsLabels)
{
    public static ShowResponse from(Show show, List<String> availableSeatsLabels){
        return new ShowResponse(show.getId(),
                MovieResponse.from(show.getMovie()),
                TheatreResponse.from(show.getTheatre()),
                show.getStartsAt(),
                show.getEndsAt(),
                show.getTicketPrice(),
                show.getTotalSeats(),
                show.getAvailableSeats(),
                availableSeatsLabels);
    }
}

/*
 if we would have done show.availableSeatsLabels -> it had given us number of the available seats
 availableSeatsLabels - ["A1", "A2", "A3", "B1", "B2", ...]
 These specific seats are available.
The list usually needs to be calculated by checking which seats have already been booked
That's why it is supplied separately to from().     */
