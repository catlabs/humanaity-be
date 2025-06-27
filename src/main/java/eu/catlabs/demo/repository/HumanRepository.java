package eu.catlabs.demo.repository;

import eu.catlabs.demo.entity.Human;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HumanRepository extends JpaRepository<Human, Long> {
    List<Human> findByCityId(Long cityId);

    List<Human> findByJobContainingIgnoreCase(String job);

//     @Query("SELECT h FROM Human h WHERE h.happiness >= :minHappiness")
//    List<Human> findByHappinessGreaterThanEqual(@Param("minHappiness") double minHappiness);
//
//    @Query("SELECT h FROM Human h WHERE h.age BETWEEN :minAge AND :maxAge")
//    List<Human> findByAgeBetween(@Param("minAge") int minAge, @Param("maxAge") int maxAge);
}
