package eu.catlabs.demo.services;

import eu.catlabs.demo.dto.HumanInput;
import eu.catlabs.demo.dto.HumanOutput;
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
    private final Sinks.Many<List<Human>> humansUpdateSink = Sinks.many().multicast().onBackpressureBuffer();
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

        // Publish the new human to subscribers
        // publishHumanUpdate(savedHuman);

        return toHumanOutput(savedHuman);
    }

    private HumanOutput toHumanOutput(Human human) {
        HumanOutput output = new HumanOutput();
        output.setId(human.getId());
        output.setName(human.getName());
        output.setJob(human.getJob());
        output.setHappiness(human.getHappiness());
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

    public void publishHumanUpdates(List<Human> humans) {
        humans.forEach(human -> lastPositions.put(human.getId(), human));
        humansUpdateSink.tryEmitNext(humans);
    }

    public Publisher<List<HumanOutput>> getCityPositionsStream(Long cityId) {
        return Flux.interval(Duration.ofMillis(100))
                .map(tick -> lastPositions.values().stream()
                        .filter(human -> human.getCity() != null && human.getCity().getId().equals(cityId))
                        .map(this::toHumanOutput)
                        .toList());
    }

    public void removeHumanFromSubscriptions(Long humanId) {
        lastPositions.remove(humanId);
    }


}