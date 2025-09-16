package com.setianjay.models.response.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDatesResponse {
    private String checkin;
    private String checkout;
}
