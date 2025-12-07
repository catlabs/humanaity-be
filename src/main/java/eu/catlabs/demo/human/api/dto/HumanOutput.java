package eu.catlabs.demo.human.api.dto;

import eu.catlabs.demo.human.domain.Personality;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HumanOutput {
    private Long id;
    private boolean isBusy;
    private String name;

    private Double x;
    private Double y;

    private Double creativity;
    private Double intellect;
    private Double sociability;
    private Double practicality;
    private Personality personality;

    private Double scienceSkill;
    private Double cultureSkill;
    private Double socialSkill;

    private Double totalScienceContributed;
    private Double totalCultureContributed;
    private Double totalSocialContributed;
}
