package models;

import java.util.ArrayList;
import java.util.List;

public class Being {
    private final String name;
    private int population;
    private final List<Being> victims;

    public Being(String name, int population) {
        this.name = name;
        this.population = population;
        this.victims = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public int getPopulation() {
        return population;
    }

    public List<Being> getVictims() {
        return victims;
    }

    public void addVictim(Being victim) {
        this.victims.add(victim);
    }

    public void decreasePopulation() {
        if (population > 0) {
            population--;
        }
    }

    public void increasePopulation() {
        population++;
    }

    public void adjustPopulation(int delta) {
        this.population = Math.max(0, this.population + delta);
    }
}

