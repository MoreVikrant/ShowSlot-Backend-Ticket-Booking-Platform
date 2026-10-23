package com.ShowSlot.controller;

// WORKS ON THE SINGLE RESPONSIBILTY PRINCIPLE

import com.ShowSlot.dto.BookingResponse;
import com.ShowSlot.dto.CreateBookingRequest;
import com.ShowSlot.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/shows/{showId}")
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse book(@PathVariable Long showId,
                                @Valid @RequestBody CreateBookingRequest request,
                                Principal principal) {
        return bookingService.book(showId, request, principal.getName());
    }

    @PostMapping("/{bookingId}/cancel")
    public BookingResponse cancel(@PathVariable Long bookingId, Principal principal) {
        return bookingService.cancel(bookingId, principal.getName());
    }

    @GetMapping("/{bookingId}")
    public BookingResponse find(@PathVariable Long bookingId, Principal principal) {
        return bookingService.find(bookingId, principal.getName());
    }
}


