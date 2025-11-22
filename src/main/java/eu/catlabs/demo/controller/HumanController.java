package eu.catlabs.demo.controller;

import eu.catlabs.demo.dto.HumanInput;
import eu.catlabs.demo.dto.HumanOutput;
import eu.catlabs.demo.services.HumanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/humans")
@Tag(name = "Humans", description = "Human management API")
public class HumanController {

    private final HumanService humanService;

    public HumanController(HumanService humanService) {
        this.humanService = humanService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get human by ID")
    public ResponseEntity<HumanOutput> getHumanById(@PathVariable Long id) {
        Optional<HumanOutput> human = humanService.getHumanById(id);
        return human.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/city/{cityId}")
    @Operation(summary = "Get all humans in a city")
    public ResponseEntity<List<HumanOutput>> getHumansByCity(@PathVariable String cityId) {
        List<HumanOutput> humans = humanService.getHumansByCityId(cityId);
        return ResponseEntity.ok(humans);
    }

    @PostMapping
    @Operation(summary = "Create a new human")
    public ResponseEntity<HumanOutput> createHuman(@Valid @RequestBody HumanInput input) {
        HumanOutput human = humanService.createHuman(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(human);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a human")
    public ResponseEntity<HumanOutput> updateHuman(@PathVariable String id, @Valid @RequestBody HumanInput input) {
        try {
            HumanOutput human = humanService.updateHuman(id, input);
            return ResponseEntity.ok(human);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a human")
    public ResponseEntity<Void> deleteHuman(@PathVariable Long id) {
        boolean deleted = humanService.deleteHuman(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
