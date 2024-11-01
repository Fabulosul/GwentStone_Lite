package main;

import checker.Checker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import checker.CheckerConstants;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.CardInput;
import fileio.Coordinates;
import fileio.Input;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.XMLFormatter;


/**
 * The entry point to this homework. It runs the checker that tests your implentation.
 */
public final class Main {
    /**
     * for coding style
     */
    private Main() {
    }

    /**
     * DO NOT MODIFY MAIN METHOD
     * Call the checker
     * @param args from command line
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void main(final String[] args) throws IOException {
        File directory = new File(CheckerConstants.TESTS_PATH);
        Path path = Paths.get(CheckerConstants.RESULT_PATH);

        if (Files.exists(path)) {
            File resultFile = new File(String.valueOf(path));
            for (File file : Objects.requireNonNull(resultFile.listFiles())) {
                file.delete();
            }
            resultFile.delete();
        }
        Files.createDirectories(path);

        for (File file : Objects.requireNonNull(directory.listFiles())) {
            String filepath = CheckerConstants.OUT_PATH + file.getName();
            File out = new File(filepath);
            boolean isCreated = out.createNewFile();
            if (isCreated) {
                action(file.getName(), filepath);
            }
        }

        Checker.calculateScore();
    }

    /**
     * @param filePath1 for input file
     * @param filePath2 for output file
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void action(final String filePath1,
                              final String filePath2) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Input inputData = objectMapper.readValue(new File(CheckerConstants.TESTS_PATH + filePath1),
                Input.class);

        ArrayNode output = objectMapper.createArrayNode();

        /*
         * TODO Implement your function here
         *
         * How to add output to the output array?
         * There are multiple ways to do this, here is one example:
         *
         * ObjectMapper mapper = new ObjectMapper();
         *
         * ObjectNode objectNode = mapper.createObjectNode();
         * objectNode.put("field_name", "field_value");
         *
         * ArrayNode arrayNode = mapper.createArrayNode();
         * arrayNode.add(objectNode);
         *
         * output.add(arrayNode);
         * output.add(objectNode);
         *
         */
        int nrGames = inputData.getGames().size();
        for(int i = 0; i < nrGames; i++) {
           int playerOneDeckIdx = inputData.getGames().get(i).getStartGame().getPlayerOneDeckIdx();
           int playerTwoDeckIdx = inputData.getGames().get(i).getStartGame().getPlayerTwoDeckIdx();
           int shuffleSeed = inputData.getGames().get(i).getStartGame().getShuffleSeed();
           int startingPlayer = inputData.getGames().get(i).getStartGame().getStartingPlayer();

           ArrayList<CardInput> playerOneDeck = Utility.chooseDeck(playerOneDeckIdx,
                   inputData.getPlayerOneDecks().getDecks());
           ArrayList<CardInput> playerTwoDeck = Utility.chooseDeck(playerTwoDeckIdx,
                   inputData.getPlayerTwoDecks().getDecks());

           ArrayList<CardInput> playerOneShuffledDeck = Utility.shuffleDeck(playerOneDeck, shuffleSeed);
           ArrayList<CardInput> playerTwoShuffledDeck = Utility.shuffleDeck(playerTwoDeck, shuffleSeed);

           CardInput playerOneHero = inputData.getGames().get(i).getStartGame().getPlayerOneHero();
           CardInput playerTwoHero = inputData.getGames().get(i).getStartGame().getPlayerTwoHero();

           Player playerOne = new Player(1, playerOneHero, playerOneShuffledDeck);
           Player playerTwo = new Player(2, playerTwoHero, playerTwoShuffledDeck);

           playerOne.hero.setHealth(30);
           playerTwo.hero.setHealth(30);

           playerOne.drawCard();
           playerTwo.drawCard();

           Game game = new Game(startingPlayer);

           Table table = new Table();

           int nrActions = inputData.getGames().get(i).getActions().size();
           for(int j = 0; j < nrActions; j++) {
                Command command = new Command(inputData.getGames().get(i).getActions().get(j).getCommand());
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode objectNode = mapper.createObjectNode();

                if(command.getCommand().equals("endPlayerTurn")) {
                    command.endPlayerTurn(game, playerOne, playerTwo, table);
                }
                if(command.getCommand().equals("placeCard")) {
                    int handIdx = inputData.getGames().get(i).getActions().get(j).getHandIdx();
                    boolean canPlaceCard;
                    if(game.getPlayerTurn() == 1) {
                        canPlaceCard = playerOne.placeCard(objectNode, mapper, handIdx, table);
                    } else {
                        canPlaceCard = playerTwo.placeCard(objectNode, mapper, handIdx, table);
                    }
                    if(!canPlaceCard) {
                        output.add(objectNode);
                    }
                }
                if(command.getCommand().equals("cardUsesAttack")) {
                    Coordinates cardAttackerCoordinates = inputData.getGames().get(i).getActions().get(j).getCardAttacker();
                    Coordinates cardAttackedCoordinates = inputData.getGames().get(i).getActions().get(j).getCardAttacked();

                    CardInput cardAttacker = table.getTableCards().get(cardAttackerCoordinates.getX()).get(cardAttackerCoordinates.getY());
                    CardInput cardAttacked = table.getTableCards().get(cardAttackedCoordinates.getX()).get(cardAttackedCoordinates.getY());

                  boolean hasAttacked = cardAttacker.cardUsesAttack(cardAttacked, cardAttackerCoordinates, cardAttackedCoordinates, table, objectNode, mapper);

                    if(!hasAttacked) {
                        output.add(objectNode);
                    }
                }
                if(command.getCommand().equals("cardUsesAbility")) {
                    Coordinates cardAttackerCoordinates = inputData.getGames().get(i).getActions().get(j).getCardAttacker();
                    Coordinates cardAttackedCoordinates = inputData.getGames().get(i).getActions().get(j).getCardAttacked();

                    CardInput cardAttacker = table.getTableCards().get(cardAttackerCoordinates.getX()).get(cardAttackerCoordinates.getY());
                    CardInput cardAttacked = table.getTableCards().get(cardAttackedCoordinates.getX()).get(cardAttackedCoordinates.getY());

                    boolean hasUsedAbility = cardAttacker.cardUsesAbility(cardAttacked, cardAttackerCoordinates, cardAttackedCoordinates,
                            table, objectNode, mapper);

                    if(!hasUsedAbility) {
                        output.add(objectNode);
                    }

                }
                if(command.getCommand().equals("useAttackHero")) {
                    Coordinates cardAttackerCoordinates = inputData.getGames().get(i).getActions().get(j).getCardAttacker();
                    CardInput cardAttacker = table.getTableCards().get(cardAttackerCoordinates.getX()).get(cardAttackerCoordinates.getY());
                    boolean hasAttackedHero;
                    if(Utility.getPlayerId(cardAttackerCoordinates.getX()) == 1) {
                        hasAttackedHero = cardAttacker.useAttackHero(cardAttackerCoordinates, playerTwoHero, table,
                                objectNode, mapper);
                        if(!hasAttackedHero || playerTwoHero.getHealth() == 0) {
                            output.add(objectNode);
                        }
                    } else {
                        hasAttackedHero = cardAttacker.useAttackHero(cardAttackerCoordinates, playerOneHero, table,
                                objectNode, mapper);
                        if(!hasAttackedHero || playerOneHero.getHealth() == 0) {
                            output.add(objectNode);
                        }
                    }
                }
                if(command.getCommand().equals("useHeroAbility")) {
                    int affectedRow = inputData.getGames().get(i).getActions().get(j).getAffectedRow();
                    int currentPlayerTurn = game.getPlayerTurn();
                    boolean heroUsedAbility;
                    if(currentPlayerTurn == 1) {
                        heroUsedAbility = playerOne.hero.useHeroAbility(affectedRow, currentPlayerTurn,
                                playerOne, table, objectNode, mapper);
                    } else {
                        heroUsedAbility = playerTwo.hero.useHeroAbility(affectedRow, currentPlayerTurn,
                                playerTwo, table, objectNode, mapper);
                    }
                    if(!heroUsedAbility) {
                        output.add(objectNode);
                    }
                }
                if(command.getCommand().equals("getCardAtPosition")) {
                    int cardRow = inputData.getGames().get(i).getActions().get(j).getX();
                    int cardColumn = inputData.getGames().get(i).getActions().get(j).getY();

                    table.getCardAtPosition(table, cardRow, cardColumn, objectNode, mapper);
                    output.add(objectNode);
                }
                if(command.getCommand().equals("getPlayerMana")) {
                    int playerIdx = inputData.getGames().get(i).getActions().get(j).getPlayerIdx();
                    objectNode.put("command", "getPlayerMana");
                    if(playerIdx == 1) {
                        objectNode.put("playerIdx", 1);
                        objectNode.put("output", playerOne.getMana());
                    } else {
                        objectNode.put("playerIdx", 2);
                        objectNode.put("output", playerTwo.getMana());
                    }
                    output.add(objectNode);
                }
                if(command.getCommand().equals("getCardsInHand")) {
                    int playerIdx = inputData.getGames().get(i).getActions().get(j).getPlayerIdx();
                    ArrayNode cardsInHand;
                    if(playerIdx == 1) {
                        cardsInHand = command.getCardsInHand(objectNode, mapper, playerOne);
                    } else {
                        cardsInHand = command.getCardsInHand(objectNode, mapper, playerTwo);
                    }
                    objectNode.set("output", cardsInHand);
                    output.add(objectNode);
                }
                if(command.getCommand().equals("getCardsOnTable")){
                    ArrayNode cardsOnTable = table.getCardsOnTable(objectNode, mapper);
                    objectNode.set("output", cardsOnTable);
                    output.add(objectNode);
                }
               if(command.getCommand().equals("getFrozenCardsOnTable")){
                   ArrayNode frozenCardsOnTable = table.getFrozenCardsOnTable(objectNode, mapper);
                   objectNode.set("output", frozenCardsOnTable);
                   output.add(objectNode);
               }
                if(command.getCommand().equals("getPlayerDeck")) {
                    int playerIdx = inputData.getGames().get(i).getActions().get(j).getPlayerIdx();
                    ArrayNode deck;
                    if(playerIdx == 1) {
                         deck = command.getPlayerDeck(objectNode, mapper, playerOne);
                    } else {
                        deck = command.getPlayerDeck(objectNode, mapper, playerTwo);
                    }
                    objectNode.set("output", deck);
                    output.add(objectNode);
                }
                if(command.getCommand().equals("getPlayerHero")) {
                    int playerIdx = inputData.getGames().get(i).getActions().get(j).getPlayerIdx();
                    ObjectNode hero;
                    if(playerIdx == 1) {
                        hero = command.getPlayerHero(objectNode, mapper, playerOne);
                    } else {
                        hero = command.getPlayerHero(objectNode, mapper, playerTwo);
                    }
                    objectNode.set("output", hero);
                    output.add(objectNode);
                }
                if(command.getCommand().equals("getPlayerTurn")) {
                    objectNode.put("command", "getPlayerTurn");
                    objectNode.put("output", command.getPlayerTurn(game));
                    output.add(objectNode);
                }
            }
           System.out.println("Game " + (i + 1) + " ended");
        }

        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        objectWriter.writeValue(new File(filePath2), output);
    }
}
