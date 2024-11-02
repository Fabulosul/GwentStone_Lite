package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.CardInput;

public final class Command {
    private String command;

    public Command(final String command) {
        this.command = command;
    }

    ArrayNode getPlayerDeck(final ObjectNode objectNode, final ObjectMapper mapper,
                            final Player player) {
        objectNode.put("command", "getPlayerDeck");
        objectNode.put("playerIdx", player.getId());
        ArrayNode deck = mapper.createArrayNode();
        for (int i = 0; i < player.getDeck().size(); i++) {
            ObjectNode card = mapper.createObjectNode();
            card.put("mana", player.getDeck().get(i).getMana());
            card.put("attackDamage", player.getDeck().get(i).getAttackDamage());
            card.put("health", player.getDeck().get(i).getHealth());
            card.put("description", player.getDeck().get(i).getDescription());
            ArrayNode colors = mapper.createArrayNode();
            for (int j = 0; j < player.getDeck().get(i).getColors().size(); j++) {
                colors.add(player.getDeck().get(i).getColors().get(j));
            }
            card.set("colors", colors);
            card.put("name", player.getDeck().get(i).getName());
            deck.add(card);
        }
        return deck;
    }

    ObjectNode getPlayerHero(final ObjectNode objectNode, final ObjectMapper mapper,
                             final Player player) {
        objectNode.put("command", "getPlayerHero");
        objectNode.put("playerIdx", player.getId());
        ObjectNode hero = mapper.createObjectNode();
        hero.put("mana", player.getHero().getMana());
        hero.put("description", player.getHero().getDescription());
        ArrayNode colors = mapper.createArrayNode();
        for (int j = 0; j < player.getHero().getColors().size(); j++) {
            colors.add(player.getHero().getColors().get(j));
        }
        hero.set("colors", colors);
        hero.put("name", player.getHero().getName());
        hero.put("health", player.getHero().getHealth());
        return hero;
    }

    int getPlayerTurn(final Game game) {
        return game.getPlayerTurn();
    }

    int getPlayerOneWins(final GameStats gameStats) {
        return gameStats.getPlayerOneWins();
    }

    int getPlayerTwoWins(final GameStats gameStats) {
        return gameStats.getPlayerTwoWins();
    }

    int getTotalGamesPlayed(final GameStats gameStats) {
        return gameStats.getTotalGamesPlayed();
    }

    void endPlayerTurn(final Game game, final Player playerOne, final Player playerTwo,
                       final Table table, final GameStats gameStats) {
        game.setTotalTurns(game.getTotalTurns() + 1);

        if (game.getPlayerTurn() == Player.PLAYER_ONE_ID) {
            for (int i = Table.PLAYER_ONE_FRONT_ROW; i <= Table.PLAYER_ONE_BACK_ROW; i++) {
                for (int j = 0; j < table.getTableCards().get(i).size(); j++) {
                    CardInput currentCard = table.getTableCards().get(i).get(j);
                    if (currentCard.getIsFrozen()) {
                        currentCard.setIsFrozen(false);
                    }
                }
            }
        } else {
            for (int i = Table.PLAYER_TWO_BACK_ROW; i <= Table.PLAYER_TWO_FRONT_ROW; i++) {
                for (int j = 0; j < table.getTableCards().get(i).size(); j++) {
                    CardInput currentCard = table.getTableCards().get(i).get(j);
                    if (currentCard.getIsFrozen()) {
                        currentCard.setIsFrozen(false);
                    }
                }
            }
        }
        if (!gameStats.isGameOver()) {
            if (game.getPlayerTurn() == 1) {
                game.setPlayerTurn(2);
            } else {
                game.setPlayerTurn(1);
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

    ArrayNode getCardsInHand(final ObjectNode objectNode, final ObjectMapper mapper,
                             final Player player) {
        objectNode.put("command", "getCardsInHand");
        objectNode.put("playerIdx", player.getId());
        ArrayNode cardsInHand = mapper.createArrayNode();
        for (int i = 0; i < player.getCardsInHand().size(); i++) {
            ObjectNode card = mapper.createObjectNode();
            card.put("mana", player.getCardsInHand().get(i).getMana());
            card.put("attackDamage", player.getCardsInHand().get(i).getAttackDamage());
            card.put("health", player.getCardsInHand().get(i).getHealth());
            card.put("description", player.getCardsInHand().get(i).getDescription());
            ArrayNode colors = mapper.createArrayNode();
            for (int j = 0; j < player.getCardsInHand().get(i).getColors().size(); j++) {
                colors.add(player.getCardsInHand().get(i).getColors().get(j));
            }
            card.set("colors", colors);
            card.put("name", player.getCardsInHand().get(i).getName());
            cardsInHand.add(card);
        }
        return cardsInHand;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(final String command) {
        this.command = command;
    }
}
