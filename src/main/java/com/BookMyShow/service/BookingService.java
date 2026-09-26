package com.BookMyShow.service;

import com.BookMyShow.dto.BookingResponse;
import com.BookMyShow.dto.CreateBookingRequest;
import com.BookMyShow.entity.Booking;
import com.BookMyShow.entity.BookingStatus;
import com.BookMyShow.entity.Show;
import com.BookMyShow.entity.ShowSeat;
import com.BookMyShow.repository.BookingRepository;
import com.BookMyShow.repository.CustomerRepository;
import com.BookMyShow.repository.ShowRepository;
import com.BookMyShow.repository.ShowSeatRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;


@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ShowSeatRepository showSeatRepository;
    private final ShowRepository showRepository;
    private final CustomerRepository customerRepository;

    // constructor DI
    public BookingService(BookingRepository bookingRepository, ShowSeatRepository showSeatRepository, ShowRepository showRepository, CustomerRepository customerRepository) {
        this.bookingRepository = bookingRepository;
        this.showSeatRepository = showSeatRepository;
        this.showRepository = showRepository;
        this.customerRepository = customerRepository;
    }

    // Your method doesn't return the Booking entity directly.
       @Transactional                                     // This contains the information sent by the client.
    public BookingResponse book(Long showId, CreateBookingRequest request){

        // If the show exists, give me the Show, If it doesn't exist, throw ResourceNotFoundException.
        Show show = showRepository.findById(showId).orElseThrow(()->  new ResourceNotFoundException("show not found"));

        // find the customer
        var customer = customerRepository.findById(request.profileId()).orElseThrow(() ->
                new ResourceNotFoundException("Profile not found"));

/*         FIND THE SEATLABELS
        This is doing input normalization.
        .stream() - converts the list into a Java Stream.
         .map(...) - Take every seat label and transform it into something else.
        Locale.ROOT provides a locale-neutral conversion, which is appropriate for technical identifiers like seat labels.
        .toList() - converts the Stream back into a List.
          example  - [" j1 ", "j3", " k1"]  to ["J1", "J3", "K1"]  making the input consistent before processing it                      */
        List<String> labels = request.seatLabels().stream().
                map(label -> label.trim().toUpperCase(Locale.ROOT)).toList();


        //This checks whether the customer selected the same seat more than once.
        if(labels.stream().distinct().count() != labels.size()){
            throw new SeatUnavailableException("Duplicate seats are not allowed");
            // "Stop the booking process because the requested seat selection is invalid."
        }

// Give me the ShowSeat records for this show and these labels, in a way that protects them while this transaction is processing."
        List<ShowSeat> seats = showSeatRepository.findForUpdate(showId,labels);   // findForUpdate does the above

        if(seats.size()!= labels.size() || seats.stream().anyMatch(ShowSeat::isReserved))
             //This checks whether at least one selected seat is already reserved.
            // method reference.shorthand for:seat -> seat.isReserved()
        {
            throw new SeatUnavailableException("One or more selected seats are unavailable");
        }
        seats.forEach(ShowSeat::reserve); // So each seat gets its reserve() method called.
        show.reserve(labels.size()); // This updates the show's seat availability.
        // The exact implementation depends on your Show.reserve() method, but conceptually that's what it's doing.

        BigDecimal total = show.getTicketPrice().multiply(BigDecimal.valueOf(labels.size()));

        Booking booking = bookingRepository.save(new Booking(show, customer, total, labels));
        return BookingResponse.from(booking);
    }

    //Find a booking by ID → throw an error if it doesn't exist → convert the entity to BookingResponse → return it.
    @Transactional
    public BookingResponse find(Long bookingId)  {
        Booking booking  = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        return BookingResponse.from(booking);
    }

    @Transactional
    public List<BookingResponse> findByProfileId(Long profileId)
    {
        if (!customerRepository.existsById(profileId))
        {
            throw new ResourceNotFoundException("Profile not found");
        }

        List<BookingResponse> list1 = bookingRepository.
                findByCustomerIdOrderByBookedAtDesc(profileId).stream()
                .map(BookingResponse::from).toList();
        return list1;
    }
    // Given a profile ID, first check that the customer exists.
    // If they don't exist, throw an error. If they do exist,
    // find all their bookings, sort them from newest to oldest,
    // convert each Booking entity into a BookingResponse DTO,
    // put those responses into a list, and return the list.

    @Transactional
    public BookingResponse cancel(Long bookingId,Long profileId)
    {
        Booking booking = bookingRepository.findByIdAndCustomerId(bookingId, profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        if(booking.getStatus()== BookingStatus.CONFIRMED)
        {
            List<ShowSeat> seats = showSeatRepository.findForUpdate(booking.getShow().getId(), booking.getSeatLabels());
            seats.forEach(ShowSeat::release);
            booking.getShow().release(booking.getSeatLabels().size());
            booking.cancel();
        }
        return BookingResponse.from(booking);
    }
}


