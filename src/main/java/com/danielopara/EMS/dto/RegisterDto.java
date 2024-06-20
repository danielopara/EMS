package com.danielopara.EMS.dto;

import com.danielopara.EMS.entity.enums.Roles;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDto {
    @NotBlank(message = "first name cannot be null or blank")
    private String firstName;

    @NotBlank(message = "last name cannot be null or blank")
    private String lastName;

    private String middleName;


    @NotBlank(message = "email cannot be null or blank")
    private String email;

    @NotBlank(message = "gender cannot be null or blank")
    private String gender;

    @NotBlank(message = "phone number cannot be null or blank")
    private String phoneNumber;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate dob;

    @NotBlank(message = "password cannot be null or blank")
    private String password;
    private Roles role;
}
