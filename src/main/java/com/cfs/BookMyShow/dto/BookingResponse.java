package com.cfs.BookMyShow.dto;
 // This record presents
//  ** The data about a booking that you want to send to the client/frontend.  **
// thats why it is named booking response
// ** When backend sends data back to frontend:     its a response

import com.cfs.BookMyShow.entity.Booking;
import com.cfs.BookMyShow.entity.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// "When I send booking information to the frontend, I only want these fields."
public record BookingResponse
        (Long id , Long showId, Long profileId, String movieTitle,String theatreName,String customerName,
         String customerEmail, String customerPhone, List<String> seatLabels,
         BigDecimal totalAmount, BookingStatus status, LocalDateTime bookedAt
                              ) {
    // "Create a BookingResponse from this Booking object."
    public static BookingResponse from (Booking booking)          // from is method
    {
        return new BookingResponse(booking.getId(),
                booking.getShow().getId(),  //BookingResponse.id
                booking.getCustomer()==null ? null : booking.getCustomer().getId(),
                booking.getShow().getMovie().getTitle(),
                booking.getShow().getTheatre().getName(),
                booking.getCustomerName(),booking.getCustomerEmail(),
                booking.getCustomerPhone(),
                booking.getSeatLabels(),
                booking.getTotalAmount(),
                booking.getStatus(),
                booking.getBookedAt());
    }


}
