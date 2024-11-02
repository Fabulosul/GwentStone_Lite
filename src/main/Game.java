package main;

public final class Game {
    public static final int MAX_MANA_PER_ROUND = 10;
    private int roundNr;
    private int playerTurn;
    private int totalTurns;

    public Game(final int playerTurn) {
        this.roundNr = 1;
        this.playerTurn = playerTurn;
        this.totalTurns = 0;
    }

    public int getNrRound() {
        return roundNr;
    }

    public void setNrRound(final int nrRound) {
        this.roundNr = nrRound;
    }

    public int getPlayerTurn() {
        return playerTurn;
    }

    public void setPlayerTurn(final int playerTurn) {
        this.playerTurn = playerTurn;
    }

    public int getTotalTurns() {
        return totalTurns;
    }

    public void setTotalTurns(final int totalTurns) {
        this.totalTurns = totalTurns;
    }
}
