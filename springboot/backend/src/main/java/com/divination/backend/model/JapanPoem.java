package com.divination.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "japan_poem")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JapanPoem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(length = 20, nullable = false)
    private String number;
    
    @Column(name = "fortune_level", length = 20)
    private String fortuneLevel;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    @Column(length = 200)
    private String wish;
    
    @Column(length = 200)
    private String illness;
    
    @Column(name = "waiting_person", length = 200)
    private String waitingPerson;
    
    @Column(name = "lost_item", length = 200)
    private String lostItem;
    
    @Column(name = "life_events", length = 300)
    private String lifeEvents;
}