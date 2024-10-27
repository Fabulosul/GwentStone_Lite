package main;

import checker.Checker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import checker.CheckerConstants;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.CardInput;
import fileio.Input;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;

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

        for(int i = 0; i < inputData.getGames().size(); i++) {
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

           Player player1 = new Player(1, startingPlayer, playerOneShuffledDeck);
           Player player2 = new Player(2, startingPlayer, playerTwoShuffledDeck);

           player1.drawCard();
           player2.drawCard();

           for(int j = 0; j < inputData.getGames().get(i).getActions().size(); j++) {
               Command command = new Command(inputData.getGames().get(i).getActions().get(j).getCommand());
               ObjectMapper mapper = new ObjectMapper();
               ObjectNode objectNode = mapper.createObjectNode();
               objectNode.put("command", command.getCommand());

                if(command.getCommand().equals("getPlayerDeck")) {
                    int playerIdx = inputData.getGames().get(i).getActions().get(j).getPlayerIdx();
                    ArrayNode deck;
                    if(playerIdx == 1) {
                         deck = command.getPlayerDeck(objectNode, mapper, player1);
                    } else {
                        deck = command.getPlayerDeck(objectNode, mapper, player2);
                    }
                    objectNode.set("output", deck);
                }
                if(command.equals("getPlayerHero")) {

                    ObjectNode heroNode = objectMapper.createObjectNode();
                    if(inputData.getGames().get(i).getActions().get(j).getPlayerIdx() == 1) {
                        heroNode.put("mana", inputData.getGames().get(i).getStartGame().getPlayerOneHero().getMana());
                        heroNode.put("description", inputData.getGames().get(i).getStartGame().getPlayerOneHero().getDescription());
                        ArrayNode colorsArray = objectMapper.createArrayNode();
                        for (String color : inputData.getGames().get(i).getStartGame().getPlayerOneHero().getColors()) {
                            colorsArray.add(color);
                        }
                        heroNode.set("colors", colorsArray);
                        heroNode.put("name", inputData.getGames().get(i).getStartGame().getPlayerOneHero().getName());
                        heroNode.put("health", inputData.getGames().get(i).getStartGame().getPlayerOneHero().getHealth());
                    }
                    if(inputData.getGames().get(i).getActions().get(j).getPlayerIdx() == 2) {
                        heroNode.put("mana", inputData.getGames().get(i).getStartGame().getPlayerTwoHero().getMana());
                        heroNode.put("description", inputData.getGames().get(i).getStartGame().getPlayerTwoHero().getDescription());
                        ArrayNode colorsArray = objectMapper.createArrayNode();
                        for (String color : inputData.getGames().get(i).getStartGame().getPlayerTwoHero().getColors()) {
                            colorsArray.add(color);
                        }
                        heroNode.set("colors", colorsArray);
                        heroNode.put("name", inputData.getGames().get(i).getStartGame().getPlayerTwoHero().getName());
                        heroNode.put("health", inputData.getGames().get(i).getStartGame().getPlayerTwoHero().getHealth());
                    }
                    objectNode.set("output", heroNode);
                }
                if(command.equals("getPlayerTurn")) {
                    objectNode.put("output", inputData.getGames().get(i).getStartGame().getStartingPlayer());
                }
                output.add(objectNode);
            }
        }



        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        objectWriter.writeValue(new File(filePath2), output);
    }
}
