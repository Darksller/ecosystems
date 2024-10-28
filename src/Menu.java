import models.*;

import java.util.ArrayList;
import java.util.List;

public class Menu {
    private List<Being> currentBeings;

    public Menu() {
        this.currentBeings = new ArrayList<>();
    }

    public void start() {
        Utils.createDirectoryIfNotExists();
        while (true) {
            displayMainMenu();
            int choice = Utils.getIntInput("Select action: ");

            switch (choice) {
                case 1:
                    Ecosystem loadedEcosystem = Utils.loadExistingEcosystem();
                    if (loadedEcosystem != null) {
                        currentBeings = loadedEcosystem.getBeings();
                        manageEcosystem(loadedEcosystem);
                    }
                    break;
                case 2:
                    createNewEcosystem();
                    break;
                case 3:
                    System.out.println("Exiting program...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void displayMainMenu() {
        System.out.println("\n=== Ecosystem Simulator ===");
        System.out.println("1. Load Existing Ecosystem");
        System.out.println("2. Create New Ecosystem");
        System.out.println("3. Exit");
    }

    private void createNewEcosystem() {
        System.out.println("\n=== Creating New Ecosystem ===");
        String name = Utils.getStringInput("Enter ecosystem name: ");

        double temperature = Utils.getDoubleInput("Enter temperature: ");
        double humidity = Utils.getDoubleInput("Enter humidity (0-100): ");
        double water = Utils.getDoubleInput("Enter available water amount: ");
        Conditions conditions = new Conditions(temperature, humidity, water);

        Ecosystem ecosystem = new Ecosystem(name, conditions);
        currentBeings = ecosystem.getBeings();

        manageEcosystem(ecosystem);
    }

    private void manageEcosystem(Ecosystem ecosystem) {
        while (true) {
            displayEcosystemMenu();
            int choice = Utils.getIntInput("Select action: ");

            switch (choice) {
                case 1:
                    addCarnivore(ecosystem);
                    break;
                case 2:
                    addHerbivore(ecosystem);
                    break;
                case 3:
                    addPlant(ecosystem);
                    break;
                case 4:
                    ecosystem.simulateInteractions();
                    break;
                case 5:
                    Utils.saveEcosystem(ecosystem, ecosystem.getName());
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void displayEcosystemMenu() {
        System.out.println("\n\\\\\\ Ecosystem Management ///");
        System.out.println("1. Add Carnivore");
        System.out.println("2. Add Herbivore");
        System.out.println("3. Add Plant");
        System.out.println("4. Simulate Interactions");
        System.out.println("5. Save and Exit");
    }

    private void addPlant(Ecosystem ecosystem) {
        String name = Utils.getStringInput("Enter plant name: ");
        int population = Utils.getIntInput("Enter population size: ");
        Plant plant = new Plant(name, population);
        ecosystem.addSpecies(plant);
        System.out.println("Plant added to ecosystem.");
    }

    private void addCarnivore(Ecosystem ecosystem) {
        String name = Utils.getStringInput("Enter carnivore name: ");
        int population = Utils.getIntInput("Enter population size: ");
        Animal carnivore = new Animal(name, population, new Carnivore());

        System.out.println("\nSelect possible victims for " + name + ":");
        selectVictims(carnivore);

        ecosystem.addSpecies(carnivore);
        System.out.println("Carnivore added to ecosystem.");
    }

    private void selectVictims(Animal predator) {
        if (currentBeings.isEmpty()) {
            System.out.println("No potential victims available yet.");
            return;
        }

        while (true) {
            System.out.println("\nAvailable species:");
            for (int i = 0; i < currentBeings.size(); i++) {
                Being being = currentBeings.get(i);
                System.out.println((i + 1) + ". " + being.getName() +
                        " (Type: " + being.getClass().getSimpleName() +
                        ", Population: " + being.getPopulation() + ")");
            }
            System.out.println("0. Finish selection");

            int choice = Utils.getIntInput("Select victim (0 to finish): ");
            if (choice == 0) break;

            if (choice > 0 && choice <= currentBeings.size()) {
                Being victim = currentBeings.get(choice - 1);
                if (!predator.getVictims().contains(victim)) {
                    predator.addVictim(victim);
                    System.out.println(victim.getName() + " added as prey for " + predator.getName());
                } else {
                    System.out.println("This species is already a prey.");
                }
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }

    private void addHerbivore(Ecosystem ecosystem) {
        String name = Utils.getStringInput("Enter herbivore name: ");
        int population = Utils.getIntInput("Enter population size: ");
        Animal herbivore = new Animal(name, population, new Herbivore());

        System.out.println("\nSelect plants that " + name + " can eat:");
        selectVictimsForHerbivore(herbivore);

        ecosystem.addSpecies(herbivore);
        System.out.println("Herbivore added to ecosystem.");
    }

    private void selectVictimsForHerbivore(Animal herbivore) {
        List<Being> availablePlants = currentBeings.stream()
                .filter(being -> being instanceof Plant)
                .toList();

        if (availablePlants.isEmpty()) {
            System.out.println("No plants available yet.");
            return;
        }

        while (true) {
            System.out.println("\nAvailable plants:");
            for (int i = 0; i < availablePlants.size(); i++) {
                Plant plant = (Plant) availablePlants.get(i);
                System.out.println((i + 1) + ". " + plant.getName() +
                        " (Population: " + plant.getPopulation() + ")");
            }
            System.out.println("0. Finish selection");

            int choice = Utils.getIntInput("Select plant to eat (0 to finish): ");
            if (choice == 0) break;

            if (choice > 0 && choice <= availablePlants.size()) {
                Plant plant = (Plant) availablePlants.get(choice - 1);
                if (!herbivore.getVictims().contains(plant)) {
                    herbivore.addVictim(plant);
                    System.out.println(plant.getName() + " added as food for " + herbivore.getName());
                } else {
                    System.out.println("This plant is already in the diet.");
                }
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }
}