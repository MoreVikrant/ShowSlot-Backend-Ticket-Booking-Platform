package com.ShowSlot.security;

import com.ShowSlot.entity.Customer;
import com.ShowSlot.repository.CustomerRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class CustomerUserDetailsService implements UserDetailsService {

    private final CustomerRepository customerRepository;

    public CustomerUserDetailsService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override   // called by Spring during authenticate(); "identifier" is the email OR the phone typed at login
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        String value = identifier == null ? "" : identifier.trim();

        Customer customer = value.contains("@")
                ? customerRepository.findByEmail(value.toLowerCase(Locale.ROOT)).orElse(null)
                : customerRepository.findByPhone(value.replaceAll("\\D", "")).orElse(null);

        // old rows have no password: building a User with a null password would throw
        if (customer == null || customer.getPassword() == null) {
            throw new UsernameNotFoundException("Customer not found");
        }

        return User.withUsername(customer.getEmail())     // username is always the EMAIL, so the token's subject is the email
                .password(customer.getPassword())
                .roles(customer.getRoles().name())        // adds the ROLE_ prefix
                .build();
    }
}