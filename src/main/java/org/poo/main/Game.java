package org.poo.main;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.fileio.ActionsInput;
import org.poo.fileio.CardInput;
import org.poo.fileio.DecksInput;
import org.poo.main.cards.Card;
import org.poo.main.cards.herocards.HeroCard;

import org.poo.fileio.ActionsInput;
import org.poo.fileio.CardInput;
import org.poo.fileio.DecksInput;
import org.poo.fileio.Input;
import org.poo.main.cards.Card;
import org.poo.main.cards.herocards.HeroCard;

import java.util.ArrayList;

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

    public void start(Input inputData, GameStats gameStats, ArrayNode output, int i) {
        int playerOneDeckIdx = inputData.getGames().get(i).getStartGame().getPlayerOneDeckIdx();
        int playerTwoDeckIdx = inputData.getGames().get(i).getStartGame().getPlayerTwoDeckIdx();
        int shuffleSeed = inputData.getGames().get(i).getStartGame().getShuffleSeed();

        ArrayList<Card> playerOneDeck = DecksInput.chooseDeck(playerOneDeckIdx,
                inputData.getPlayerOneDecks().getDecks());
        ArrayList<Card> playerTwoDeck = DecksInput.chooseDeck(playerTwoDeckIdx,
                inputData.getPlayerTwoDecks().getDecks());

        ArrayList<Card> playerOneShuffledDeck
                = DecksInput.shuffleDeck(playerOneDeck, shuffleSeed);
        ArrayList<Card> playerTwoShuffledDeck
                = DecksInput.shuffleDeck(playerTwoDeck, shuffleSeed);

        CardInput playerOneHeroCard = inputData.getGames().get(i).getStartGame().getPlayerOneHero();
        CardInput playerTwoHeroCard = inputData.getGames().get(i).getStartGame().getPlayerTwoHero();

        HeroCard playerOneHero = HeroCard.createHeroCard(playerOneHeroCard);
        HeroCard playerTwoHero = HeroCard.createHeroCard(playerTwoHeroCard);

        Player playerOne = new Player(Player.PLAYER_ONE_ID, playerOneHero, playerOneShuffledDeck);
        Player playerTwo = new Player(Player.PLAYER_TWO_ID, playerTwoHero, playerTwoShuffledDeck);

        playerOne.drawCard();
        playerTwo.drawCard();

        Table table = new Table();

        gameStats.setGameOver(false);

        int nrActions = inputData.getGames().get(i).getActions().size();
        for (int j = 0; j < nrActions; j++) {
            ActionsInput currentAction = inputData.getGames().get(i).getActions().get(j);
            CommandHandler.handleCommands(currentAction, this, playerOne, playerTwo, table,
                    gameStats, output);
        }
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
