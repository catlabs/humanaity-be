package eu.catlabs.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HumanOutput {
    private Long id;
    private String name;
    private String job;
    private Double happiness;
    private Double x;
    private Double y;
}