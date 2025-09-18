package eu.catlabs.demo.services;

import eu.catlabs.demo.dto.HumanInput;
import eu.catlabs.demo.entity.City;
import eu.catlabs.demo.entity.Human;
import eu.catlabs.demo.repository.CityRepository;
import eu.catlabs.demo.repository.HumanRepository;
import jakarta.persistence.EntityNotFoundException;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class HumanService {
    private final HumanRepository humanRepository;
    private final CityRepository cityRepository;

    // Subscription-related fields
    private final Sinks.Many<Human> humanUpdateSink = Sinks.many().multicast().onBackpressureBuffer();
    private final ConcurrentHashMap<Long, Human> lastPositions = new ConcurrentHashMap<>();

    public HumanService(HumanRepository humanRepository, CityRepository cityRepository) {
        this.humanRepository = humanRepository;
        this.cityRepository = cityRepository;
    }

    // Existing CRUD methods
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
        Human savedHuman = humanRepository.save(human);

        // Publish the new human to subscribers
        publishHumanUpdate(savedHuman);
        return savedHuman;
    }

    public Human updateHuman(String id, HumanInput input) {
        Human human = humanRepository.findById(Long.parseLong(id))
                .orElseThrow(() -> new EntityNotFoundException("Human not found"));

        updateHumanFields(human, input);
        setHumanCity(human, input.getCityId());
        Human updatedHuman = humanRepository.save(human);

        // Publish the update to subscribers
        publishHumanUpdate(updatedHuman);
        return updatedHuman;
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

    // Subscription methods
    public void publishHumanUpdate(Human human) {
        lastPositions.put(human.getId(), human);
        humanUpdateSink.tryEmitNext(human);
    }

    public Publisher<List<Human>> getCityPositionsStream(Long cityId) {
        return Flux.interval(Duration.ofMillis(1000))
                .map(tick -> lastPositions.values().stream()
                        .filter(human -> human.getCity() != null && human.getCity().getId().equals(cityId))
                        .toList());
    }

    public void removeHumanFromSubscriptions(Long humanId) {
        lastPositions.remove(humanId);
    }

    // Method for simulation service to update positions
    public void updateHumanPosition(Human human) {
        // Update the database
        humanRepository.save(human);
        // Publish to subscribers
        publishHumanUpdate(human);
    }
}