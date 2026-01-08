package com.divination.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "avalo_poem")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvaloPoem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "tibetan_code", length = 50)
    private String tibetanCode;
    
    @Column(length = 100)
    private String Title;
    
    @Column(columnDefinition = "TEXT")
    private String summary;
}