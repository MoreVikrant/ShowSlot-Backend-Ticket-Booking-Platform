package com.BookMyShow.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "shows")
public class Show {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) // multiple shows can run the same movie && one movie can run in multiple shows
    private Movie movie;  // because show contains the movie

    @ManyToOne(fetch = FetchType.LAZY,optional = false) // multiple shows can be in the same theatre
    private Theatre theatre; // because show is running in the theatre

    private LocalDateTime startsAt;

    private LocalDateTime endsAt;

    private BigDecimal ticketPrice;

    private int totalSeats;

    private int availableSeats;

    private boolean active = true;        // is show active in the theatre

    @Version
    private long version;

    public Show() {
    }

    public Show(Movie movie, Theatre theatre, LocalDateTime startsAt, LocalDateTime endsAt, int totalSeats, BigDecimal ticketPrice ) {
        this.movie = movie;
        this.theatre = theatre;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.totalSeats = totalSeats;
        this.ticketPrice = ticketPrice;
        this.availableSeats = availableSeats;  // removed available seats because intially the total seats are the available seats
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public Theatre getTheatre() {
        return theatre;
    }

    public void setTheatre(Theatre theatre) {
        this.theatre = theatre;
    }

    public LocalDateTime getStartsAt() {
        return startsAt;
    }

    public void setStartsAt(LocalDateTime startsAt) {
        this.startsAt = startsAt;
    }

    public LocalDateTime getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(LocalDateTime endsAt) {
        this.endsAt = endsAt;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

      public void reserve(int seats){ //seats is simply a number that tells the method how many seats someone wants to reserve.

        if(seats <= 0 || seats > availableSeats){
            throw new IllegalArgumentException("Not enough seats");
        }
        availableSeats -= seats;
      }

                   //release -  cancel their booking.
      public void release(int seats){
        availableSeats = availableSeats+seats; // The previously reserved seats are being returned/cancelled, so increase the available seat count.
      }  // In these method we are just increasing the no of available seats

    // we dont need this much getter and setters make the only needed one but this is our first project so we are making this


    public BigDecimal getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(BigDecimal ticketPrice) {
        this.ticketPrice = ticketPrice;
    }
}
