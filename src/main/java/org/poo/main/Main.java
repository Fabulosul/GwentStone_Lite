package org.poo.main;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;

import org.poo.checker.Checker;
import org.poo.checker.CheckerConstants;
import org.poo.fileio.ActionsInput;
import org.poo.fileio.CardInput;
import org.poo.fileio.DecksInput;
import org.poo.fileio.Input;
import org.poo.main.cards.Card;
import org.poo.main.cards.HeroCard;

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

        GameStats gameStats = new GameStats();

        int nrGames = inputData.getGames().size();
        for (int i = 0; i < nrGames; i++) {
           int playerOneDeckIdx = inputData.getGames().get(i).getStartGame().getPlayerOneDeckIdx();
           int playerTwoDeckIdx = inputData.getGames().get(i).getStartGame().getPlayerTwoDeckIdx();
           int shuffleSeed = inputData.getGames().get(i).getStartGame().getShuffleSeed();
           int startingPlayer = inputData.getGames().get(i).getStartGame().getStartingPlayer();

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

           Player playerOne = new Player(1, playerOneHero, playerOneShuffledDeck);
           Player playerTwo = new Player(2, playerTwoHero, playerTwoShuffledDeck);

           playerOne.drawCard();
           playerTwo.drawCard();

           Game game = new Game(startingPlayer);

           Table table = new Table();

           gameStats.setGameOver(false);

           int nrActions = inputData.getGames().get(i).getActions().size();
           for (int j = 0; j < nrActions; j++) {
                ActionsInput currentAction = inputData.getGames().get(i).getActions().get(j);
                CommandHandler.handleCommands(currentAction, game, playerOne, playerTwo, table,
                        gameStats, output);
           }
        }

        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        objectWriter.writeValue(new File(filePath2), output);
    }
}
