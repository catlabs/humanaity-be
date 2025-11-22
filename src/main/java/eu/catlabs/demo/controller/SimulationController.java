package eu.catlabs.demo.controller;

import eu.catlabs.demo.services.SimulationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/simulations")
@Tag(name = "Simulations", description = "Simulation management API")
public class SimulationController {

    private final SimulationService simulationService;

    public SimulationController(SimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @PostMapping("/{cityId}/start")
    @Operation(summary = "Start simulation for a city")
    public ResponseEntity<Map<String, String>> startSimulation(@PathVariable Long cityId) {
        String message = simulationService.startSimulation(cityId);
        return ResponseEntity.ok(Map.of("message", message));
    }

    @PostMapping("/{cityId}/stop")
    @Operation(summary = "Stop simulation for a city")
    public ResponseEntity<Map<String, String>> stopSimulation(@PathVariable Long cityId) {
        String message = simulationService.stopSimulation(cityId);
        return ResponseEntity.ok(Map.of("message", message));
    }

    @GetMapping("/{cityId}/status")
    @Operation(summary = "Check if simulation is running for a city")
    public ResponseEntity<Map<String, Boolean>> isSimulationRunning(@PathVariable Long cityId) {
        boolean isRunning = simulationService.isRunning(cityId);
        return ResponseEntity.ok(Map.of("running", isRunning));
    }
}
