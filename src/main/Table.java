package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.CardInput;

import java.util.ArrayList;

public class Table {
    private CardInput[][] tableCards;
    private int p1BackRowNrCards;
    private int p1FrontRowNrCards;
    private int p2BackRowNrCards;
    private int p2FrontRowNrCards;

    public Table() {
        this.tableCards = new CardInput[4][5];
    }


    public ObjectNode createCardObject(ObjectMapper mapper, CardInput card) {
        ObjectNode cardObject = mapper.createObjectNode();
        cardObject.put("mana", card.getMana());
        cardObject.put("attackDamage", card.getAttackDamage());
        cardObject.put("health", card.getHealth());
        cardObject.put("description", card.getDescription());
        ArrayNode colors = mapper.createArrayNode();
        for(int j = 0; j < card.getColors().size(); j++) {
            colors.add(card.getColors().get(j));
        }
        cardObject.set("colors", colors);
        cardObject.put("name", card.getName());
        return cardObject;
    }

    public ArrayNode getCardsOnTable(ObjectNode objectNode, ObjectMapper mapper) {
        objectNode.put("command", "getCardsOnTable");
        ArrayNode cardsOnTable = mapper.createArrayNode();

        for (int i = 0; i < 4; i++) {
            ArrayNode playerRowObjArr = mapper.createArrayNode();
            for (int j = 0; j < 5; j++) {
                if(tableCards[i][j] != null) {
                    playerRowObjArr.add(createCardObject(mapper, tableCards[i][j]));
                }
            }
            cardsOnTable.add(playerRowObjArr);
        }
        return cardsOnTable;
    }

    public boolean checkForTankCards(int playerId, Table table) {
        if(playerId == 1) {
            return table.p1FrontRowNrCards != 0;
        } else {
            return table.p2FrontRowNrCards != 0;
        }
    }

    public CardInput[][] getTableCards() {
        return tableCards;
    }

    public void setTableCards(CardInput[][] tableCards) {
        this.tableCards = tableCards;
    }

    public int getP1BackRowNrCards() {
        return p1BackRowNrCards;
    }

    public void setP1BackRowNrCards(int p1BackRowNrCards) {
        this.p1BackRowNrCards = p1BackRowNrCards;
    }

    public int getP1FrontRowNrCards() {
        return p1FrontRowNrCards;
    }

    public void setP1FrontRowNrCards(int p1FrontRowNrCards) {
        this.p1FrontRowNrCards = p1FrontRowNrCards;
    }

    public int getP2BackRowNrCards() {
        return p2BackRowNrCards;
    }

    public void setP2BackRowNrCards(int p2BackRowNrCards) {
        this.p2BackRowNrCards = p2BackRowNrCards;
    }

    public int getP2FrontRowNrCards() {
        return p2FrontRowNrCards;
    }

    public void setP2FrontRowNrCards(int p2FrontRowNrCards) {
        this.p2FrontRowNrCards = p2FrontRowNrCards;
    }
}
