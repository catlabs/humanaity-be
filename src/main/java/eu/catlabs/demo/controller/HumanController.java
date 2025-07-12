package eu.catlabs.demo.controller;

import eu.catlabs.demo.dto.HumanInput;
import eu.catlabs.demo.entity.Human;
import eu.catlabs.demo.repository.CityRepository;
import eu.catlabs.demo.repository.HumanRepository;
import eu.catlabs.demo.services.HumanService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/humans")
public class HumanController {

    private final HumanRepository humanRepository;
    private final HumanService humanService;
    private final CityRepository cityRepository;

    public HumanController(HumanService humanService, HumanRepository humanRepository, CityRepository cityRepository) {
        this.humanRepository = humanRepository;
        this.humanService = humanService;
        this.cityRepository = cityRepository;
    }

    @QueryMapping
    public List<Human> humans() {
        return humanService.getAllHumans();
    }

    @QueryMapping
    public Optional<Human> human(@Argument String id) {
        return humanService.getHumanById(Long.parseLong(id));
    }

    @QueryMapping
    public List<Human> humansByCity(@Argument String cityId) {
        return humanService.getHumansByCityId(cityId);
    }

    @QueryMapping
    public List<Human> humansByJob(@Argument String job) {
        return humanService.getHumansByJob(job);
    }

    @MutationMapping
    public Human createHuman(@Argument HumanInput input) {
        return humanService.createHuman(input);
    }

    @MutationMapping
    public Human updateHuman(@Argument String id, @Argument HumanInput input) {
        return humanService.updateHuman(id, input);
    }

    @MutationMapping
    public Boolean deleteHuman(@Argument String id) {
        return humanService.deleteHuman(Long.parseLong(id));
    }

}