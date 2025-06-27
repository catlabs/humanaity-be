package eu.catlabs.demo.controller;

import eu.catlabs.demo.entity.Human;
import eu.catlabs.demo.services.HumanService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/humans")
public class HumanController {

    private final HumanService humanService;

    public HumanController(HumanService humanService) {
        this.humanService = humanService;
    }

    // GET all products
    @GetMapping
    public List<Human> getAllHumans() {
        return humanService.getAllHumans();
    }

    // GET one product by ID
    @GetMapping("/{id}")
    public Human getProductById(@PathVariable Long id) {
        return humanService.getHumanById(id);
    }

    // POST: Create new product
    @PostMapping
    public Human createProduct(@RequestBody Human human) {
        return humanService.createHuman(human);
    }

    // PUT: Update product
    @PutMapping("/{id}")
    public Human updateProduct(@PathVariable Long id, @RequestBody Human human) {
        return humanService.updateHuman(id, human);
    }

    // DELETE: Delete product
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        humanService.deleteHuman(id);
    }
}