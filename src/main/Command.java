package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.CardInput;

public class Command {
    private String command;

    public Command(String command) {
        this.command = command;
    }

    ArrayNode getPlayerDeck(ObjectNode objectNode, ObjectMapper mapper, Player player) {
        objectNode.put("command", "getPlayerDeck");
        objectNode.put("playerIdx", player.id);
        ArrayNode deck = mapper.createArrayNode();
        for(int i = 0; i < player.deck.size(); i++) {
            ObjectNode card = mapper.createObjectNode();
            card.put("mana", player.deck.get(i).getMana());
            card.put("attackDamage", player.deck.get(i).getAttackDamage());
            card.put("health", player.deck.get(i).getHealth());
            card.put("description", player.deck.get(i).getDescription());
            ArrayNode colors = mapper.createArrayNode();
            for(int j = 0; j < player.deck.get(i).getColors().size(); j++) {
                colors.add(player.deck.get(i).getColors().get(j));
            }
            card.set("colors", colors);
            card.put("name", player.deck.get(i).getName());
            deck.add(card);
        }
        return deck;
    }

    ObjectNode getPlayerHero(ObjectNode objectNode, ObjectMapper mapper, Player player) {
        objectNode.put("command", "getPlayerHero");
        objectNode.put("playerIdx", player.id);
        ObjectNode hero = mapper.createObjectNode();
        hero.put("mana", player.hero.getMana());
        hero.put("description", player.hero.getDescription());
        ArrayNode colors = mapper.createArrayNode();
        for(int j = 0; j < player.hero.getColors().size(); j++) {
            colors.add(player.hero.getColors().get(j));
        }
        hero.set("colors", colors);
        hero.put("name", player.hero.getName());
        hero.put("health", player.hero.getHealth());
        return hero;
    }

    int getPlayerTurn(Game game) {
        return game.getPlayerTurn();
    }

    void endPlayerTurn(Game game, Player playerOne, Player playerTwo, Table table) {
        game.setTotalTurns(game.getTotalTurns() + 1);
        if(game.getPlayerTurn() == 1) {
            game.setPlayerTurn(2);
        } else {
            game.setPlayerTurn(1);
        }
        if(game.getTotalTurns() % 2 == 0) {
            game.setNrRound(game.getNrRound() + 1);
            playerOne.mana += game.getNrRound();
            playerTwo.mana += game.getNrRound();
            playerOne.drawCard();
            playerTwo.drawCard();
            table.resetCardProperties();
        }
    }

    ArrayNode getCardsInHand(ObjectNode objectNode, ObjectMapper mapper, Player player) {
        objectNode.put("command", "getCardsInHand");
        objectNode.put("playerIdx", player.id);
        ArrayNode cardsInHand = mapper.createArrayNode();
        for(int i = 0; i < player.cardsInHand.size(); i++) {
            ObjectNode card = mapper.createObjectNode();
            card.put("mana", player.cardsInHand.get(i).getMana());
            card.put("attackDamage", player.cardsInHand.get(i).getAttackDamage());
            card.put("health", player.cardsInHand.get(i).getHealth());
            card.put("description", player.cardsInHand.get(i).getDescription());
            ArrayNode colors = mapper.createArrayNode();
            for(int j = 0; j < player.cardsInHand.get(i).getColors().size(); j++) {
                colors.add(player.cardsInHand.get(i).getColors().get(j));
            }
            card.set("colors", colors);
            card.put("name", player.cardsInHand.get(i).getName());
            cardsInHand.add(card);
        }
        return cardsInHand;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
