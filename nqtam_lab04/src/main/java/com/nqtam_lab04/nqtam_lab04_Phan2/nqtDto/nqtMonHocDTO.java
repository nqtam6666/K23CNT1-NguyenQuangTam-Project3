package com.nqtam_lab04.nqtam_lab04_Phan2.nqtDto;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class nqtMonHocDTO {
    
    @NotBlank(message = "Mamh cannot be empty")
    @Size(min = 2, max = 2, message = "Mamh must be exactly 2 characters")
    String mamh; // subject code
    
    @NotBlank(message = "Tenmh cannot be empty")
    @Size(min = 5, max = 50, message = "Tenmh must be between 5 and 50 characters")
    String tenmh; // subject name
    
    @NotNull(message = "Sotiet cannot be null")
    @Min(value = 45, message = "Sotiet must be at least 45")
    @Max(value = 75, message = "Sotiet must be at most 75")
    Integer sotiet; // number of periods/credits
}

