package eu.catlabs.demo.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import eu.catlabs.demo.dto.HumanInput;
import eu.catlabs.demo.entity.City;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Random;

@Service
public class HumanGenerationService {

    private static final Logger logger = LoggerFactory.getLogger(HumanGenerationService.class);

    private final AiService aiService;
    private final HumanService humanService;
    private final Faker faker = new Faker(new Locale("en"));
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Random random = new Random();

    public HumanGenerationService(AiService aiService, HumanService humanService) {
        this.aiService = aiService;
        this.humanService = humanService;
    }

    @Transactional
    public void generateHumansForCity(City city) {
        try {
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
                humanInput.setPersonality(this.humanService.derivePersonality(
                        humanInput.getCreativity(),
                        humanInput.getIntellect(),
                        humanInput.getSociability(),
                        humanInput.getPracticality()
                ));
                humanInput.setX(faker.number().randomDouble(3, 0, 1));
                humanInput.setY(faker.number().randomDouble(3, 0, 1));
                this.humanService.createHuman(humanInput);
            });
        } catch (Exception e) {
            logger.error("Error generating humans for city {}", city.getId(), e);
            throw new RuntimeException("Failed to generate humans for city", e);
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

