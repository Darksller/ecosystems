package models;

import interfaces.DietInterface;

public class Animal extends Being {

    private final DietInterface diet;

    public Animal(String name, int population, DietInterface diet) {
        super(name, population);
        this.diet = diet;
    }

    public DietInterface getDiet() {
        return diet;
    }

    public void interact(Being other) {
        if (getVictims().contains(other)) {
            diet.eat(this, other);
        } else {
            System.out.println(getName() + " cannot eat " + other.getName());
        }
    }

    public void addVictim(Being being) {
        if (diet instanceof Herbivore && !(being instanceof Plant)) {
            System.out.println(getName() + " cannot hunt animals. It only eats plants.");
        } else super.addVictim(being);
    }
}
