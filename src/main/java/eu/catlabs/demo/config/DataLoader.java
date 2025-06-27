package eu.catlabs.demo.config;

import eu.catlabs.demo.entity.City;
import eu.catlabs.demo.entity.Human;
import eu.catlabs.demo.repository.HumanRepository;
import eu.catlabs.demo.repository.CityRepository;
import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Random;

@Component
public class DataLoader implements CommandLineRunner {

    private final CityRepository cityRepository;
    private final HumanRepository humanRepository;
    private final Faker faker = new Faker(new Locale("en"));
    private final Random random = new Random();

    public DataLoader(CityRepository cityRepository, HumanRepository humanRepository) {
        this.cityRepository = cityRepository;
        this.humanRepository = humanRepository;
    }

    @Override
    public void run(String... args) {
        for (int i = 0; i < 5; i++) {
            City city = new City();
            city.setName(faker.address().city());
            cityRepository.save(city);
            for (int j = 0; j < 20; j++) {
                String name = faker.name().fullName();
                int age = random.nextInt(60);
                Human human = new Human();
                human.setName(name);
                human.setAge(age);
                human.setCity(city);
                human.setHappiness(random.nextDouble());
                humanRepository.save(human);
            }
        }
    }
}