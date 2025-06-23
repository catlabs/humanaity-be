package eu.catlabs.demo.repository;

import eu.catlabs.demo.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByPriceLessThan(BigDecimal price);
    List<Product> findByNameContainingIgnoreCase(String name);
}