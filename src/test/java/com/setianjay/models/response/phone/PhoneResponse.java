package com.setianjay.models.response.phone;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhoneResponse {
    private Integer id;
    private String name;
    private PhoneSpecificationResponse data;
}
