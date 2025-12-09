package com.nqtam_lab04.nqtam_lab04_Phan2.nqtDto;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class nqtKhoaDTO {
    
    @NotBlank(message = "Makh cannot be empty")
    @Size(min = 2, message = "Makh must be at least 2 characters")
    String makh; // department code
    
    @NotBlank(message = "Tenkh cannot be empty")
    @Size(min = 5, max = 25, message = "Tenkh must be between 5 and 25 characters")
    String tenkh; // department name
}

