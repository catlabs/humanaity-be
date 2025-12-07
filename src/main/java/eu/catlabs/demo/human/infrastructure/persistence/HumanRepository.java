package eu.catlabs.demo.human.infrastructure.persistence;

import eu.catlabs.demo.human.domain.Human;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HumanRepository extends JpaRepository<Human, Long> {
    List<Human> findByCityId(Long cityId);
    List<Human> findByCityIdAndBusyTrue(Long cityId);
}
