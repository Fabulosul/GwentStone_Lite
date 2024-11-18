package org.poo.main;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.fileio.ActionsInput;


public final class CommandHandler {
    /**
     * Private constructor to prevent the initialization of the default constructor.
     */
    private CommandHandler() { }

    /**
     * Method used to handle the commands from the input.
     * It uses a switch case to determine the command name and then calls the appropriate
     * processing method from the Command class to solve the action.
     *
     * @param currentAction - an instance that contains the all the information needed about the
     *                      current command
     * @param game - an instance of the current game
     * @param playerOne - an instance of the first player
     * @param playerTwo - an instance of the second player
     * @param table - an instance of the table
     * @param gameStats - an instance that has all the information about the games played
     * @param output - an ArrayNode that is used to send data to the output
     */
    public static void handleCommands(final ActionsInput currentAction, final Game game,
                                      final Player playerOne, final Player playerTwo,
                                      final Table table, final GameStats gameStats,
                                      final ArrayNode output) {

            Command command = new Command(currentAction.getCommand());

            switch (command.getCommand()) {
                case "endPlayerTurn":
                    Command.endPlayerTurn(game, playerOne, playerTwo, table, gameStats);
                    break;

                case "placeCard":
                    Command.processPlaceCard(currentAction, game, playerOne,
                            playerTwo, table, output);
                    break;

                case "cardUsesAttack":
                    Command.processCardUsesAttack(currentAction, table, output);
                    break;

                case "cardUsesAbility":
                    Command.processCardUsesAbility(currentAction, table, output);
                    break;

                case "useAttackHero":
                    Command.processUseAttackHero(currentAction, table, playerOne, playerTwo,
                            gameStats, output);
                    break;

                case "useHeroAbility":
                    Command.processUseHeroAbility(currentAction, game, playerOne, playerTwo,
                            table, output);
                    break;

                case "getCardAtPosition":
                    Command.processGetCardAtPosition(currentAction, table, output);
                    break;

                case "getPlayerMana":
                    Command.processGetPlayerMana(currentAction, playerOne, playerTwo, output);
                    break;

                case "getCardsInHand":
                    Command.processGetCardsInHand(currentAction, playerOne, playerTwo, output);
                    break;

                case "getCardsOnTable":
                    Command.processGetCardsOnTable(table, output);
                    break;

                case "getFrozenCardsOnTable":
                    Command.processGetFrozenCardsOnTable(table, output);
                    break;

                case "getPlayerDeck":
                    Command.processGetPlayerDeck(currentAction, playerOne, playerTwo, output);
                    break;

                case "getPlayerHero":
                    Command.processGetPlayerHero(currentAction, playerOne, playerTwo, output);
                    break;

                case "getPlayerTurn":
                    Command.processGetPlayerTurn(game, output);
                    break;

                case "getPlayerOneWins":
                    Command.processGetPlayerOneWins(gameStats, output);
                    break;

                case "getPlayerTwoWins":
                    Command.processGetPlayerTwoWins(gameStats, output);
                    break;

                case "getTotalGamesPlayed":
                    Command.processGetTotalGamesPlayed(gameStats, output);
                    break;

                default:
                    System.out.println("Invalid command");
                    break;
            }
        }
}

