package eu.catlabs.demo.controller;

import eu.catlabs.demo.dto.CityInput;
import eu.catlabs.demo.dto.HumanInput;
import eu.catlabs.demo.entity.City;
import eu.catlabs.demo.entity.Human;
import eu.catlabs.demo.repository.HumanRepository;
import eu.catlabs.demo.repository.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@Controller
public class GraphQLController {
    private final HumanRepository humanRepository;
    private final CityRepository cityRepository;

    @Autowired
    public GraphQLController(HumanRepository humanRepository, CityRepository cityRepository) {
        this.humanRepository = humanRepository;
        this.cityRepository = cityRepository;
    }

    @QueryMapping
    public List<Human> humans() {
        return humanRepository.findAll();
    }

    @QueryMapping
    public Optional<Human> human(@Argument String id) {
        return humanRepository.findById(Long.parseLong(id));
    }

    @QueryMapping
    public List<Human> humansByCity(@Argument String cityId) {
        return humanRepository.findByCityId(Long.parseLong(cityId));
    }

    @QueryMapping
    public List<Human> humansByJob(@Argument String job) {
        return humanRepository.findByJobContainingIgnoreCase(job);
    }

    // City Queries
    @QueryMapping
    public List<City> cities() {
        return cityRepository.findAll();
    }

    @QueryMapping
    public Optional<City> city(@Argument String id) {
        return cityRepository.findById(Long.parseLong(id));
    }

    @QueryMapping
    public List<City> citiesByName(@Argument String name) {
        return cityRepository.findByNameContainingIgnoreCase(name);
    }

    // Human Mutations
    @MutationMapping
    public Human createHuman(@Argument HumanInput input) {
        Human human = new Human();
        human.setName(input.getName());
        human.setAge(input.getAge());
        human.setJob(input.getJob());
        human.setHappiness(input.getHappiness());

        if (input.getCityId() != null) {
            Optional<City> city = cityRepository.findById(Long.parseLong(input.getCityId()));
            city.ifPresent(human::setCity);
        }

        return humanRepository.save(human);
    }

    @MutationMapping
    public Human updateHuman(@Argument String id, @Argument HumanInput input) {
        Optional<Human> existingHuman = humanRepository.findById(Long.parseLong(id));

        if (existingHuman.isPresent()) {
            Human human = existingHuman.get();
            human.setName(input.getName());
            human.setAge(input.getAge());
            human.setJob(input.getJob());
            human.setHappiness(input.getHappiness());

            if (input.getCityId() != null) {
                Optional<City> city = cityRepository.findById(Long.parseLong(input.getCityId()));
                city.ifPresent(human::setCity);
            }

            return humanRepository.save(human);
        }

        throw new RuntimeException("Human not found with id: " + id);
    }

    @MutationMapping
    public Boolean deleteHuman(@Argument String id) {
        try {
            humanRepository.deleteById(Long.parseLong(id));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // City Mutations
    @MutationMapping
    public City createCity(@Argument CityInput input) {
        City city = new City();
        city.setName(input.getName());
        return cityRepository.save(city);
    }

    @MutationMapping
    public City updateCity(@Argument String id, @Argument CityInput input) {
        Optional<City> existingCity = cityRepository.findById(Long.parseLong(id));

        if (existingCity.isPresent()) {
            City city = existingCity.get();
            city.setName(input.getName());
            return cityRepository.save(city);
        }

        throw new RuntimeException("City not found with id: " + id);
    }

    @MutationMapping
    public Boolean deleteCity(@Argument String id) {
        try {
            cityRepository.deleteById(Long.parseLong(id));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
