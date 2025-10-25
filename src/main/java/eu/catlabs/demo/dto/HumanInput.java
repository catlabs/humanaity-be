package eu.catlabs.demo.dto;

import eu.catlabs.demo.enums.Personality;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class HumanInput {
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

    private Long cityId;

}