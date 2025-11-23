package eu.catlabs.demo.repository;

import eu.catlabs.demo.entity.City;
import eu.catlabs.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
    List<City> findByNameContainingIgnoreCase(String name);
    List<City> findByOwner(User owner);
}