package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.CardInput;

import java.util.ArrayList;

public class Table {
    private ArrayList<ArrayList<CardInput>> tableCards;

    public Table() {
        tableCards = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            ArrayList<CardInput> row = new ArrayList<>();
            tableCards.add(row);
        }
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
            ArrayNode RowObjArr = mapper.createArrayNode();
            for (int j = 0; j <  tableCards.get(i).size(); j++) {
                    RowObjArr.add(createCardObject(mapper, tableCards.get(i).get(j)));
            }
            cardsOnTable.add(RowObjArr);
        }
        return cardsOnTable;
    }

    public boolean containsFrozenCards(int Row) {
        for (int j = 0; j < getTableCards().get(Row).size(); j++) {
            if(getTableCards().get(Row).get(j).getIsFrozen()) {
                return true;
            }
        }
        return false;
    }

    public ArrayNode getFrozenCardsOnTable(ObjectNode objectNode, ObjectMapper mapper) {
        objectNode.put("command", "getFrozenCardsOnTable");
        ArrayNode frozenCardsOnTable = mapper.createArrayNode();

        for (int i = 0; i < 4; i++) {
            if(containsFrozenCards(i)){
                ArrayNode RowObjArr = mapper.createArrayNode();
                for (int j = 0; j <  tableCards.get(i).size(); j++) {
                    if(tableCards.get(i).get(j).getIsFrozen()) {
                        RowObjArr.add(createCardObject(mapper, tableCards.get(i).get(j)));
                    }
                }
                frozenCardsOnTable.add(RowObjArr);
            }
        }
        return frozenCardsOnTable;
    }

    public boolean checkForTankCards(int playerIdx) {
        if(playerIdx == 1){
            if(getTableCards().get(2) == null) {
                System.out.println("is null");
            }
            for (int j = 0; j < getTableCards().get(2).size(); j++) {
                if(getTableCards().get(2).get(j).getName().equals("Goliath") ||
                        getTableCards().get(2).get(j).getName().equals("Warden")) {
                    return true;
                }
            }
        } else {
            if(getTableCards().get(1) == null) {
                System.out.println("is null");
            }
            for (int j = 0; j < getTableCards().get(1).size(); j++) {
                if(getTableCards().get(1).get(j).getName().equals("Goliath") ||
                        getTableCards().get(1).get(j).getName().equals("Warden")) {
                    return true;
                }
            }
        }
        return false;
    }

    public void resetCardProperties() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < tableCards.get(i).size(); j++) {
                if(tableCards.get(i).get(j) != null) {
                    this.getTableCards().get(i).get(j).setIsFrozen(false);
                    this.getTableCards().get(i).get(j).setHasAttacked(false);
                    this.getTableCards().get(i).get(j).setHasUsedAbility(false);
                }
            }
        }
    }

    public void getCardAtPosition(Table table, int cardRow, int cardColumn,
                                  ObjectNode objectNode, ObjectMapper mapper) {
        objectNode.put("command", "getCardAtPosition");
        objectNode.put("x", cardRow);
        objectNode.put("y", cardColumn);
        if(table.getTableCards().get(cardRow).size() > cardColumn) {
            CardInput card = table.getTableCards().get(cardRow).get(cardColumn);
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
            objectNode.set("output", cardObject);
        } else {
            objectNode.put("output", "No card available at that position.");
        }
    }

    public ArrayList<ArrayList<CardInput>> getTableCards() {
        return tableCards;
    }

    public void setTableCards(ArrayList<ArrayList<CardInput>> tableCards) {
        this.tableCards = tableCards;
    }

}
