package eu.catlabs.demo.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "humans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Human {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int age;
    private String job;
    private double happiness;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;
}