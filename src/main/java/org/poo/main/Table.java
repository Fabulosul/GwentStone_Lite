package org.poo.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.main.cards.Card;
import org.poo.main.cards.minioncards.MinionCard;
import org.poo.main.cards.specialabilitycards.SpecialAbilityCard;


import java.util.ArrayList;

public final class Table {
    public static final int PLAYER_TWO_BACK_ROW = 0;
    public static final int PLAYER_TWO_FRONT_ROW = 1;
    public static final int PLAYER_ONE_FRONT_ROW = 2;
    public static final int PLAYER_ONE_BACK_ROW = 3;
    public static final int ROWS = 4;
    public static final int MAX_CARDS_ON_ROW = 5;
    private ArrayList<ArrayList<Card>> tableCards;

    public Table() {
        tableCards = new ArrayList<>();

        for (int i = 0; i < ROWS; i++) {
            ArrayList<Card> row = new ArrayList<>();
            tableCards.add(row);
        }
    }

    public ObjectNode createCardObject(final ObjectMapper mapper, final Card card) {
        ObjectNode cardObject = mapper.createObjectNode();
        cardObject.put("mana", card.getMana());
        cardObject.put("attackDamage", card.getAttackDamage());
        cardObject.put("health", card.getHealth());
        cardObject.put("description", card.getDescription());
        ArrayNode colors = mapper.createArrayNode();
        for (int j = 0; j < card.getColors().size(); j++) {
            colors.add(card.getColors().get(j));
        }
        cardObject.set("colors", colors);
        cardObject.put("name", card.getName());
        return cardObject;
    }

    public ArrayNode addCardsOnTableToArr(final ObjectNode objectNode, final ObjectMapper mapper) {
        objectNode.put("command", "getCardsOnTable");
        ArrayNode cardsOnTable = mapper.createArrayNode();

        for (int i = 0; i < ROWS; i++) {
            ArrayNode rowObjArr = mapper.createArrayNode();
            for (int j = 0; j < tableCards.get(i).size(); j++) {
                    rowObjArr.add(createCardObject(mapper, tableCards.get(i).get(j)));
            }
            cardsOnTable.add(rowObjArr);
        }
        return cardsOnTable;
    }
    public ArrayNode addFrozenCardsToArr(final ObjectNode objectNode,
                                           final ObjectMapper mapper) {
        objectNode.put("command", "getFrozenCardsOnTable");
        ArrayNode frozenCardsOnTable = mapper.createArrayNode();

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j <  tableCards.get(i).size(); j++) {
                if (tableCards.get(i).get(j).isFrozen()) {
                    frozenCardsOnTable.add(createCardObject(mapper, tableCards.get(i).get(j)));
                }
            }
        }
        return frozenCardsOnTable;
    }

    public boolean hasTankCards(final int playerIdx) {
        if (playerIdx == 1) {
            for (int j = 0; j < getTableCards().get(2).size(); j++) {
                Card card = getTableCards().get(2).get(j);
                if (!card.hasSpecialAbility() && ((MinionCard) card).isTank()) {
                    return true;
                }
            }
        } else {
            for (int j = 0; j < getTableCards().get(1).size(); j++) {
                Card card = getTableCards().get(1).get(j);
                if (!card.hasSpecialAbility() && ((MinionCard) card).isTank()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void resetCardProperties() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < tableCards.get(i).size(); j++) {
                if (tableCards.get(i).get(j) != null) {
                    getTableCards().get(i).get(j).setHasAttacked(false);
                    if(getTableCards().get(i).get(j).hasSpecialAbility()) {
                        ((SpecialAbilityCard)getTableCards().get(i).get(j)).setHasUsedAbility(false);
                    }
                }
            }
        }
    }

    public void addCardAtPosToArr(final Table table, final int cardRow, final int cardColumn,
                                  final ObjectNode objectNode, final ObjectMapper mapper) {
        objectNode.put("command", "getCardAtPosition");
        objectNode.put("x", cardRow);
        objectNode.put("y", cardColumn);
        if (table.getTableCards().get(cardRow).size() > cardColumn) {
            Card card = table.getTableCards().get(cardRow).get(cardColumn);
            ObjectNode cardObject = mapper.createObjectNode();
            cardObject.put("mana", card.getMana());
            cardObject.put("attackDamage", card.getAttackDamage());
            cardObject.put("health", card.getHealth());
            cardObject.put("description", card.getDescription());
            ArrayNode colors = mapper.createArrayNode();
            for (int j = 0; j < card.getColors().size(); j++) {
                colors.add(card.getColors().get(j));
            }
            cardObject.set("colors", colors);
            cardObject.put("name", card.getName());
            objectNode.set("output", cardObject);
        } else {
            objectNode.put("output", "No card available at that position.");
        }
    }

    public ArrayList<ArrayList<Card>> getTableCards() {
        return tableCards;
    }

    public void setTableCards(final ArrayList<ArrayList<Card>> tableCards) {
        this.tableCards = tableCards;
    }
}
