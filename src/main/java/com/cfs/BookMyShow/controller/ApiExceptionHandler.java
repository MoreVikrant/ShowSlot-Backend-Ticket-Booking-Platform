package com.cfs.BookMyShow.controller;

import com.cfs.BookMyShow.dto.ApiError;
import com.cfs.BookMyShow.service.ResourceNotFoundException;
import com.cfs.BookMyShow.service.SeatUnavailableException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.Instant;

//  @RestControllerAdvice tells Spring:
//"This class contains exception-handling logic that should apply to my REST controllers.
@RestControllerAdvice            // Its a GLOBAL EXCEPTION HANDLER
public class ApiExceptionHandler { // Global exception handler that converts application exceptions
// into consistent HTTP error responses.

    @ExceptionHandler
 //Return an HTTP response whose body is an ApiError object
    ResponseEntity<ApiError> notFound(ResourceNotFoundException exception, HttpServletRequest request)
                                     //This is the actual exception object that was thrown.
    {                                                   // This represents the HTTP request that caused the exception.

        return error(HttpStatus.NOT_FOUND,exception.getMessage(),request);
                                                                //This allows the helper method to get the requested URL:
    }

    @ExceptionHandler({SeatUnavailableException.class,IllegalArgumentException.class})
    ResponseEntity<ApiError> conflict(RuntimeException exception,HttpServletRequest request){
         return error(HttpStatus.CONFLICT,exception.getMessage(),request);
    }

    // If incoming request data fails validation, return an HTTP 400 Bad Request response.
                      // If either of these two validation exceptions occurs, call the validation() method
    @ExceptionHandler({ConstraintViolationException.class, MethodArgumentNotValidException.class})
    ResponseEntity<ApiError> validation(Exception exception,HttpServletRequest request){
         return error(HttpStatus.BAD_REQUEST,"Request Validation failed",request);
    }

    private ResponseEntity<ApiError> error(HttpStatus status,String msg,HttpServletRequest request)
    { // current time, gets the numeric HTTP status,gets the standard text associated with the status,get the message,This gets the API path.
        return ResponseEntity.status(status).body(new ApiError(Instant.now(),status.value(),status.getReasonPhrase(),msg,request.getRequestURI()));
    }                                                 // now we are filling the field of new ApiError dto
            //specifies the actual response body.
}
