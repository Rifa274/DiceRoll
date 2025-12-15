package dicegame.model;

import java.util.Random;

public class NormalDice implements DiceStrategy {
    private final Random random = new Random();

    @Override
    public int roll() {
        return random.nextInt(6) + 1;
    }
}
