package eu.catlabs.demo.entity;

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

    private boolean isBusy;
    private double happiness;
    private String job;
    private int age;
    private String name;
    private double x;
    private double y;

    @ManyToOne
    private City city;
}