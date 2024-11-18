package org.poo.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.ActionsInput;
import org.poo.fileio.Coordinates;
import org.poo.main.cards.Card;
import org.poo.main.cards.specialabilitycards.SpecialAbilityCard;


import java.util.ArrayList;


public final class Command {
    /** The command that is read from the input. */
    private String command;

    /**
     * Constructor for the Command class which initializes the command field.
     *
     * @param command - the command that is read from the input
     */
    public Command(final String command) {
        this.command = command;
    }

    /**
     * Method that makes the changes needed at the end of a player's turn.
     * It increments the total number of turns, resets all the isFrozen
     * fields of the cards used the previous turn, increments the mana
     * of the players, makes the players draw another card, changes the current player
     * turn and checks if the game is over.
     * It also verifies if both players finished the current round and if they did
     * it resets the properties of the cards on the table.
     * @param game - an instance of the current game
     * @param playerOne - an instance of the player one
     * @param playerTwo - an instance of the player two
     * @param table - the game board where all cards that are placed are stored
     * @param gameStats - information about the played games
     */
    public static void endPlayerTurn(final Game game, final Player playerOne,
                                     final Player playerTwo, final Table table,
                                     final GameStats gameStats) {
        game.setTotalTurns(game.getTotalTurns() + 1);

        if (game.getPlayerTurn() == Player.PLAYER_ONE_ID) {
            for (int i = Table.PLAYER_ONE_FRONT_ROW; i <= Table.PLAYER_ONE_BACK_ROW; i++) {
                for (int j = 0; j < table.getTableCards().get(i).size(); j++) {
                    Card currentCard = table.getTableCards().get(i).get(j);
                    if (currentCard.isFrozen()) {
                        currentCard.setIsFrozen(false);
                    }
                }
            }
        } else {
            for (int i = Table.PLAYER_TWO_BACK_ROW; i <= Table.PLAYER_TWO_FRONT_ROW; i++) {
                for (int j = 0; j < table.getTableCards().get(i).size(); j++) {
                    Card currentCard = table.getTableCards().get(i).get(j);
                    if (currentCard.isFrozen()) {
                        currentCard.setIsFrozen(false);
                    }
                }
            }
        }
        if (!gameStats.isGameOver()) {
            if (game.getPlayerTurn() == Player.PLAYER_ONE_ID) {
                game.setPlayerTurn(Player.PLAYER_TWO_ID);
            } else {
                game.setPlayerTurn(Player.PLAYER_ONE_ID);
            }
            if (game.getTotalTurns() % 2 == 0) {
                game.setNrRound(game.getNrRound() + 1);
                if (game.getNrRound() <= Game.MAX_MANA_PER_ROUND) {
                    playerOne.setMana(playerOne.getMana() + game.getNrRound());
                    playerTwo.setMana(playerTwo.getMana() + game.getNrRound());
                } else {
                    playerOne.setMana(playerOne.getMana() + Game.MAX_MANA_PER_ROUND);
                    playerTwo.setMana(playerTwo.getMana() + Game.MAX_MANA_PER_ROUND);
                }
                playerOne.drawCard();
                playerTwo.drawCard();
                playerOne.getHero().setHasUsedHeroAbility(false);
                playerTwo.getHero().setHasUsedHeroAbility(false);
                table.resetCardProperties();
            }
        }
    }

    /**
     * Method that handles the case when a player tries to place a card on the table.
     * It checks if the player can place the card by calling the placeCard method from the
     * Player class.
     * The method called also handles the actual placement of the card if all the requirements
     * are met.
     *
     * @param currentAction - an instance that keeps all the information about the current command
     * @param game - the instance of the current game
     * @param playerOne - the instance of the first player
     * @param playerTwo - the instance of the second player
     * @param table - the game board where all cards that are placed are stored
     * @param output - the array node that will be sent to the output
     *
     * @see Player#placeCard method which handles both the positive and negative cases when
     * trying to place a card on the table
     */
    public static void processPlaceCard(final ActionsInput currentAction, final Game game,
                                        final Player playerOne, final Player playerTwo,
                                        final Table table, final ArrayNode output) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();

