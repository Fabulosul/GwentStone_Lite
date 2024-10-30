package main;

public class Game {
    private int roundNr;
    private int playerTurn;
    private int totalTurns;

    public Game(int playerTurn) {
        this.roundNr = 1;
        this.playerTurn = playerTurn;
        this.totalTurns = 0;
    }

    public int getNrRound() {
        return roundNr;
    }

    public void setNrRound(int nrRound) {
        this.roundNr = nrRound;
    }

    public int getPlayerTurn() {
        return playerTurn;
    }

    public void setPlayerTurn(int playerTurn) {
        this.playerTurn = playerTurn;
    }

    public int getTotalTurns() {
        return totalTurns;
    }

    public void setTotalTurns(int totalTurns) {
        this.totalTurns = totalTurns;
    }
}
