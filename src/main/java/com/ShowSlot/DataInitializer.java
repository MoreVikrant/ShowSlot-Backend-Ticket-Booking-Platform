package com.ShowSlot;

import com.ShowSlot.entity.Customer;
import com.ShowSlot.entity.Role;
import com.ShowSlot.repository.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedAdmin(CustomerRepository repository, PasswordEncoder encoder) {
        return args -> {
            String email = "admin@showslot.com";
            if (repository.existsByEmail(email)) return;

            Customer admin = new Customer("Admin", email, "9999999999");   // change if this phone exists
            admin.setPassword(encoder.encode("admin12345"));               // dev password only
            admin.setRoles(Role.ADMIN);
            repository.save(admin);
        };
    }
}