        boolean canPlaceCard;
        if (game.getPlayerTurn() == Player.PLAYER_ONE_ID) {
            canPlaceCard = playerOne.placeCard(currentAction, objectNode, table);
        } else {
            canPlaceCard = playerTwo.placeCard(currentAction, objectNode, table);
        }
        if (!canPlaceCard) {
            output.add(objectNode);
        }
    }

    /**
     * Method that handles the case when a player tries to use a card to attack another card.
     * It checks if the card can attack by calling the cardUsesAttack method from the Card class.
     * The called method also takes care of the actual attack if all the conditions are met.
     *
     *
     * @param currentAction - an instance that keeps all the information about the current command
     * @param table - the game board where all cards that are placed are stored
     * @param output - the array node that will be sent to the output
     *
     * @see Card#cardUsesAttack method for more information about the actual case handling
     */
    public static void processCardUsesAttack(final ActionsInput currentAction, final Table table,
                                             final ArrayNode output) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();

        Coordinates cardAttackerCoordinates = currentAction.getCardAttacker();
        Coordinates cardAttackedCoordinates = currentAction.getCardAttacked();

        ArrayList<Card> cardAttackerRow
                = table.getTableCards().get(cardAttackerCoordinates.getX());
        Card cardAttacker = cardAttackerRow.get(cardAttackerCoordinates.getY());

        ArrayList<Card> cardAttackedRow
                = table.getTableCards().get(cardAttackedCoordinates.getX());
        Card cardAttacked = cardAttackedRow.get(cardAttackedCoordinates.getY());

        boolean hasAttacked = cardAttacker.cardUsesAttack(cardAttacked, cardAttackerCoordinates,
                cardAttackedCoordinates, table, objectNode, mapper);

        if (!hasAttacked) {
            output.add(objectNode);
        }
    }

    /**
     * Method that handles the case when a card tries to use its special ability on another card.
     * It checks if the card can use its ability by calling the cardUsesAbility method from the
     * SpecialAbilityCard class.
     * The called method also takes care of the actual use of ability if all the conditions are met.
     *
     * @param currentAction - an instance that keeps all the information about the current command
     * @param table - the game board where all cards that are placed are stored
     * @param output - the array node that will be sent to the output
     *
     * @see SpecialAbilityCard#cardUsesAbility method for more information about the
     * actual case handling
     */
    public static void processCardUsesAbility(final ActionsInput currentAction, final Table table,
                                              final ArrayNode output) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();

        Coordinates cardAttackerCoordinates = currentAction.getCardAttacker();
        Coordinates cardAttackedCoordinates = currentAction.getCardAttacked();

        ArrayList<Card> cardAttackerRow
                = table.getTableCards().get(cardAttackerCoordinates.getX());
        Card cardAttacker = cardAttackerRow.get(cardAttackerCoordinates.getY());

        ArrayList<Card> cardAttackedRow
                = table.getTableCards().get(cardAttackedCoordinates.getX());
        Card cardAttacked = cardAttackedRow.get(cardAttackedCoordinates.getY());

        if (!cardAttacker.hasSpecialAbility()) {
            return;
        }

        boolean hasUsedAbility = ((SpecialAbilityCard) cardAttacker).cardUsesAbility(cardAttacked,
                cardAttackerCoordinates, cardAttackedCoordinates,
                table, objectNode, mapper);

        if (!hasUsedAbility) {
            output.add(objectNode);
        }
    }

    /**
     * Method that processes the case when a player tries to attack the hero of the opponent.
     * It checks if the card can attack the hero by calling the useAttackHero method
     * from the Card class.
     * That method also handles the actual attack if all the requirements are met.
     * If a hero's health reaches 0, the isGameOver field from the gameStats is set to true,
     * the total number of games played is incremented and the number of wins for the player
     * that won the game is incremented.
     *
     * @param currentAction - an instance that keeps all the information about the current command
     * @param table - the game board where all cards that are placed are stored
     * @param playerOne - the instance of the first player
     * @param playerTwo - the instance of the second player
     * @param gameStats - an instance that keeps information about the played games
     * @param output - the array node that will be sent to the output
     */
    public static void processUseAttackHero(final ActionsInput currentAction, final Table table,
                                     final Player playerOne, final Player playerTwo,
                                     final GameStats gameStats,
                                     final ArrayNode output) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();

        Coordinates cardAttackerCoordinates = currentAction.getCardAttacker();

        ArrayList<Card> cardAttackerRow
                = table.getTableCards().get(cardAttackerCoordinates.getX());
        Card cardAttacker = cardAttackerRow.get(cardAttackerCoordinates.getY());

        boolean hasAttackedHero;
        if (Player.getPlayerByRow(cardAttackerCoordinates.getX()) == 1) {
            hasAttackedHero = cardAttacker.useAttackHero(cardAttackerCoordinates,
                    playerTwo.getHero(), table,
                    objectNode, mapper);
        } else {
            hasAttackedHero = cardAttacker.useAttackHero(cardAttackerCoordinates,
                    playerOne.getHero(), table,
                    objectNode, mapper);
        }
        if (!hasAttackedHero) {
            output.add(objectNode);
        }
        if (playerOne.getHero().getHealth() == 0 && !gameStats.isGameOver()) {
            output.add(objectNode);
            gameStats.setPlayerTwoWins(gameStats.getPlayerTwoWins() + 1);
            gameStats.setTotalGamesPlayed(gameStats.getTotalGamesPlayed() + 1);
            gameStats.setGameOver(true);
        }
        if (playerTwo.getHero().getHealth() == 0 && !gameStats.isGameOver()) {
            output.add(objectNode);
            gameStats.setPlayerOneWins(gameStats.getPlayerOneWins() + 1);
            gameStats.setTotalGamesPlayed(gameStats.getTotalGamesPlayed() + 1);
            gameStats.setGameOver(true);
        }
    }

    /**
     * Method that processes the case when a player tries to use the ability of his hero.
     * It checks if the player can use the hero's ability by calling the useHeroAbility method
     * from the HeroCard class.
     * Depending on the current player turn, the method is called for the hero of the first or
     * the second player.
     * The called method also handles the actual use of the ability if all the requirements are met.
     *
     * @param currentAction - an instance that keeps all the information about the current command
     * @param game - the instance of the current game
     * @param playerOne - the instance of the first player
     * @param playerTwo - the instance of the second player
     * @param table - the game board where all cards that are placed are stored
     * @param output - the array node that will be sent to the output
     *
     * @see org.poo.main.cards.herocards.HeroCard#useHeroAbility(ArrayList)  method for
     * more information about the actual case handling
     */
    public static void processUseHeroAbility(final ActionsInput currentAction, final Game game,
                                             final Player playerOne, final Player playerTwo,
                                             final Table table, final ArrayNode output) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();

        int affectedRow = currentAction.getAffectedRow();
        int currentPlayerTurn = game.getPlayerTurn();
        boolean heroUsedAbility;
        if (currentPlayerTurn == Player.PLAYER_ONE_ID) {
            heroUsedAbility = playerOne.getHero().useHeroAbility(affectedRow, currentPlayerTurn,
                    playerOne, table, objectNode);
        } else {
            heroUsedAbility = playerTwo.getHero().useHeroAbility(affectedRow, currentPlayerTurn,
                    playerTwo, table, objectNode);
        }
        if (!heroUsedAbility) {
            output.add(objectNode);
        }
    }

    /**
     * Method used to process the case when trying to retrieve information about a card
     * at a certain position on the table.
     * It calls the addCardAtPosToArr method to put the data in the output ArrayNode.
     * @param currentAction - an instance that keeps all the information about the current command
     * @param table - the game board where all cards that are placed are stored
     * @param output - the array node that will be sent to the output
     *
     * @see Table#addCardAtPosToArr method for more information about the actual use of the method
     */
    public static void processGetCardAtPosition(final ActionsInput currentAction, final Table table,
                                                final ArrayNode output) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();

        int cardRow = currentAction.getX();
        int cardColumn = currentAction.getY();

        table.addCardAtPosToArr(table, cardRow, cardColumn, objectNode, mapper);
        output.add(objectNode);
    }

    /**
     * Method used to process the case when trying to retrieve the current value of
     * the mana of a player.
     * It gets the number from the getMana getter and adds the received information to
     * the output ArrayNode.
     *
     * @param currentAction - an instance that keeps all the information about the current command
     * @param playerOne - the instance of the first player
     * @param playerTwo - the instance of the second player
     * @param output - the array node that will be sent to the output
     *
     * @see Player#getMana method for more information about the actual use of the method
     */
    public static void processGetPlayerMana(final ActionsInput currentAction,
                                            final Player playerOne, final Player playerTwo,
                                            final ArrayNode output) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();

        int playerIdx = currentAction.getPlayerIdx();
        objectNode.put("command", "getPlayerMana");
        if (playerIdx == Player.PLAYER_ONE_ID) {
            objectNode.put("playerIdx", Player.PLAYER_ONE_ID);
            objectNode.put("output", playerOne.getMana());
        } else {
            objectNode.put("playerIdx", Player.PLAYER_TWO_ID);
            objectNode.put("output", playerTwo.getMana());
        }
        output.add(objectNode);
    }

    /**
     * Method used to process the case when trying to retrieve the cards from a player's hand.
     * It calls the addCardsInHandToArr method to put the data in the output ArrayNode depending
     * on the playerIdx.
     *
     * @param currentAction - an instance that keeps all the information about the current command
     * @param playerOne - the instance of the first player
     * @param playerTwo - the instance of the second player
     * @param output - the array node that will be sent to the output
     *
     * @see Player#addCardsInHandToArr method for more information about the actual
     * use of the method
     */
    public static void processGetCardsInHand(final ActionsInput currentAction,
                                             final Player playerOne, final Player playerTwo,
                                             final ArrayNode output) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();

        int playerIdx = currentAction.getPlayerIdx();
        ArrayNode cardsInHand;
        if (playerIdx == Player.PLAYER_ONE_ID) {
            cardsInHand = playerOne.addCardsInHandToArr(objectNode, mapper);
        } else {
            cardsInHand = playerTwo.addCardsInHandToArr(objectNode, mapper);
        }
        objectNode.set("output", cardsInHand);
        output.add(objectNode);
    }

    /**
     * Method used to sent all the cards on the table to the output.
     * It calls the addCardsOnTableToArr method to put the data in the output ArrayNode.
     *
     * @param table - the game board where all cards that are placed are stored
     * @param output - the array node that will be sent to the output
     *
     * @see Table#addCardsOnTableToArr method for more information about the actual
     * use of the method
     */
    public static void processGetCardsOnTable(final Table table,
                                              final ArrayNode output) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        ArrayNode cardsOnTable = table.addCardsOnTableToArr(objectNode, mapper);
        objectNode.set("output", cardsOnTable);
        output.add(objectNode);
    }

    /**
     * Method used to sent all the frozen cards on the table to the output.
     * It calls the addFrozenCardsToArr method to put the data in the output ArrayNode.
     *
     * @param table - the game board where all cards that are placed are stored
     * @param output - the array node that will be sent to the output
     *
     * @see Table#addFrozenCardsToArr method for more information about the actual
     * use of the method
     */
    public static void processGetFrozenCardsOnTable(final Table table,
                                              final ArrayNode output) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        ArrayNode cardsOnTable = table.addFrozenCardsToArr(objectNode, mapper);
        objectNode.set("output", cardsOnTable);
        output.add(objectNode);
    }

    /**
     * Method that sends the deck of a player to the output.
     * Depending on the playerIdx, the method calls the addPlayerDeckToArr method
     * from the Player class to put the data in the output ArrayNode for the first or the
     * second player.
     *
     * @param currentAction - an instance that keeps all the information about the current command
     * @param playerOne - the instance of the first player
     * @param playerTwo - the instance of the second player
     * @param output - the array node that will be sent to the output
     *
     * @see Player#addPlayerDeckToArr method for more information about adding the deck
     * to the output
     */
    public static void processGetPlayerDeck(final ActionsInput currentAction,
                                            final Player playerOne, final Player playerTwo,
                                            final ArrayNode output) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();

        int playerIdx = currentAction.getPlayerIdx();
        ArrayNode deck;
        if (playerIdx == Player.PLAYER_ONE_ID) {
            deck = playerOne.addPlayerDeckToArr(objectNode, mapper);
        } else {
            deck = playerTwo.addPlayerDeckToArr(objectNode, mapper);
        }
        objectNode.set("output", deck);
        output.add(objectNode);
    }

    /**
     * Method called to retrieve the hero of a player and send it to the output.
     * Depending on the playerIdx, the method calls the addPlayerHeroToArr method
     * from the Player class to put the data in the output ArrayNode for the first or the
     * second player.
     *
     * @param currentAction - an instance that keeps all the information about the current command
     * @param playerOne - the instance of the first player
     * @param playerTwo - the instance of the second player
     * @param output - the array node that will be sent to the output
     *
     * @see Player#addPlayerHeroToArr method for more information about adding the hero
     * to the output
     */
    public static void processGetPlayerHero(final ActionsInput currentAction,
                                            final Player playerOne, final Player playerTwo,
                                            final ArrayNode output) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();

        int playerIdx = currentAction.getPlayerIdx();
        ObjectNode hero;
        if (playerIdx == Player.PLAYER_ONE_ID) {
            hero = playerOne.addPlayerHeroToArr(objectNode, mapper);
        } else {
            hero = playerTwo.addPlayerHeroToArr(objectNode, mapper);
        }
        objectNode.set("output", hero);
        output.add(objectNode);
    }

    /**
     * Method that puts the current player turn in the output ArrayNode.
     * it initializes an objectMapper and an objectNode, adds the command and the player turn to it
     * (retrieved using the getPlayerTurn getter from the Game class) and then adds
     * the objectNode to the output ArrayNode.
     *
     * @param game - the instance of the current game
     * @param output - the array node that will be sent to the output
     */
    public static void processGetPlayerTurn(final Game game, final ArrayNode output) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();

        objectNode.put("command", "getPlayerTurn");
        objectNode.put("output", game.getPlayerTurn());
        output.add(objectNode);
    }

    /**
     * Method that puts the number of wins of the first player in the output ArrayNode.
     * it initializes an objectMapper and an objectNode, adds the command and the number of wins
     * of the first player to it (retrieved using the getPlayerOneWins getter from the GameStats
     * class) and then adds the objectNode to the output ArrayNode.
     *
     * @param gameStats - the instance of the GameStats class
     * @param output - the array node that will be sent to the output
     */
    public static void processGetPlayerOneWins(final GameStats gameStats, final ArrayNode output) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();

        objectNode.put("command", "getPlayerOneWins");
        objectNode.put("output", gameStats.getPlayerOneWins());
        output.add(objectNode);
    }

    /**
     * Method used to send the number of wins of the playerTwo to the output.
     * It uses the getPlayerTwoWins getter from the GameStats class to retrieve
     * the value needed and then adds it to the output ArrayNode alongside with the command.
     *
     * @param gameStats - the instance of the GameStats class, keeps information
     *                  about the played games
     * @param output - the array node that will be sent to the output
     */
    public static void processGetPlayerTwoWins(final GameStats gameStats, final ArrayNode output) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();

        objectNode.put("command", "getPlayerTwoWins");
        objectNode.put("output", gameStats.getPlayerTwoWins());
        output.add(objectNode);
    }

    /**
     * Method used to send the total number of games played by the two players to the output.
     * It makes use of the getTotalGamesPlayed getter from the GameStats class to retrieve
     * the value of the field and then adds it to the output ArrayNode alongside with the command.
     *
     * @param gameStats - the instance of the GameStats class, keeps track of all the games played
     *                  and all stats about them
     * @param output - the array node that will be sent to the output
     */
    public static void processGetTotalGamesPlayed(final GameStats gameStats,
                                                  final ArrayNode output) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();

        objectNode.put("command", "getTotalGamesPlayed");
        objectNode.put("output", gameStats.getTotalGamesPlayed());
        output.add(objectNode);
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(final String command) {
        this.command = command;
    }
}
