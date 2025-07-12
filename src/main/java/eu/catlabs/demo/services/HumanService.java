package eu.catlabs.demo.services;

import eu.catlabs.demo.dto.HumanInput;
import eu.catlabs.demo.entity.City;
import eu.catlabs.demo.entity.Human;
import eu.catlabs.demo.repository.CityRepository;
import eu.catlabs.demo.repository.HumanRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HumanService {
    private final HumanRepository humanRepository;
    private final CityRepository cityRepository;

    public HumanService(HumanRepository humanRepository, CityRepository cityRepository) {
        this.humanRepository = humanRepository;
        this.cityRepository = cityRepository;
    }

    public List<Human> getAllHumans() {
        return humanRepository.findAll();
    }

    public Optional<Human> getHumanById(Long id) {
        return humanRepository.findById(id);
    }

    public List<Human> getHumansByCityId(String cityId) {
        return humanRepository.findByCityId(Long.parseLong(cityId));
    }

    public List<Human> getHumansByJob(String job) {
        return humanRepository.findByJobContainingIgnoreCase(job);
    }

    public Human createHuman(HumanInput input) {
        Human human = new Human();
        updateHumanFields(human, input);
        setHumanCity(human, input.getCityId());
        return humanRepository.save(human);
    }

    public Human updateHuman(String id, HumanInput input) {
        Human human = humanRepository.findById(Long.parseLong(id))
                .orElseThrow(() -> new EntityNotFoundException("Human not found"));

        updateHumanFields(human, input);
        setHumanCity(human, input.getCityId());
        return humanRepository.save(human);
    }

    private void updateHumanFields(Human human, HumanInput input) {
        human.setName(input.getName());
        human.setAge(input.getAge());
        human.setJob(input.getJob());
        human.setHappiness(input.getHappiness());
    }

    private void setHumanCity(Human human, String cityId) {
        if (cityId != null) {
            Optional<City> city = cityRepository.findById(Long.parseLong(cityId));
            city.ifPresent(human::setCity);
        }
    }

    public boolean deleteHuman(Long id) {
        try {
            humanRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}