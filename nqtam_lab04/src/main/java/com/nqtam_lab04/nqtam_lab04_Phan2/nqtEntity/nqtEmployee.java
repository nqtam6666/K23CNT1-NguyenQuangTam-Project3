package com.nqtam_lab04.nqtam_lab04_Phan2.nqtEntity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Getter
@Setter
public class nqtEmployee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    
    String fullName;
    
    String gender;
    
    Integer age;
    
    Double salary;
}

