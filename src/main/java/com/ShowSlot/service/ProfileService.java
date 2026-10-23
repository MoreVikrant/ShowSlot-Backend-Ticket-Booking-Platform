package com.ShowSlot.service;

import com.ShowSlot.dto.CreateProfileRequest;
import com.ShowSlot.dto.ProfileResponse;
import com.ShowSlot.entity.Customer;
import com.ShowSlot.repository.CustomerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;
@Service
public class ProfileService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public ProfileService(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
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
        Customer customer = new Customer(request.name().trim(), email, phone);
        customer.setPassword(passwordEncoder.encode(request.password()));
        return ProfileResponse.from(customerRepository.save(customer));
    }
                             //String identifier → the user provides either: email or phone

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
