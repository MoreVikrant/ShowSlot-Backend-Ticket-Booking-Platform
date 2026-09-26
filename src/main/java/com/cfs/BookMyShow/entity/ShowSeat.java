package com.cfs.BookMyShow.entity;

import jakarta.persistence.*;
import com.cfs.BookMyShow.entity.Show;

@Entity
@Table(name = "show_seats",uniqueConstraints = @UniqueConstraint(name = "uk_show_seat",columnNames = {"show_id","seatLabel"}))
/* name = uk_show_seat      name of the is simply the name of the database constraint.
You can think of it as giving your rule a name:
columnNames = show_id + seatLabel together must be unique.
 For the same show:
  seatLabel must not repeat*/

public class ShowSeat {   //that seat's availability/booking information for a particular show.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    private Show show;

    private String seatLabel;

     private boolean reserved;

    public ShowSeat() {

    }

    public ShowSeat(Show show, String seatLabel) {
        this.show = show;
        this.seatLabel = seatLabel;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isReserved(){
        return reserved;
    }

     public String getSeatLabel(){
        return seatLabel;
     }

     public void reserve(){
        reserved = true;
     }

     public void release(){
        reserved = false;
     } // here we are releasing the particular seats

}
