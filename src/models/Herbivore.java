package models;

import interfaces.DietInterface;

public class Herbivore implements DietInterface {
    @Override
    public void eat(Being predator, Being plant) {
        if (plant instanceof Plant && plant.getPopulation() > 0) {
            System.out.println(predator.getName() + " eats the plant: " + plant.getName());
            plant.decreasePopulation();
        } else {
            System.out.println(predator.getName() + " cannot eat " + plant.getName());
        }
    }
}
