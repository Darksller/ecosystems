package models;

import interfaces.DietInterface;

public class Carnivore implements DietInterface {
    @Override
    public void eat(Being predator, Being victim) {
        if (victim.getPopulation() > 0) {
            System.out.println(predator.getName() + " hunts and eats " + victim.getName());
            victim.decreasePopulation();
        } else {
            System.out.println(predator.getName() + " cannot eat a" + victim.getName() + " because they're extinct......");
        }
    }
}
