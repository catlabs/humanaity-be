package eu.catlabs.demo.services;

import eu.catlabs.demo.dto.HumanInput;
import eu.catlabs.demo.dto.HumanOutput;
import eu.catlabs.demo.entity.City;
import eu.catlabs.demo.entity.Human;
import eu.catlabs.demo.enums.Personality;
import eu.catlabs.demo.repository.CityRepository;
import eu.catlabs.demo.repository.HumanRepository;
import jakarta.persistence.EntityNotFoundException;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class HumanService {
    private final HumanRepository humanRepository;
    private final CityRepository cityRepository;

    private final ConcurrentHashMap<Long, Human> lastPositions = new ConcurrentHashMap<>();

    public HumanService(HumanRepository humanRepository, CityRepository cityRepository) {
        this.humanRepository = humanRepository;
        this.cityRepository = cityRepository;
    }

    public Optional<HumanOutput> getHumanById(Long id) {
        return humanRepository.findById(id)
                .map(this::toHumanOutput);
    }

    public List<HumanOutput> getHumansByCityId(String cityId) {
        return humanRepository.findByCityId(Long.parseLong(cityId)).stream()
                .map(this::toHumanOutput)
                .toList();
    }

    public HumanOutput createHuman(HumanInput input) {
        Human human = new Human();
        updateHumanFields(human, input);
        setHumanCity(human, input.getCityId());
        Human savedHuman = humanRepository.save(human);
        
        // Log only the ID to avoid lazy loading issues
        // System.out.println("Created human with ID: " + savedHuman.getId());

        // Publish the new human to subscribers
        // publishHumanUpdate(savedHuman);

        return toHumanOutput(savedHuman);
    }

    private HumanOutput toHumanOutput(Human human) {
        HumanOutput output = new HumanOutput();
        output.setId(human.getId());
        output.setBusy(human.isBusy());
        output.setName(human.getName());
        output.setCreativity(human.getCreativity());
        output.setIntellect(human.getIntellect());
        output.setSociability(human.getSociability());
        output.setPracticality(human.getPracticality());
        output.setPersonality(human.getPersonality());
        output.setY(human.getY());
        output.setX(human.getX());
        return output;
    }

    public HumanOutput updateHuman(String id, HumanInput input) {
        Human human = humanRepository.findById(Long.parseLong(id))
                .orElseThrow(() -> new EntityNotFoundException("Human not found"));

        updateHumanFields(human, input);
        setHumanCity(human, input.getCityId());
        Human updatedHuman = humanRepository.save(human);

        // Publish the update to subscribers
        // publishHumanUpdate(updatedHuman);
        return toHumanOutput(updatedHuman);
    }

    public boolean deleteHuman(Long id) {
        try {
            humanRepository.deleteById(id);
            // Remove from subscription tracking
            removeHumanFromSubscriptions(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void updateHumanFields(Human human, HumanInput input) {
        human.setName(input.getName());
        human.setBusy(input.isBusy());
        human.setCreativity(input.getCreativity());
        human.setIntellect(input.getIntellect());
        human.setSociability(input.getSociability());
        human.setPracticality(input.getPracticality());
        human.setPersonality(derivePersonality(input.getCreativity(), input.getIntellect(), input.getSociability(), input.getPracticality()));
        human.setX(input.getX());
        human.setY(input.getY());
    }

    private void setHumanCity(Human human, Long cityId) {
        if (cityId != null) {
            Optional<City> city = cityRepository.findById(cityId);
            if (city.isPresent()) {
                human.setCity(city.get());
            } else {
                throw new IllegalArgumentException("City not found with id: " + cityId);
            }
        }
    }

    public void publishHumanUpdates(List<Human> humans) {
        humans.forEach(human -> lastPositions.put(human.getId(), human));
    }

    public Publisher<List<HumanOutput>> getCityPositionsStream(Long cityId) {
        return Flux.interval(Duration.ofMillis(100))
                .map(tick -> {
                    List<HumanOutput> changes = lastPositions.values().stream()
                            .filter(human -> human.getCity() != null && human.getCity().getId().equals(cityId))
                            .map(this::toHumanOutput)
                            .toList();
                    lastPositions.clear();
                    return changes;
                });
    }

    public void removeHumanFromSubscriptions(Long humanId) {
        lastPositions.remove(humanId);
    }

    public Personality derivePersonality(double creativity, double intellect, double sociability, double practicality) {
        if (creativity > 0.5 && intellect > 0.5) return Personality.VISIONARY;
        if (practicality > 0.5 && intellect > 0.5) return Personality.ENGINEER;
        if (creativity > 0.5 && sociability > 0.5) return Personality.STORYTELLER;
        if (sociability > 0.5 && practicality > 0.5) return Personality.LEADER;
        if (intellect > 0.5 && sociability < 0.5) return Personality.THINKER;
        if (creativity > 0.5 && practicality < 0.5) return Personality.DREAMER;

        return Personality.BALANCED;
    }


}