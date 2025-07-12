package eu.catlabs.demo.services;

import eu.catlabs.demo.dto.CityInput;
import eu.catlabs.demo.entity.City;
import eu.catlabs.demo.repository.CityRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CityService {
    private final CityRepository cityRepository;

    public CityService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    public List<City> getAllCities() {
        return cityRepository.findAll();
    }

    public Optional<City> getCityById(String id) {
        return cityRepository.findById(Long.parseLong(id));
    }

    public List<City> getCitiesByName(@Argument String name) {
        return cityRepository.findByNameContainingIgnoreCase(name);
    }

    public City createCity(CityInput input) {
        City city = new City();
        city.setName(input.getName());
        return cityRepository.save(city);
    }

    public City updateCity(String id, CityInput input) {
        Optional<City> existingCity = cityRepository.findById(Long.parseLong(id));

        if (existingCity.isPresent()) {
            City city = existingCity.get();
            city.setName(input.getName());
            return cityRepository.save(city);
        }

        throw new RuntimeException("City not found with id: " + id);
    }

    public void deleteCity(Long id) {
        cityRepository.deleteById(id);
    }
}
