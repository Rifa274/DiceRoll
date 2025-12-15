package dicegame.model;

public class GameEngine {
    private static GameEngine instance;

    private Player player1;
    private Player player2;

    private final int totalRounds = 3;
    private int currentRound = 1;
    private int currentTurn = 1; 
    private int rollsThisRound = 0;

    private DiceStrategy dice1;
    private DiceStrategy dice2;

    private GameEngine() {
        player1 = PlayerFactory.createPlayer("Player 1");
        player2 = PlayerFactory.createPlayer("Player 2");
        dice1 = new NormalDice();
        dice2 = new NormalDice();
    }

    public static GameEngine getInstance() {
        if (instance == null) instance = new GameEngine();
        return instance;
    }

    public void startNewGame() {
        player1.resetScore();
        player2.resetScore();
        currentRound = 1;
        currentTurn = 1;
        rollsThisRound = 0;
        dice1 = new NormalDice();
        dice2 = new NormalDice();
    }

    public Player getPlayer1() { return player1; }
    public Player getPlayer2() { return player2; }
    public int getCurrentRound() { return currentRound; }
    public int getTotalRounds() { return totalRounds; }
    public int getCurrentTurn() { return currentTurn; }

    public int rollForCurrentPlayer() {
        if (isGameOver()) return 0;

        DiceStrategy dice = (currentTurn == 1) ? dice1 : dice2;
        int roll = dice.roll();

        if (currentTurn == 1) player1.addScore(roll);
        else player2.addScore(roll);

        rollsThisRound++;

        if (rollsThisRound >= 2) {
            rollsThisRound = 0;
            currentRound++;
            currentTurn = 1; 
        } else {
            nextTurn();
        }
        return roll;
    }

    public void nextTurn() {
        currentTurn = (currentTurn == 1) ? 2 : 1;
    }

    public boolean isGameOver() {
        return currentRound > totalRounds;
    }

    public String determineWinnerMessage() {
        int s1 = player1.getTotalScore();
        int s2 = player2.getTotalScore();
        if (s1 > s2) return player1.getName() + " Wins!";
        else if (s1 == s2) return "It's a Draw!";
        else return player2.getName() + " Wins!";
    }
}