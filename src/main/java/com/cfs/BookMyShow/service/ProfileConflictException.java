package com.cfs.BookMyShow.service;


// when while the sign up the user will enter the same number and email
public class ProfileConflictException extends RuntimeException{

    public ProfileConflictException(String msg)
    {
        super(msg);
    }
}
