package eu.catlabs.demo.config;

import eu.catlabs.demo.entity.Product;
import eu.catlabs.demo.entity.User;
import eu.catlabs.demo.repository.ProductRepository;
import eu.catlabs.demo.repository.UserRepository;
import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Random;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final Faker faker = new Faker(new Locale("en"));
    private final Random random = new Random();

    public DataLoader(UserRepository userRepository, ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        // Generate 100 users
        for (int i = 0; i < 10; i++) {
            User user = new User();
            user.setFirstName(faker.name().firstName());
            user.setLastName(faker.name().lastName());
            user.setEmail(faker.internet().emailAddress());
            userRepository.save(user);
        }

        // Generate 50 products
        for (int i = 0; i < 50; i++) {
            Product product = new Product();
            product.setName(faker.commerce().productName());
            product.setDescription(faker.lorem().sentence());
            product.setPrice(new BigDecimal(faker.commerce().price().replace(",", ".")));
            productRepository.save(product);
        }

        System.out.println("Fake users and products inserted! ✅");
    }
}