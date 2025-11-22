package eu.catlabs.demo.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import eu.catlabs.demo.dto.HumanInput;
import eu.catlabs.demo.entity.City;
import eu.catlabs.demo.repository.CityRepository;
import eu.catlabs.demo.services.AiService;
import eu.catlabs.demo.services.HumanService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Random;

@Component
public class DataLoader implements CommandLineRunner {

    private final AiService aiService;
    private final CityRepository cityRepository;
    private final HumanService humanService;
    private final Faker faker = new Faker(new Locale("en"));
    private final ObjectMapper objectMapper;
    private final Random random = new Random();

    public DataLoader(AiService aiService, CityRepository cityRepository, HumanService humanService) {
        this.aiService = aiService;
        this.cityRepository = cityRepository;
        this.humanService = humanService;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    @Transactional
    public void run(String... args) throws JsonProcessingException {
        for (int i = 0; i < 1; i++) {
            City city = new City();
            city.setName(faker.address().city());
            cityRepository.save(city);
            JsonNode humans = this.aiService.createHumans();
            humans.forEach(node -> {
                HumanInput humanInput = new HumanInput();
                humanInput.setName(node.get("name").asText());
                humanInput.setCityId(city.getId());
                humanInput.setBusy(false);
                humanInput.setCreativity(node.get("creativity").asDouble());
                humanInput.setIntellect(node.get("intellect").asDouble());
                humanInput.setSociability(node.get("sociability").asDouble());
                humanInput.setPracticality(node.get("practicality").asDouble());
                humanInput.setPersonality(this.humanService.derivePersonality(humanInput.getCreativity(), humanInput.getIntellect(), humanInput.getSociability(), humanInput.getPracticality()));
                humanInput.setX(faker.number().randomDouble(3, 0, 1));
                humanInput.setY(faker.number().randomDouble(3, 0, 1));
                this.humanService.createHuman(humanInput);
            });
        }
    }
}