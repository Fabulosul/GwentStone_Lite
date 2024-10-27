package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Command {
    private String command;

    public Command(String command) {
        this.command = command;
    }

    ArrayNode getPlayerDeck(ObjectNode objectNode, ObjectMapper mapper, Player player) {
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

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
