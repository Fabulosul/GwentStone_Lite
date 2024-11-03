package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.ActionsInput;
import fileio.CardInput;
import fileio.Coordinates;

import java.util.ArrayList;


public final class Command {
    private String command;

    public Command(final String command) {
        this.command = command;
    }

    /**
     * Method that makes the changes needed at the end of a player's turn.
     *
     *
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
                    CardInput currentCard = table.getTableCards().get(i).get(j);
                    if (currentCard.isFrozen()) {
                        currentCard.setIsFrozen(false);
                    }
                }
            }
        } else {
            for (int i = Table.PLAYER_TWO_BACK_ROW; i <= Table.PLAYER_TWO_FRONT_ROW; i++) {
                for (int j = 0; j < table.getTableCards().get(i).size(); j++) {
                    CardInput currentCard = table.getTableCards().get(i).get(j);
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
                playerOne.getHero().setHasAttacked(false);
                playerTwo.getHero().setHasAttacked(false);
                table.resetCardProperties();
            }
        }
    }

    public static void processPlaceCard(final ActionsInput currentAction, final Game game,
                                        final Player playerOne, final Player playerTwo,
                                        final Table table, final ArrayNode output) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();

        boolean canPlaceCard;
        if (game.getPlayerTurn() == Player.PLAYER_ONE_ID) {
            canPlaceCard = playerOne.placeCard(currentAction, objectNode, mapper, table);
        } else {
            canPlaceCard = playerTwo.placeCard(currentAction, objectNode, mapper, table);
        }
        if (!canPlaceCard) {
            output.add(objectNode);
        }
    }

    public static void processCardUsesAttack(final ActionsInput currentAction, final Table table,
                                             final ArrayNode output) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();

        Coordinates cardAttackerCoordinates = currentAction.getCardAttacker();
        Coordinates cardAttackedCoordinates = currentAction.getCardAttacked();

        ArrayList<CardInput> cardAttackerRow
                = table.getTableCards().get(cardAttackerCoordinates.getX());
        CardInput cardAttacker = cardAttackerRow.get(cardAttackerCoordinates.getY());

        ArrayList<CardInput> cardAttackedRow
                = table.getTableCards().get(cardAttackedCoordinates.getX());
        CardInput cardAttacked = cardAttackedRow.get(cardAttackedCoordinates.getY());

        boolean hasAttacked = cardAttacker.cardUsesAttack(cardAttacked, cardAttackerCoordinates,
                cardAttackedCoordinates, table, objectNode, mapper);

        if (!hasAttacked) {
            output.add(objectNode);
        }
    }

    public static void processCardUsesAbility(final ActionsInput currentAction, final Table table,
                                              final ArrayNode output) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();

        Coordinates cardAttackerCoordinates = currentAction.getCardAttacker();
        Coordinates cardAttackedCoordinates = currentAction.getCardAttacked();

        ArrayList<CardInput> cardAttackerRow
                = table.getTableCards().get(cardAttackerCoordinates.getX());
        CardInput cardAttacker = cardAttackerRow.get(cardAttackerCoordinates.getY());

        ArrayList<CardInput> cardAttackedRow
                = table.getTableCards().get(cardAttackedCoordinates.getX());
        CardInput cardAttacked = cardAttackedRow.get(cardAttackedCoordinates.getY());

        boolean hasUsedAbility = cardAttacker.cardUsesAbility(cardAttacked,
                cardAttackerCoordinates, cardAttackedCoordinates,
                table, objectNode, mapper);

        if (!hasUsedAbility) {
            output.add(objectNode);
        }
    }

    public static void processUseAttackHero(final ActionsInput currentAction, final Table table,
                                     final Player playerOne, final Player playerTwo,
                                     final GameStats gameStats,
                                     final ArrayNode output) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();

        Coordinates cardAttackerCoordinates = currentAction.getCardAttacker();

        ArrayList<CardInput> cardAttackerRow
                = table.getTableCards().get(cardAttackerCoordinates.getX());
        CardInput cardAttacker = cardAttackerRow.get(cardAttackerCoordinates.getY());

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

    public static void processGetCardAtPosition(final ActionsInput currentAction, final Table table,
                                                final ArrayNode output) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();

        int cardRow = currentAction.getX();
        int cardColumn = currentAction.getY();

        table.addCardAtPosToArr(table, cardRow, cardColumn, objectNode, mapper);
        output.add(objectNode);
    }

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

    public static void processGetCardsOnTable(final Table table,
                                              final ArrayNode output) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        ArrayNode cardsOnTable = table.addCardsOnTableToArr(objectNode, mapper);
        objectNode.set("output", cardsOnTable);
        output.add(objectNode);
    }

    public static void processGetFrozenCardsOnTable(final Table table,
                                              final ArrayNode output) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        ArrayNode cardsOnTable = table.addFrozenCardsToArr(objectNode, mapper);
        objectNode.set("output", cardsOnTable);
        output.add(objectNode);
    }

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

    public static void processGetPlayerTurn(final Game game, final ArrayNode output) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();

        objectNode.put("command", "getPlayerTurn");
        objectNode.put("output", game.getPlayerTurn());
        output.add(objectNode);
    }

    public static void processGetPlayerOneWins(final GameStats gameStats, final ArrayNode output) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();

        objectNode.put("command", "getPlayerOneWins");
        objectNode.put("output", gameStats.getPlayerOneWins());
        output.add(objectNode);
    }

    public static void processGetPlayerTwoWins(final GameStats gameStats, final ArrayNode output) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();

        objectNode.put("command", "getPlayerTwoWins");
        objectNode.put("output", gameStats.getPlayerTwoWins());
        output.add(objectNode);
    }

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
