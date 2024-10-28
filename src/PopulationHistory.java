import java.util.ArrayList;
import java.util.List;

public final class PopulationHistory {
    private static final int HISTORY_SIZE = 5;
    private final List<Integer> populations = new ArrayList<>();

    public void addPopulation(int population) {
        populations.add(population);
        if (populations.size() > HISTORY_SIZE) {
            populations.remove(0);
        }
    }

    public List<Integer> getPopulations() {
        return new ArrayList<>(populations);
    }
}