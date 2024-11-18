package org.poo.main;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.fileio.ActionsInput;
import org.poo.fileio.CardInput;
import org.poo.fileio.DecksInput;
import org.poo.main.cards.Card;
import org.poo.main.cards.herocards.HeroCard;

import org.poo.fileio.Input;
import java.util.ArrayList;

public final class Game {
    /** The maximum number of mana that a player can get in a round */
    public static final int MAX_MANA_PER_ROUND = 10;
    /** The number of the current round */
    private int roundNr;
    private int playerTurn;
    private int totalTurns;

    /**
     * Constructor for the Game class which sets the number of the current round to 1,
     * the player turn to the starting player and the total number of turns to 0, since
     * the game just started.
     *
     * @param playerTurn - the player that starts the game
     */
    public Game(final int playerTurn) {
        this.roundNr = 1;
        this.playerTurn = playerTurn;
        this.totalTurns = 0;
    }

    /**
     * Method used to start a new game and make all the necessary initializations.
     * It creates the required instances to keep track of all the information regarding a game
     * and, at the end calls the method that processes all the actions given as input.
     *
     * @param inputData - an instance of the Input class that contains all the information
     *                  given as input
     * @param gameStats - an instance of the GameStats class that contains all the information
     *                 about the games played
     * @param output - an ArrayNode that is used to send data to the output
     * @param i - the index of the current game
     *
     * @see CommandHandler#handleCommands(ActionsInput, Game, Player, Player, Table,
     * GameStats, ArrayNode) method used to process all the actions given as input
     */
    public void start(final Input inputData, final GameStats gameStats, final ArrayNode output,
                      final int i) {
        // get the player decks indexes and the shuffle seed from the input data
        int playerOneDeckIdx = inputData.getGames().get(i).getStartGame().getPlayerOneDeckIdx();
        int playerTwoDeckIdx = inputData.getGames().get(i).getStartGame().getPlayerTwoDeckIdx();
        int shuffleSeed = inputData.getGames().get(i).getStartGame().getShuffleSeed();

        /* use the chooseDeck method from the DecksInput class to make a deep copy of the
        chosen deck for each player */
        ArrayList<Card> playerOneDeck = DecksInput.chooseDeck(playerOneDeckIdx,
                inputData.getPlayerOneDecks().getDecks());
        ArrayList<Card> playerTwoDeck = DecksInput.chooseDeck(playerTwoDeckIdx,
                inputData.getPlayerTwoDecks().getDecks());

        // shuffle the decks using the shuffleDeck method from the DecksInput class
        ArrayList<Card> playerOneShuffledDeck
                = DecksInput.shuffleDeck(playerOneDeck, shuffleSeed);
        ArrayList<Card> playerTwoShuffledDeck
                = DecksInput.shuffleDeck(playerTwoDeck, shuffleSeed);

        // get the hero cards for each player from the input
        CardInput playerOneHeroCard = inputData.getGames().get(i).getStartGame().getPlayerOneHero();
        CardInput playerTwoHeroCard = inputData.getGames().get(i).getStartGame().getPlayerTwoHero();

        // make a deep copy of the hero cards for each player
        HeroCard playerOneHero = HeroCard.createHeroCard(playerOneHeroCard);
        HeroCard playerTwoHero = HeroCard.createHeroCard(playerTwoHeroCard);

        //create an instance for each player and initialise them with the specific decks and heroes
        Player playerOne = new Player(Player.PLAYER_ONE_ID, playerOneHero, playerOneShuffledDeck);
        Player playerTwo = new Player(Player.PLAYER_TWO_ID, playerTwoHero, playerTwoShuffledDeck);

        // make each player draw a card at the beginning of the game
        playerOne.drawCard();
        playerTwo.drawCard();

        // create an instance of the table
        Table table = new Table();

        // set the initial state of the game to be not over
        gameStats.setGameOver(false);

        // process all the commands given as input by calling the handleCommands method
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
