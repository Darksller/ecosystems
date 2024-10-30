
import models.Animal;
import models.Being;
import models.Conditions;
import models.Plant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ecosystem {
    private final String name;
    private final Conditions conditions;
    private final List<Being> beings;
    private final Map<String, PopulationHistory> populationHistory;

    public Ecosystem(String name, Conditions conditions) {
        this.name = name;
        this.conditions = conditions;
        this.populationHistory = new HashMap<>();
        this.beings = new ArrayList<>();
    }

    public void updateConditions(double temperature, double humidity, double availableWater) {
        this.conditions.setTemperature(temperature);
        this.conditions.setHumidity(humidity);
        this.conditions.setAvailableWater(availableWater);
        System.out.println("Environmental conditions updated.");
    }

    public Conditions getConditions() {
        return conditions;
    }

    public String getName() {
        return name;
    }

    public void addSpecies(Being being) {
        beings.add(being);
    }

    public List<Being> getBeings() {
        return beings;
    }

    public void simulateInteractions() {
        System.out.println("\n=== Simulating ecosystem: " + name + " ===");
        printEnvironmentalConditions();

        Map<String, Integer> initialPopulations = new HashMap<>();
        beings.forEach(being -> initialPopulations.put(being.getName(), being.getPopulation()));

        calculateTotalWaterConsumption();
        updateBasedOnEnvironment();
        simulateFeeding();
        analyzeStability(initialPopulations);
        updatePopulationHistory();
        printEcosystemState();
    }

    public void calculateTotalWaterConsumption() {
        double totalWaterConsumption = 0.0;

        for (Being being : beings) {
            double waterConsumptionPerBeing = 0.0;

            if (being instanceof Plant) {
                waterConsumptionPerBeing = 0.05;
            } else if (being instanceof Animal) {
                waterConsumptionPerBeing = 2.0;
            }

            totalWaterConsumption += being.getPopulation() * waterConsumptionPerBeing;
        }

        conditions.decreaseWater(totalWaterConsumption);
    }

    private void printEnvironmentalConditions() {
        System.out.println("Environmental conditions:");
        System.out.println("Temperature: " + conditions.getTemperature());
        System.out.println("Humidity: " + conditions.getHumidity());
        System.out.println("Water available: " + conditions.getAvailableWater());
    }

    private void updateBasedOnEnvironment() {
        for (Being being : beings) {
            double survivalRate = calculateSurvivalRate(being);
            if (survivalRate > 0.8) {
                System.out.println(being.getName() + " population is stable under current conditions");
                continue;
            }
            int casualties = (int) (being.getPopulation() * (1 - survivalRate));
            if (casualties > 0) {
                being.adjustPopulation(-casualties);
                System.out.println(casualties + " " + being.getName() +
                        " didn't survive due to environmental conditions");
            }
        }
    }

    private void simulateFeeding() {
        for (Being being : beings) {
            if (being.getPopulation() <= 0) continue;

            if (being instanceof Animal animal) {
                simulateAnimalFeeding(animal);
            } else if (being instanceof Plant) {
                simulatePlantGrowth(being);
            }
        }
    }

    private void simulateAnimalFeeding(Animal animal) {
        double foodPerAnimal = 2.0;
        int requiredFood = (int) (animal.getPopulation() * foodPerAnimal);
        int totalFoodFound = 0;

        for (Being victim : animal.getVictims()) {
            if (victim.getPopulation() <= 0) continue;

            // Увеличиваем успех охоты при малом количестве хищников
            double huntingSuccess = calculateHuntingSuccess(animal, victim);
            // Увеличиваем процент успешной охоты
            int preysCaught = (int) (victim.getPopulation() * 0.4 * huntingSuccess);
            preysCaught = Math.min(preysCaught, victim.getPopulation());

            if (preysCaught > 0) {
                victim.adjustPopulation(-preysCaught);
                totalFoodFound += preysCaught;
                System.out.println(animal.getName() + " caught " + preysCaught + " " + victim.getName());
            }
        }

        updateAnimalPopulation(animal, totalFoodFound, requiredFood);
    }


    private double calculateHuntingSuccess(Animal predator, Being prey) {

        double baseSuccess = 0.8;


        double populationRatio = (double) predator.getPopulation() / prey.getPopulation();


        double successModifier = populationRatio <= 0.01 ? 1.2 :
                populationRatio <= 0.1 ? 1.0 :
                        Math.pow(0.8, populationRatio * 10);

        return Math.min(1.0, baseSuccess * successModifier);
    }

    private void updateAnimalPopulation(Animal animal, int foodFound, int requiredFood) {
        double foodRatio = (double) foodFound / requiredFood;

        if (foodRatio >= 1.0) {
            int growth = (int) (animal.getPopulation() * 0.3);
            growth = Math.max(1, growth);
            animal.adjustPopulation(growth);
            System.out.println(animal.getName() + " population grew by " + growth);
        } else if (foodRatio >= 0.7) {
            System.out.println(animal.getName() + " population remains stable");
        } else {
            int starved = (int) ((1 - foodRatio) * animal.getPopulation() * 0.7);
            animal.adjustPopulation(-starved);
            System.out.println(starved + " " + animal.getName() + " starved due to lack of food");
        }
    }

    private void simulatePlantGrowth(Being plant) {
        double growthRate = calculatePlantGrowthRate(plant);

        double densityFactor = Math.max(0.1, 1.0 - (plant.getPopulation() / 1000.0));
        growthRate *= densityFactor;

        if (growthRate > 0) {
            int growth = (int) (plant.getPopulation() * growthRate);
            growth = Math.max(5, growth);
            plant.adjustPopulation(growth);
            System.out.println(plant.getName() + " population increased by " + growth);
        } else {
            int decline = (int) (plant.getPopulation() * Math.abs(growthRate) * 0.6);
            plant.adjustPopulation(-decline);
            System.out.println(plant.getName() + " population decreased by " + decline);
        }
    }

    private void analyzeStability(Map<String, Integer> initialPopulations) {
        System.out.println("\nStability Analysis:");
        for (Being being : beings) {
            String name = being.getName();
            int initialPop = initialPopulations.get(name);
            int currentPop = being.getPopulation();

            PopulationHistory history = populationHistory.computeIfAbsent(name,
                    k -> new PopulationHistory());
            history.addPopulation(currentPop);

            String trend = analyzeTrend(history);
            double stability = calculateStabilityScore(history);

            System.out.printf("%s: %s (stability score: %.2f)%n",
                    name, trend, stability);

            suggestManagement(being, stability);
        }
    }

    private String analyzeTrend(PopulationHistory history) {
        if (history.getPopulations().size() < 2) return "Insufficient data";

        List<Integer> populations = history.getPopulations();
        int current = populations.get(populations.size() - 1);
        int previous = populations.get(populations.size() - 2);

        double change = (double) (current - previous) / previous;

        if (Math.abs(change) < 0.05) return "Stable";
        else if (change > 0) return "Growing (" + String.format("%.1f", change * 100) + "%)";
        else return "Declining (" + String.format("%.1f", Math.abs(change) * 100) + "%)";
    }

    private double calculateStabilityScore(PopulationHistory history) {
        List<Integer> populations = history.getPopulations();
        if (populations.size() < 2) return 1.0;

        double sum = 0;
        double count = 0;
        int lastPop = populations.get(populations.size() - 1);

        for (int i = populations.size() - 2; i >= 0; i--) {
            int pop = populations.get(i);
            double change = Math.abs((double) (lastPop - pop) / pop);
            sum += (1.0 - Math.min(change, 1.0));
            count++;
            lastPop = pop;
        }

        return count > 0 ? sum / count : 1.0;
    }

    private void suggestManagement(Being being, double stability) {
        if (stability < 0.5) {
            if (being instanceof Plant) {
                System.out.println("Suggestion for " + being.getName() + ": Adjust environmental conditions closer to optimal values");
            } else if (being instanceof Animal) {
                System.out.println("Suggestion for " + being.getName() + ": Check prey availability and competition levels");
            }
        }
    }

    private void updatePopulationHistory() {
        beings.forEach(being -> {
            PopulationHistory history = populationHistory.computeIfAbsent(
                    being.getName(), k -> new PopulationHistory());
            history.addPopulation(being.getPopulation());
        });
    }

    private void printEcosystemState() {
        System.out.println("\nEcosystem state after simulation:");
        beings.forEach(being ->
                System.out.println(being.getName() + " population: " + being.getPopulation()));
    }

    private double calculateSurvivalRate(Being being) {
        double optimal = being instanceof Plant ? 25 : 20;
        double tempFactor = 1 - Math.abs(conditions.getTemperature() - optimal) / 100.0;
        double humidityFactor = conditions.getHumidity() / 100.0;
        double waterFactor = Math.min(conditions.getAvailableWater() / 100.0, 1.0);

        return (tempFactor + humidityFactor + waterFactor) / 3.0;
    }

    private double calculatePlantGrowthRate(Being plant) {
        double tempFactor = 1 - Math.abs(conditions.getTemperature() - 25) / 50.0;
        double humidityFactor = conditions.getHumidity() / 100.0;
        double waterFactor = Math.min(conditions.getAvailableWater() / 100.0, 1.0);

        return 0.4 * ((tempFactor + humidityFactor + waterFactor) / 3.0);
    }
}
