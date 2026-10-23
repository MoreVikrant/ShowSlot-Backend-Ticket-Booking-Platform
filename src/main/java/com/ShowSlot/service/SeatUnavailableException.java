package com.ShowSlot.service;


public class SeatUnavailableException extends RuntimeException{

    public SeatUnavailableException(String msg){
        super(msg);
    }
}
