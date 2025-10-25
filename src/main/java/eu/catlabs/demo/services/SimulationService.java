package eu.catlabs.demo.services;

import eu.catlabs.demo.entity.Human;
import eu.catlabs.demo.repository.HumanRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;

@Service
public class SimulationService {

    private static final double COLLISION_DISTANCE = 0.02;
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(4);
    private final Map<Long, ScheduledFuture<?>> runningTasks = new ConcurrentHashMap<>();
    private final HumanRepository humanRepository;
    private final HumanService humanService;
    private final Random random = new Random();

    public SimulationService(HumanRepository humanRepository, HumanService humanService) {
        this.humanRepository = humanRepository;
        this.humanService = humanService;
    }

    public synchronized String startSimulation(Long cityId) {
        if (runningTasks.containsKey(cityId)) {
            return "Simulation already running for city " + cityId;
        }

        ScheduledFuture<?> task = executor.scheduleAtFixedRate(
                () -> simulateCity(cityId),
                0, 100, TimeUnit.MILLISECONDS
        );

        runningTasks.put(cityId, task);
        return "Simulation started for city " + cityId;
    }

    public synchronized String stopSimulation(Long cityId) {
        ScheduledFuture<?> task = runningTasks.get(cityId);
        if (task == null) {
            return "No simulation running for city " + cityId;
        }

        task.cancel(true);
        runningTasks.remove(cityId);
        return "Simulation stopped for city " + cityId;
    }

    public boolean isRunning(Long cityId) {
        return runningTasks.containsKey(cityId);
    }

    private void simulateCity(Long cityId) {
        try {
            List<Human> allHumans = humanRepository.findByCityId(cityId);
            Collections.shuffle(allHumans);
            List<Human> randomHumans = allHumans.stream().limit(10).toList();

            if (randomHumans.isEmpty()) {
                System.out.println("No humans found in city " + cityId);
                return;
            }

            Set<Human> changedHumans = new HashSet<>();

            for (Human human : randomHumans) {
                if (!human.isBusy()) {
                    boolean hadCollision = checkCollisions(human, allHumans, changedHumans);
                    if (!hadCollision) {
                        updateHumanPosition(human);
                        changedHumans.add(human);
                    }
                }

            }
            if (!changedHumans.isEmpty()) {
                humanRepository.saveAll(changedHumans);
            }
            this.humanService.publishHumanUpdates(randomHumans);

        } catch (Exception e) {
            System.err.println("Error simulating city " + cityId + ": " + e.getMessage());
        }
    }

    private boolean checkCollisions(Human currentHuman, List<Human> allHumans, Set<Human> changedHumans) {
        for (Human otherHuman : allHumans) {
            if (currentHuman.getId().equals(otherHuman.getId())) {
                continue;
            }

            if (areHumansColliding(currentHuman, otherHuman)) {
                currentHuman.setBusy(true);
                otherHuman.setBusy(true);

                changedHumans.add(currentHuman);
                changedHumans.add(otherHuman);

                // handleCollision(currentHuman, otherHuman);
                return true; // Had collision
            }
        }
        return false; // No collision
    }

    private boolean areHumansColliding(Human human1, Human human2) {
        double distance = Math.sqrt(
                Math.pow(human1.getX() - human2.getX(), 2) +
                        Math.pow(human1.getY() - human2.getY(), 2)
        );
        return distance < COLLISION_DISTANCE;
    }

    private void updateHumanPosition(Human human) {
        double deltaX = (random.nextDouble() - 0.5) * 0.1;
        double deltaY = (random.nextDouble() - 0.5) * 0.1;

        double newX = Math.max(0, Math.min(1, human.getX() + deltaX));
        double newY = Math.max(0, Math.min(1, human.getY() + deltaY));

        human.setX(newX);
        human.setY(newY);
    }

    public void resetBusyStatus(Long cityId) {
        List<Human> busyHumans = humanRepository.findByCityIdAndBusyTrue(cityId);
        busyHumans.forEach(human -> human.setBusy(false));
        humanRepository.saveAll(busyHumans);
    }

    private void updateHumanPositionCircular(Human human) {
        double angle = random.nextDouble() * 2 * Math.PI;
        double radius = 0.02;
        double newX = Math.max(0, Math.min(1, human.getX() + Math.cos(angle) * radius));
        double newY = Math.max(0, Math.min(1, human.getY() + Math.sin(angle) * radius));
        human.setX(newX);
        human.setY(newY);
    }

    // Directed movement (toward center or specific point)
    private void updateHumanPositionDirected(Human human) {
        double targetX = 0.5; // center
        double targetY = 0.5; // center
        double speed = 0.01;

        double dx = targetX - human.getX();
        double dy = targetY - human.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance > 0) {
            double newX = human.getX() + (dx / distance) * speed;
            double newY = human.getY() + (dy / distance) * speed;
            human.setX(Math.max(0, Math.min(1, newX)));
            human.setY(Math.max(0, Math.min(1, newY)));
        }
    }
}