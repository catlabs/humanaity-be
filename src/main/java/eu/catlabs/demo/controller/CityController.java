package eu.catlabs.demo.controller;

import eu.catlabs.demo.dto.CityInput;
import eu.catlabs.demo.entity.City;
import eu.catlabs.demo.services.CityService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cities")
public class CityController {
    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @QueryMapping
    public List<City> cities() {
        return cityService.getAllCities();
    }

    @QueryMapping
    public List<City> citiesByName(@Argument String name) {
        return cityService.getCitiesByName(name);
    }

    @QueryMapping
    public Optional<City> city(@Argument String id) {
        return cityService.getCityById(id);
    }

    @MutationMapping
    public City createCity(@Argument CityInput input) {
        return cityService.createCity(input);
    }

    @MutationMapping
    public City updateCity(@Argument String id, @Argument CityInput input) {
        return cityService.updateCity(id, input);
    }

    @MutationMapping
    public Boolean deleteCity(@Argument String id) {
        try {
            cityService.deleteCity(Long.parseLong(id));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
