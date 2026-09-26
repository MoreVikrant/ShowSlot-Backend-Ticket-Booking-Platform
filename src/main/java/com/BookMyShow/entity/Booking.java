package com.BookMyShow.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Booking can have differnet status so we will create an enum fo them
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    @ManyToOne(fetch = FetchType.LAZY, optional  = false)
    private Show show ;

    @ManyToOne(fetch = FetchType.LAZY)
    private Customer customer;

    // if we have used the Customer customer them why we need to store again customer info
    //One possible reason is historical booking information. to store the history if the user changed the profile
    private String customerName;
    private String customerEmail;
    private String customerPhone;

    private BigDecimal  totalAmount; // BigDeccimal is safer

    private LocalDateTime bookedAt;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @ElementCollection //because String in List in not an entity like Show, Customer so jpa provides this for collection of such values
    // A relational database doesn't normally store a Java list directly inside one normal column.

    @CollectionTable( // that's why we created table
            name ="booking_seats",     // name of table
            joinColumns = @JoinColumn(name = "booking_id") // Use booking_id to connect each seat record back to the booking
    )
    // if we dont use the above then we need to create a seperate object with one to many relationship
    @Column(name = "seat_labels")
    private List<String> seatLabels = new ArrayList<>(); // This means a booking can contain multiple seat labels.  thats why List is used
    // because booking can have multiple labels


    public Booking() {

    }

// This constructor is used to create a new Booking object and initialize all the important booking information at once.
    public Booking(Show show, String customerName, String customerEmail, String customerPhone, BigDecimal totalAmount, List<String> seatLabels) {
        this.show = show;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.totalAmount = totalAmount;
        this.seatLabels =new ArrayList<>(seatLabels); //Make a separate copy of the seat list for every other booking

        this.bookedAt = LocalDateTime.now(); //Don't use the booking time provided by the caller. Instead, automatically use the current date and time. // we dont ask the caller when it was made
        this.status = BookingStatus.CONFIRMED;//Whenever this constructor creates a booking, its status will automatically be CONFIRMED.
    }

    public Booking(Show show,Customer customer,BigDecimal totalAmount,List<String> seatLabels){
        this(show,customer.getName(), customer.getEmail(), customer.getPhone(), totalAmount,seatLabels); // calls another constructor of the same class, constructor chaining
        this.customer = customer; // first constructor not has this actual object
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Show getShow() {
        return show;
    }

    public void setShow(Show show) {
        this.show = show;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getBookedAt() {
        return bookedAt;
    }

    public void setBookedAt(LocalDateTime bookedAt) {
        this.bookedAt = bookedAt;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public List<String> getSeatLabels() {
        return seatLabels;
    }

    public void setSeatLabels(List<String> seatLabels) {
        this.seatLabels = seatLabels;
    }

    public void cancel() {
        status = BookingStatus.CANCELLED;
    }
}
