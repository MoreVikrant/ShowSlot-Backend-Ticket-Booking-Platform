package com.cfs.BookMyShow.repository;

import com.cfs.BookMyShow.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// ** The Repository is the layer responsible for communicating with the database.**
// Perform database operations for the Booking entity.

public interface BookingRepository extends JpaRepository<Booking,Long> {

/*    Spring Data JPA derives the query from the method name.
    This feature is called Query Derivation / Derived Query Methods.    */

    /*      List<Booking> - Because one customer can have many bookings.
      Find all bookings belonging to this customer phone number
     and return them with the newest booking first.                */
     List<Booking> findByCustomerPhoneOrderByBookedAtDesc(String customerPhone);


     Optional<Booking> findByIdAndCustomerPhone(Long id, String customerPhone);
 /*    Find a booking whose ID matches AND whose customer phone matches.
     This is actually important from a backend/security perspective.
     because if someone knows someone's booking id and trying to access other customers booking
     then he can't access it
     Optional<Booking> - expecting at most one booking.          */

    List<Booking> findByCustomerIdOrderByBookedAtDesc(Long customerId);
    // Find all bookings for a particular customer ID and sort them by booking time descending.


    Optional<Booking> findByIdAndCustomerId(Long id, Long customerId);
  //  Find the booking with this ID AND make sure it belongs to this customer.
}
