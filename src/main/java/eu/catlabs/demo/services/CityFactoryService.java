package eu.catlabs.demo.services;

import eu.catlabs.demo.dto.CityInput;
import eu.catlabs.demo.entity.City;
import eu.catlabs.demo.entity.User;
import eu.catlabs.demo.repository.CityRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CityFactoryService {

    private final CityRepository cityRepository;
    private final HumanGenerationService humanGenerationService;
    private final EntityManager entityManager;

    public CityFactoryService(CityRepository cityRepository, HumanGenerationService humanGenerationService, EntityManager entityManager) {
        this.cityRepository = cityRepository;
        this.humanGenerationService = humanGenerationService;
        this.entityManager = entityManager;
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
        
        // Flush to ensure the city is persisted before generating humans
        entityManager.flush();
        
        // Generate humans for the city
        humanGenerationService.generateHumansForCity(savedCity);
        
        return savedCity;
    }
}

