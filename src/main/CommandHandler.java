//package main;
//
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.node.ArrayNode;
//import com.fasterxml.jackson.databind.node.ObjectNode;
//import fileio.*;
//
//import java.util.ArrayList;
//
//public final class CommandHandler {
//
//    private CommandHandler() {
//    }
//
//    public static void handleCommands(final Input inputData, final ArrayNode output) {
//        GameStats gameStats = new GameStats();
//
//        int nrGames = inputData.getGames().size();
//        for (int i = 0; i < nrGames; i++) {
//            GameInput currentGame = inputData.getGames().get(i);
//
//            int playerOneDeckIdx = currentGame.getStartGame().getPlayerOneDeckIdx();
//            int playerTwoDeckIdx = currentGame.getStartGame().getPlayerTwoDeckIdx();
//            int shuffleSeed = currentGame.getStartGame().getShuffleSeed();
//            int startingPlayer = currentGame.getStartGame().getStartingPlayer();
//
//            ArrayList<CardInput> playerOneDeck = DecksInput.chooseDeck(playerOneDeckIdx,
//                    inputData.getPlayerOneDecks().getDecks());
//            ArrayList<CardInput> playerTwoDeck = DecksInput.chooseDeck(playerTwoDeckIdx,
//                    inputData.getPlayerTwoDecks().getDecks());
//
//            ArrayList<CardInput> playerOneShuffledDeck = DecksInput.shuffleDeck(playerOneDeck, shuffleSeed);
//            ArrayList<CardInput> playerTwoShuffledDeck = DecksInput.shuffleDeck(playerTwoDeck, shuffleSeed);
//
//            CardInput playerOneHero = new CardInput(currentGame.getStartGame().getPlayerOneHero());
//            CardInput playerTwoHero = new CardInput(currentGame.getStartGame().getPlayerTwoHero());
//
//            Player playerOne = new Player(Player.PLAYER_ONE_ID, playerOneHero, playerOneShuffledDeck);
//            Player playerTwo = new Player(Player.PLAYER_TWO_ID, playerTwoHero, playerTwoShuffledDeck);
//
//            playerOne.getHero().setHealth(CardInput.INITIAL_HERO_HEALTH);
//            playerTwo.getHero().setHealth(CardInput.INITIAL_HERO_HEALTH);
//
//            playerOne.drawCard();
//            playerTwo.drawCard();
//
//            Game game = new Game(startingPlayer);
//
//            Table table = new Table();
//
//            gameStats.setGameOver(false);
//
//            int nrActions = inputData.getGames().get(i).getActions().size();
//
//            for (int j = 0; j < nrActions; j++) {
//                ActionsInput currentAction = inputData.getGames().get(i).getActions().get(j);
//
//                Command command = new Command(currentAction.getCommand());
//
//                ObjectMapper mapper = new ObjectMapper();
//                ObjectNode objectNode = mapper.createObjectNode();
//
//                switch (command.getCommand()) {
//                    case "endPlayerTurn":
//                        command.endPlayerTurn(game, playerOne, playerTwo, table, gameStats);
//                        break;
//
//                    case "placeCard":
//                        int handIdx = inputData.getGames().get(i).getActions().get(j).getHandIdx();
//                        boolean canPlaceCard;
//                        if (game.getPlayerTurn() == 1) {
//                            canPlaceCard = playerOne.placeCard(objectNode, mapper, handIdx, table);
//                        } else {
//                            canPlaceCard = playerTwo.placeCard(objectNode, mapper, handIdx, table);
//                        }
//                        if (!canPlaceCard) {
//                            output.add(objectNode);
//                        }
//                        break;
//
//                    case "cardAttacksCard":
//                        Coordinates cardAttackerCoordinates = inputData.getGames().get(i).getActions().get(j).getCardAttacker();
//                        Coordinates cardAttackedCoordinates = inputData.getGames().get(i).getActions().get(j).getCardAttacked();
//
//                        CardInput cardAttacker = table.getTableCards().get(cardAttackerCoordinates.getX()).get(cardAttackerCoordinates.getY());
//                        CardInput cardAttacked = table.getTableCards().get(cardAttackedCoordinates.getX()).get(cardAttackedCoordinates.getY());
//
//                        boolean hasAttacked = cardAttacker.cardUsesAttack(cardAttacked, cardAttackerCoordinates, cardAttackedCoordinates, table, objectNode, mapper);
//
//                        if (!hasAttacked) {
//                            output.add(objectNode);
//                        }
//                        break;
//
//                    case "cardAttacksHero":
//                        Coordinates cardAttackerCoordinates = inputData.getGames().get(i).getActions().get(j).getCardAttacker();
//                        Coordinates cardAttackedCoordinates = inputData.getGames().get(i).getActions().get(j).getCardAttacked();
//
//                        CardInput cardAttacker = table.getTableCards().get(cardAttackerCoordinates.getX()).get(cardAttackerCoordinates.getY());
//                        CardInput cardAttacked = table.getTableCards().get(cardAttackedCoordinates.getX()).get(cardAttackedCoordinates.getY());
//
//                        boolean hasUsedAbility = cardAttacker.cardUsesAbility(cardAttacked, cardAttackerCoordinates, cardAttackedCoordinates,
//                                table, objectNode, mapper);
//
//                        if (!hasUsedAbility) {
//                            output.add(objectNode);
//                        }
//                        break;
//
//                    case "useHeroAbility":
//                        Coordinates cardAttackerCoordinates = inputData.getGames().get(i).getActions().get(j).getCardAttacker();
//                        CardInput cardAttacker = table.getTableCards().get(cardAttackerCoordinates.getX()).get(cardAttackerCoordinates.getY());
//                        boolean hasAttackedHero;
//                        if (Player.getPlayerByRow(cardAttackerCoordinates.getX()) == 1) {
//                            hasAttackedHero = cardAttacker.useAttackHero(cardAttackerCoordinates, playerTwo.getHero(), table,
//                                    objectNode, mapper);
//                        } else {
//                            hasAttackedHero = cardAttacker.useAttackHero(cardAttackerCoordinates, playerOne.getHero(), table,
//                                    objectNode, mapper);
//                        }
//                        if (!hasAttackedHero) {
//                            output.add(objectNode);
//                        }
//                        if (playerOne.getHero().getHealth() == 0 && !gameStats.isGameOver()) {
//                            output.add(objectNode);
//                            gameStats.setPlayerTwoWins(gameStats.getPlayerTwoWins() + 1);
//                            gameStats.setTotalGamesPlayed(gameStats.getTotalGamesPlayed() + 1);
//                            gameStats.setGameOver(true);
//                        }
//                        if (playerTwo.getHero().getHealth() == 0 && !gameStats.isGameOver()) {
//                            output.add(objectNode);
//                            gameStats.setPlayerOneWins(gameStats.getPlayerOneWins() + 1);
//                            gameStats.setTotalGamesPlayed(gameStats.getTotalGamesPlayed() + 1);
//                            gameStats.setGameOver(true);
//                        }
//                        break;
//
//                    case "getCardAtPosition":
//                        int affectedRow = inputData.getGames().get(i).getActions().get(j).getAffectedRow();
//                        int currentPlayerTurn = game.getPlayerTurn();
//                        boolean heroUsedAbility;
//                        if (currentPlayerTurn == 1) {
//                            heroUsedAbility = playerOne.getHero().useHeroAbility(affectedRow, currentPlayerTurn,
//                                    playerOne, table, objectNode, mapper);
//                        } else {
//                            heroUsedAbility = playerTwo.getHero().useHeroAbility(affectedRow, currentPlayerTurn,
//                                    playerTwo, table, objectNode, mapper);
//                        }
//                        if (!heroUsedAbility) {
//                            output.add(objectNode);
//                        }
//                        break;
//
//                    case "getCardAtPosition":
//                        int cardRow = inputData.getGames().get(i).getActions().get(j).getX();
//                        int cardColumn = inputData.getGames().get(i).getActions().get(j).getY();
//
//                        table.getCardAtPosition(table, cardRow, cardColumn, objectNode, mapper);
//                        output.add(objectNode);
//                        break;
//
//                    case "getPlayerMana":
//                        int playerIdx = inputData.getGames().get(i).getActions().get(j).getPlayerIdx();
//                        objectNode.put("command", "getPlayerMana");
//                        if (playerIdx == 1) {
//                            objectNode.put("playerIdx", 1);
//                            objectNode.put("output", playerOne.getMana());
//                        } else {
//                            objectNode.put("playerIdx", 2);
//                            objectNode.put("output", playerTwo.getMana());
//                        }
//                        output.add(objectNode);
//                        break;
//
//                    case "getCardsInHand":
//                        int playerIdx = inputData.getGames().get(i).getActions().get(j).getPlayerIdx();
//                        ArrayNode cardsInHand;
//                        if (playerIdx == 1) {
//                            cardsInHand = command.getCardsInHand(objectNode, mapper, playerOne);
//                        } else {
//                            cardsInHand = command.getCardsInHand(objectNode, mapper, playerTwo);
//                        }
//                        objectNode.set("output", cardsInHand);
//                        output.add(objectNode);
//                        break;
//
//                    case "getCardsOnTable":
//                        ArrayNode cardsOnTable = table.getCardsOnTable(objectNode, mapper);
//                        objectNode.set("output", cardsOnTable);
//                        output.add(objectNode);
//                        break;
//
//                    case "getFrozenCardsOnTable":
//                        ArrayNode frozenCardsOnTable = table.getFrozenCardsOnTable(objectNode, mapper);
//                        objectNode.set("output", frozenCardsOnTable);
//                        output.add(objectNode);
//                        break;
//
//                    case "getPlayerDeck":
//                        int playerIdx = inputData.getGames().get(i).getActions().get(j).getPlayerIdx();
//                        ArrayNode deck;
//                        if (playerIdx == 1) {
//                            deck = command.getPlayerDeck(objectNode, mapper, playerOne);
//                        } else {
//                            deck = command.getPlayerDeck(objectNode, mapper, playerTwo);
//                        }
//                        objectNode.set("output", deck);
//                        output.add(objectNode);
//                        break;
//
//                    case "getPlayerHero":
//                        int playerIdx = inputData.getGames().get(i).getActions().get(j).getPlayerIdx();
//                        ObjectNode hero;
//                        if (playerIdx == 1) {
//                            hero = command.getPlayerHero(objectNode, mapper, playerOne);
//                        } else {
//                            hero = command.getPlayerHero(objectNode, mapper, playerTwo);
//                        }
//                        objectNode.set("output", hero);
//                        output.add(objectNode);
//                        break;
//
//                    case "getPlayerTurn":
//                        objectNode.put("command", "getPlayerTurn");
//                        objectNode.put("output", command.getPlayerTurn(game));
//                        output.add(objectNode);
//                        break;
//
//                    case "getPlayerOneWins":
//                        objectNode.put("command", "getPlayerOneWins");
//                        objectNode.put("output", command.getPlayerOneWins(gameStats));
//                        output.add(objectNode);
//                        break;
//
//                    case "getPlayerTwoWins":
//                        objectNode.put("command", "getPlayerTwoWins");
//                        objectNode.put("output", command.getPlayerTwoWins(gameStats));
//                        output.add(objectNode);
//                        break;
//
//                    case "getTotalGamesPlayed":
//                        objectNode.put("command", "getTotalGamesPlayed");
//                        objectNode.put("output", command.getTotalGamesPlayed(gameStats));
//                        output.add(objectNode);
//                        break;
//                }
//            }
//        }
//    }
//
//
//
//}
