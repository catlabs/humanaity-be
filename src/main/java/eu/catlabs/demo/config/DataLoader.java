package eu.catlabs.demo.config;

import com.github.javafaker.Faker;
import eu.catlabs.demo.entity.City;
import eu.catlabs.demo.entity.Human;
import eu.catlabs.demo.repository.CityRepository;
import eu.catlabs.demo.repository.HumanRepository;
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
            for (int j = 0; j < 200; j++) {
                String name = faker.name().fullName();
                int age = random.nextInt(60);
                Human human = new Human();
                human.setName(name);
                human.setAge(age);
                human.setCity(city);
                human.setHappiness(random.nextDouble());
                human.setX(faker.number().randomDouble(3, 0, 1));
                human.setY(faker.number().randomDouble(3, 0, 1));
                humanRepository.save(human);
            }
        }
    }
}