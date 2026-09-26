package com.cfs.BookMyShow.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// it is used when a user wants to create a profile/customer.
// Think of it as defining the rules for incoming profile data.
public record CreateProfileRequest(

        @NotBlank @Size(max = 8) String name, // Name must be provided and must contain at most 8 characters.

        @NotBlank @Email @Size(max =200) String email,
        // @Email - checks whether the value looks like an email address.
        // It does not check: Is this email already registered?
        //protecting your API from unnecessarily large input. max  200 character

        @NotBlank @Pattern(regexp = "^[0-9+()-]{10,20}$") String phone ) {
/*       ^ - Start of the string.
         [0-9+()-] - 0 1 2 3 4 5 6 7 8 9
              +
              (
              )
              -
       {10,20} The entire value must contain between 10 and 20 allowed characters.
        $ - End of the string.        */
}
