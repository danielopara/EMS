package com.danielopara.EMS.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEmployee {
    private String firstName;
    private String lastName;
    private String middleName;
    private String phoneNumber;
}
