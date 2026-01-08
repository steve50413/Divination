package com.divination.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "manju_poem")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManjuPoem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "tibetan_code", length = 50)
    private String tibetanCode;
    
    @Column(length = 100)
    private String direction;
    
    @Column(length = 100)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String summary;
    
    @Column(name = "family_life", columnDefinition = "TEXT")
    private String familyLife;
    
    @Column(columnDefinition = "TEXT")
    private String strategy;
    
    @Column(columnDefinition = "TEXT")
    private String fortune;
    
    @Column(columnDefinition = "TEXT")
    private String enemy;
    
    @Column(columnDefinition = "TEXT")
    private String travel;
    
    @Column(columnDefinition = "TEXT")
    private String illness;
    
    @Column(columnDefinition = "TEXT")
    private String practice;
    
    @Column(name = "lost_item", columnDefinition = "TEXT")
    private String lostItem;
    
    @Column(columnDefinition = "TEXT")
    private String visitor;
    
    @Column(name = "other_matters", columnDefinition = "TEXT")
    private String otherMatters;
    
    @Column(columnDefinition = "TEXT")
    private String advice;
}