package com.setianjay.models.request.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingAuthRequest {
    private String username;
    private String password;
}
