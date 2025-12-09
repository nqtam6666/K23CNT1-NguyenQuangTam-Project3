package com.nqtam_lab04.nqtam_lab04_Phan2.nqtDto;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class nqtEmployeeDTO {
    
    // Id is auto-incrementing, so no validation needed in DTO
    
    @NotBlank(message = "Full name cannot be empty")
    @Size(min = 3, max = 25, message = "Full name must be between 3 and 25 characters")
    String fullName;
    
    String gender;
    
    @NotNull(message = "Age cannot be null")
    @Min(value = 18, message = "Age must be at least 18")
    @Max(value = 60, message = "Age must be at most 60")
    Integer age;
    
    @NotNull(message = "Salary cannot be null")
    @Positive(message = "Salary must be greater than 0")
    Double salary;
}

