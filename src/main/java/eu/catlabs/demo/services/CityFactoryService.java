package eu.catlabs.demo.services;

import eu.catlabs.demo.dto.CityInput;
import eu.catlabs.demo.entity.City;
import eu.catlabs.demo.entity.User;
import eu.catlabs.demo.repository.CityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CityFactoryService {

    private final CityRepository cityRepository;
    private final HumanGenerationService humanGenerationService;

    public CityFactoryService(CityRepository cityRepository, HumanGenerationService humanGenerationService) {
        this.cityRepository = cityRepository;
        this.humanGenerationService = humanGenerationService;
    }

    @Transactional
    public City createCityForUser(CityInput input, User owner) {
        if (owner == null) {
            throw new IllegalArgumentException("Owner cannot be null");
        }
        
        City city = new City();
        city.setName(input.getName());
        city.setOwner(owner);
        
        City savedCity = cityRepository.save(city);
        
        // Generate humans for the city
        humanGenerationService.generateHumansForCity(savedCity);
        
        return savedCity;
    }
}

