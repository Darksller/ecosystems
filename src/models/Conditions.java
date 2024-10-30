package models;

public class Conditions {
    private double temperature;
    private double humidity;
    private double availableWater;

    public Conditions(double temperature, double humidity, double availableWater) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.availableWater = availableWater;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public double getAvailableWater() {
        return availableWater;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public void setAvailableWater(double availableWater) {
        this.availableWater = availableWater;
    }

    public void decreaseWater(double amount) {
        this.availableWater -= amount;
        if (this.availableWater < 0) {
            this.availableWater = 0;
        }
    }
}

