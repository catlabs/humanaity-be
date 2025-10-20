package eu.catlabs.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HumanOutput {
    boolean isBusy;
    private Long id;
    private String name;
    private String job;
    private double happiness;
    private double x;
    private double y;
}