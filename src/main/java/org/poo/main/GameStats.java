package org.poo.main;

public final class GameStats {
    private int playerOneWins;
    private int playerTwoWins;
    private int totalGamesPlayed;
    private boolean isGameOver;

    /**
     * Constructor for the GameStats class which sets the number of wins for player one
     * and player two to 0 and the total number of games played to 0 at the beginning of a game.
     */
    public GameStats() {
        this.playerOneWins = 0;
        this.playerTwoWins = 0;
        this.totalGamesPlayed = 0;
    }

    public int getPlayerOneWins() {
        return playerOneWins;
    }

    public void setPlayerOneWins(final int playerOneWins) {
        this.playerOneWins = playerOneWins;
    }

    public int getPlayerTwoWins() {
        return playerTwoWins;
    }

    public void setPlayerTwoWins(final int playerTwoWins) {
        this.playerTwoWins = playerTwoWins;
    }

    public int getTotalGamesPlayed() {
        return totalGamesPlayed;
    }

    public void setTotalGamesPlayed(final int totalGamesPlayed) {
        this.totalGamesPlayed = totalGamesPlayed;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public void setGameOver(final boolean gameOver) {
        isGameOver = gameOver;
    }
}
