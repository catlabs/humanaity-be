package eu.catlabs.demo.entity;

import eu.catlabs.demo.enums.Personality;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Human {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean busy;
    private String name;
    private Double x;
    private Double y;

    // Personality traits (0.0 to 1.0)
    private Double creativity = 0.5;      // Affects art/invention
    private Double intellect = 0.5;       // Affects science/invention
    private Double sociability = 0.5;     // Affects dialog/cooperation
    private Double practicality = 0.5;
    private Personality personality;

    // Skill levels (can grow over time)
    private Double scienceSkill = 0.0;
    private Double cultureSkill = 0.0;
    private Double socialSkill = 0.0;

    // Lifetime contributions
    private Double totalScienceContributed = 0.0;
    private Double totalCultureContributed = 0.0;
    private Double totalSocialContributed = 0.0;

    @ManyToOne
    private City city;
}