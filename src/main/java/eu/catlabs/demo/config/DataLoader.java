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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Random;

@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

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
    public void run(String... args) {
        try {
            for (int i = 0; i < 1; i++) {
                City city = new City();
                city.setName(faker.address().city());
                cityRepository.save(city);
                
                JsonNode humans;
                try {
                    // Try to use OpenAI to generate humans
                    humans = this.aiService.createHumans();
                } catch (Exception e) {
                    // If OpenAI is not available, create humans manually
                    logger.warn("OpenAI service unavailable, creating test data manually: {}", e.getMessage());
                    humans = createHumansManually();
                }
                
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
        } catch (Exception e) {
            logger.error("Error loading initial data", e);
            // Don't fail application startup if data loading fails
        }
    }

    private JsonNode createHumansManually() throws JsonProcessingException {
        // Create 5 test humans with random data
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < 5; i++) {
            if (i > 0) json.append(",");
            double creativity = random.nextDouble();
            double intellect = random.nextDouble();
            double sociability = random.nextDouble();
            double practicality = 2.0 - creativity - intellect - sociability; // Ensure sum is ~2
            
            // Normalize to ensure sum is exactly 2
            double sum = creativity + intellect + sociability + practicality;
            creativity = (creativity / sum) * 2.0;
            intellect = (intellect / sum) * 2.0;
            sociability = (sociability / sum) * 2.0;
            practicality = (practicality / sum) * 2.0;
            
            json.append(String.format(
                "{\"name\":\"%s\",\"creativity\":%.2f,\"intellect\":%.2f,\"sociability\":%.2f,\"practicality\":%.2f,\"personality\":\"BALANCED\"}",
                faker.name().fullName(),
                creativity,
                intellect,
                sociability,
                practicality
            ));
        }
        json.append("]");
        return objectMapper.readTree(json.toString());
    }
}