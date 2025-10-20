package eu.catlabs.demo.repository;

import eu.catlabs.demo.entity.Human;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HumanRepository extends JpaRepository<Human, Long> {
    List<Human> findByCityId(Long cityId);

    List<Human> findByCityIdAndBusyTrue(Long cityId); // Add this method
}
