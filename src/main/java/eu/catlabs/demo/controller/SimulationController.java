package eu.catlabs.demo.controller;

import eu.catlabs.demo.services.SimulationService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class SimulationController {

    private final SimulationService simulationService;

    public SimulationController(SimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @MutationMapping
    public String startSimulation(@Argument Long cityId) {
        return simulationService.startSimulation(cityId);
    }

    @MutationMapping
    public String stopSimulation(@Argument Long cityId) {
        return simulationService.stopSimulation(cityId);
    }

    @MutationMapping
    public boolean isSimulationRunning(@Argument Long cityId) {
        return simulationService.isRunning(cityId);
    }
}
