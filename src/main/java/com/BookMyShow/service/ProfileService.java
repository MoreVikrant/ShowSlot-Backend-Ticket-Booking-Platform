package com.BookMyShow.service;

import com.BookMyShow.dto.CreateProfileRequest;
import com.BookMyShow.dto.ProfileResponse;
import com.BookMyShow.entity.Customer;
import com.BookMyShow.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.Locale;
@Service
public class ProfileService {

    private final CustomerRepository customerRepository;

    public ProfileService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public ProfileResponse create(CreateProfileRequest request){

        String email = request.email().trim().toLowerCase(Locale.ROOT);
        String phone = normalizePhone(request.phone());

        if(customerRepository.existsByEmail(email)){
            throw  new ProfileConflictException("Profile with this email already exists");
        }

        if(customerRepository.existsByPhone(phone)){
            throw new ProfileConflictException("Profile associated with this phone number already exists ");
        }
        return ProfileResponse.from(customerRepository.save(new Customer(request.name().trim(),email,phone)));
   /*     CreateProfileRequest
       ↓
        New Customer
        (name, email, phone) */
    }
                             //String identifier → the user provides either: email or phone
    public ProfileResponse login(String identifier) {

         String value = identifier == null ? "" : identifier.trim();   // \\D - non digit character
         String phone = value.replaceAll("\\D","");  // replace all non digit character with ""
        Customer customer = value.contains("@")
                ? customerRepository.findByEmail(value.toLowerCase(Locale.ROOT)).orElse(null)
                : customerRepository.findByPhone(phone).orElse(null);

        if(customer == null){
            throw new ResourceNotFoundException("NO profile found for this phone number or email");
        }
        return ProfileResponse.from(customer);
    }

    public String normalizePhone(String phone)
    {
        String normalize=phone==null ? "" : phone.replaceAll("\\D","");
        if(normalize.length()<10 || normalize.length()>15)
        {
            throw  new IllegalArgumentException("Enter valid Phone number");
        }
        return normalize;
    }
}
