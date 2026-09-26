package com.cfs.BookMyShow.service;


public class SeatUnavailableException extends RuntimeException{

    public SeatUnavailableException(String msg){
        super(msg);
    }
}
