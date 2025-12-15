package dicegame.model;

public class GameEngine {
    private static GameEngine instance;

    private Player player1;
    private Player player2;

    private final int totalRounds = 3;
    private int currentRound = 1;
    private int currentTurn = 1; // 1 or 2
    private int rollsThisRound = 0; // counts how many players rolled in current round

    private DiceStrategy dice1;
    private DiceStrategy dice2;

    private GameEngine() {
        player1 = PlayerFactory.createPlayer("Player1");
        player2 = PlayerFactory.createPlayer("Player2");

        // default both players use same normal dice
        dice1 = new NormalDice();
        dice2 = new NormalDice();
    }

    public static GameEngine getInstance() {
        if (instance == null) {
            instance = new GameEngine();
        }
        return instance;
    }

    public void startNewGame() {
        player1.resetScore();
        player2.resetScore();
        currentRound = 1;
        currentTurn = 1;
        rollsThisRound = 0;
        // reset dice strategies (default)
        dice1 = new NormalDice();
        dice2 = new NormalDice();
    }

    public Player getPlayer1() { return player1; }
    public Player getPlayer2() { return player2; }
    public Player getCurrentPlayer() { return (currentTurn == 1) ? player1 : player2; }
    public int getCurrentRound() { return currentRound; }
    public int getTotalRounds() { return totalRounds; }
    public int getCurrentTurn() { return currentTurn; }

    // roll for the current player, update score and internal state, return roll value
    public int rollForCurrentPlayer() {
        if (isGameOver()) return 0;

        DiceStrategy dice = (currentTurn == 1) ? dice1 : dice2;
        int roll = dice.roll();

        if (currentTurn == 1) {
            player1.addScore(roll);
        } else {
            player2.addScore(roll);
        }

        rollsThisRound++;

        // if both players have rolled this round -> next round
        if (rollsThisRound >= 2) {
            rollsThisRound = 0;
            currentRound++;
            currentTurn = 1; // always start next round with player1
        } else {
            // otherwise change turn
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
        if (s1 > s2) return player1.getName() + " won! (" + s1 + " vs " + s2 + ")";
        else if (s1 == s2) return "Draw! (" + s1 + " vs " + s2 + ")";
        else return player2.getName() + " won! (" + s2 + " vs " + s1 + ")";
    }

    // optional: allow setting a dice strategy per player (Strategy + Factory usage)
    public void setDiceForPlayer(int playerNumber, DiceStrategy diceStrategy) {
        if (playerNumber == 1) dice1 = diceStrategy;
        else dice2 = diceStrategy;
    }
}
