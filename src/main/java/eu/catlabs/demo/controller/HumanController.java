package eu.catlabs.demo.controller;

import eu.catlabs.demo.dto.HumanInput;
import eu.catlabs.demo.dto.HumanOutput;
import eu.catlabs.demo.services.HumanService;
import org.reactivestreams.Publisher;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/humans")
public class HumanController {

    private final HumanService humanService;

    public HumanController(HumanService humanService) {
        this.humanService = humanService;
    }

    @QueryMapping
    public Optional<HumanOutput> human(@Argument String id) {
        return humanService.getHumanById(Long.parseLong(id));
    }

    @QueryMapping
    public List<HumanOutput> humansByCity(@Argument String cityId) {
        return humanService.getHumansByCityId(cityId);
    }

    @MutationMapping
    public HumanOutput createHuman(@Argument HumanInput input) {
        return humanService.createHuman(input);
    }

    @MutationMapping
    public HumanOutput updateHuman(@Argument String id, @Argument HumanInput input) {
        return humanService.updateHuman(id, input);
    }

    @MutationMapping
    public Boolean deleteHuman(@Argument String id) {
        return humanService.deleteHuman(Long.parseLong(id));
    }

    @SubscriptionMapping
    public Publisher<List<HumanOutput>> humansByCityPositions(@Argument String cityId) {
        return humanService.getCityPositionsStream(Long.parseLong(cityId));
    }

}