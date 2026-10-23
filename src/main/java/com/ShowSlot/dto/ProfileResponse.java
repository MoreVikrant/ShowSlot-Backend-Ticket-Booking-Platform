package com.ShowSlot.dto;

import com.ShowSlot.entity.Customer;

public record ProfileResponse(
        Long id, String name, String email, String phone,String role
) {
    public static ProfileResponse from(Customer customer){
        return new ProfileResponse(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getRoles().name());
    }


}
