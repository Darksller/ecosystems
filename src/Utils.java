import java.io.*;
import java.util.Scanner;

import interfaces.DietInterface;
import models.*;

import java.util.List;

public final class Utils {
    private static final Scanner scanner = new Scanner(System.in);
    private static final String ECOSYSTEMS_DIRECTORY = "ecosystems/";

    public static Ecosystem loadExistingEcosystem() {
        File directory = new File(ECOSYSTEMS_DIRECTORY);
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".txt"));

        if (files == null || files.length == 0) {
            System.out.println("No saved ecosystems found.");
            return null;
        }

        System.out.println("\nAvailable ecosystems:");
        for (int i = 0; i < files.length; i++) {
            System.out.println((i + 1) + ". " + files[i].getName().replace(".txt", ""));
        }

        int choice = getIntInput("Select ecosystem (0 to return): ");
        if (choice == 0 || choice > files.length) return null;

        try (BufferedReader reader = new BufferedReader(new FileReader(files[choice - 1]))) {
            String name = reader.readLine().split(":")[1].trim();
            String[] conditions = reader.readLine().split(":");
            double temperature = Double.parseDouble(conditions[1].trim());
            double humidity = Double.parseDouble(conditions[2].trim());
            double water = Double.parseDouble(conditions[3].trim());

            Ecosystem ecosystem = new Ecosystem(name, new Conditions(temperature, humidity, water));

            int beingsCount = Integer.parseInt(reader.readLine().split(":")[1].trim());

            for (int i = 0; i < beingsCount; i++) {
                String beingType = reader.readLine().trim();
                String beingName = reader.readLine().split(":")[1].trim();
                int population = Integer.parseInt(reader.readLine().split(":")[1].trim());

                if (beingType.equals("Plant")) {
                    ecosystem.addSpecies(new Plant(beingName, population));
                } else {
                    String dietType = reader.readLine().split(":")[1].trim();
                    DietInterface diet = dietType.equals("Carnivore") ? new Carnivore() : new Herbivore();
                    Animal animal = new Animal(beingName, population, diet);

                    int victimsCount = Integer.parseInt(reader.readLine().split(":")[1].trim());
                    for (int j = 0; j < victimsCount; j++) {
                        String victimName = reader.readLine().trim();
                        ecosystem.getBeings().stream()
                                .filter(b -> b.getName().equals(victimName))
                                .findFirst().ifPresent(animal::addVictim);
                    }
                    ecosystem.addSpecies(animal);
                }
            }

            System.out.println("Ecosystem loaded successfully!");
            return ecosystem;
        } catch (IOException e) {
            System.out.println("Error loading ecosystem: " + e.getMessage());
            return null;
        }
    }

    public static void saveEcosystem(Ecosystem ecosystem, String name) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ECOSYSTEMS_DIRECTORY + name + ".txt"))) {

            writer.println("Name:" + ecosystem.getName());
            Conditions conditions = ecosystem.getConditions();
            writer.println("Conditions:" + conditions.getTemperature() + ":" +
                    conditions.getHumidity() + ":" + conditions.getAvailableWater());

            List<Being> beings = ecosystem.getBeings();
            writer.println("BeingsCount:" + beings.size());

            for (Being being : beings) {
                if (being instanceof Plant) {
                    writer.println("Plant");
                    writer.println("Name:" + being.getName());
                    writer.println("Population:" + being.getPopulation());
                } else if (being instanceof Animal animal) {
                    writer.println("Animal");
                    writer.println("Name:" + animal.getName());
                    writer.println("Population:" + animal.getPopulation());
                    writer.println("Diet:" + (animal.getDiet() instanceof Carnivore ? "Carnivore" : "Herbivore"));

                    List<Being> victims = animal.getVictims();
                    writer.println("VictimsCount:" + victims.size());
                    for (Being victim : victims) {
                        writer.println(victim.getName());
                    }
                }
            }
            System.out.println("Ecosystem saved successfully: " + name);
        } catch (IOException e) {
            System.out.println("Error saving ecosystem: " + e.getMessage());
        }
    }

    public static void createDirectoryIfNotExists() {
        File directory = new File(ECOSYSTEMS_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    public static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    public static double getDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}