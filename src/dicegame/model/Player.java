package dicegame.model;

public class Player {
    private String name;
    private int totalScore;

    public Player(String name) {
        this.name = name;
        this.totalScore = 0;
    }

    public void addScore(int value) {
        this.totalScore += value;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void resetScore() {
        this.totalScore = 0;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
