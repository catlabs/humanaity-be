package eu.catlabs.demo.services;

import eu.catlabs.demo.entity.Human;
import eu.catlabs.demo.repository.HumanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HumanService {
    @Autowired
    private HumanRepository humanRepository;

    public List<Human> getAllProducts() {
        return humanRepository.findAll();
    }

    public Human getProductById(Long id) {
        return humanRepository.findById(id).orElse(null);
    }

    public Human createProduct(Human human) {
        return humanRepository.save(human);
    }

    public Human updateProduct(Long id, Human human) {
        human.setId(id);
        return humanRepository.save(human);
    }

    public void deleteProduct(Long id) {
        humanRepository.deleteById(id);
    }
}