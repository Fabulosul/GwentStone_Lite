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

    /**
     * Default constructor for the Table class which initializes the tableCards field
     * with a new ArrayList of ArrayLists of Cards.
     */
    public Table() {
        tableCards = new ArrayList<>();

        for (int i = 0; i < ROWS; i++) {
            ArrayList<Card> row = new ArrayList<>();
            tableCards.add(row);
        }
    }

    /**
     * Helper method used to avoid duplicate code when adding all the fields
     * of a card to an ObjectNode.
     *
     * @param mapper - an instance of the ObjectMapper class used to create the ObjectNode
     * @param card - the card for which the ObjectNode is created to put its data in it
     * @return an ObjectNode containing all the fields of the card
     */
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

    /**
     * Method used to sent all the cards on the table to the output.
     * It uses two for loops to iterate through all the cards on the table
     * and call the createCardObject method for each card.
     *
     * @param objectNode - the objectNode to which all the information about a card is added
     * @param mapper - the ObjectMapper used to create the ObjectNode
     * @return an ArrayNode containing all the cards on the table
     *
     * @see #createCardObject method used to create an ObjectNode for a card
     */
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

    /**
     * Method used to sent all the frozen cards on the table to the output.
     * It uses two for loops to iterate through all the cards on the table
     * and check if the current card is frozen.
     * If it is, it calls the createCardObject method for each frozen card.
     *
     * @param objectNode - the objectNode to which all the information about a card is added
     * @param mapper - the ObjectMapper used to create the ObjectNode
     * @return an ArrayNode containing all the frozen cards on the table
     */
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

    /**
     * Method used to check if a player has tank cards on his rows.
     * Depending on the player id, it iterates through the cards on the table
     * that belong to that certain player and checks if the current card is a tank.
     * If it founds a tank card, it returns true and exits the method.
     * At the end, if no tank card was found, it returns false.
     *
     * @param playerIdx - the index of the player for which the method checks if he has tank cards
     * @return true if the player has tank cards on the table, false if he doesn't
     */
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

    /**
     * Method used to set the hasAttacked field of all the cards on the table to false
     * and the hasUsedAbility field of the special ability cards to false.
     */
    public void resetCardProperties() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < tableCards.get(i).size(); j++) {
                if (tableCards.get(i).get(j) != null) {
                    Card card = tableCards.get(i).get(j);
                    card.setHasAttacked(false);
                    if (card.hasSpecialAbility()) {
                        ((SpecialAbilityCard) card).setHasUsedAbility(false);
                    }
                }
            }
        }
    }

    /**
     * Method used add the information about a card at a certain position to the output.
     * It checks if the card exists at the position given as parameter and if it does,
     * it creates an ObjectNode with all the information about the card,
     * if not it sends an error message.
     *
     * @param table - the table where the cards are stored
     * @param cardRow - the row of the card
     * @param cardColumn - the column of the card
     * @param objectNode - the objectNode to which the information about the card is added
     * @param mapper - the ObjectMapper used to create the ObjectNode
     */
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
