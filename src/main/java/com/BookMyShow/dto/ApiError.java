package com.BookMyShow.dto;

import java.time.Instant;


//**  DTO — represents API data ****

// DTO defining the structure of an error response
public record ApiError(Instant timestamp,int status, String error,String message,String path) {

}
