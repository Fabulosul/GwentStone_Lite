package org.poo.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


import org.poo.fileio.ActionsInput;
import org.poo.main.cards.Card;
import org.poo.main.cards.herocards.HeroCard;

import java.util.ArrayList;

public final class Player {
    public static final int PLAYER_ONE_ID = 1;
    public static final int PLAYER_TWO_ID = 2;
    public static final int MAX_CARDS_IN_HAND = 5;
    private int id;
    private int mana;
    private HeroCard hero;
    private ArrayList<Card> deck;
    private ArrayList<Card> cardsInHand;

    /**
     * Constructor for the Player class which sets the id of the player, the hero card,
     * the deck of cards and initialises a new array list for the cards in hand.
     * @param id - the id of the player
     * @param hero - the hero card of the player
     * @param deck - the deck of cards of the player
     */
    public Player(final int id, final HeroCard hero, final ArrayList<Card> deck) {
        this.id = id;
        this.mana = 1;
        this.deck = deck;
        this.hero = hero;
        this.cardsInHand = new ArrayList<>(MAX_CARDS_IN_HAND);
    }

    /**
     * Method that removes a card from the deck of the player and adds it to the cards in hand.
     */
    void drawCard() {
        if (!deck.isEmpty()) {
            cardsInHand.add(deck.get(0));
            deck.remove(0);
        }
    }

    /**
     * Method used to add all the cards from a player's hand to an array node.
     * It creates a new object node called card for each card in hand and adds all the
     * fields of the card to the object node, then adds the object node to the array node.
     *
     * @param objectNode - the object node to which all the information is added
     * @param mapper - the object mapper used to create the object nodes
     * @return an array node containing all the cards in hand of the player
     */
    public ArrayNode addCardsInHandToArr(final ObjectNode objectNode, final ObjectMapper mapper) {
        objectNode.put("command", "getCardsInHand");
        objectNode.put("playerIdx", getId());
        ArrayNode cardsInHandArr = mapper.createArrayNode();
        for (int i = 0; i < getCardsInHand().size(); i++) {
            ObjectNode card = mapper.createObjectNode();
            card.put("mana", getCardsInHand().get(i).getMana());
            card.put("attackDamage", getCardsInHand().get(i).getAttackDamage());
            card.put("health", getCardsInHand().get(i).getHealth());
            card.put("description", getCardsInHand().get(i).getDescription());
            ArrayNode colors = mapper.createArrayNode();
            for (int j = 0; j < getCardsInHand().get(i).getColors().size(); j++) {
                colors.add(getCardsInHand().get(i).getColors().get(j));
            }
            card.set("colors", colors);
            card.put("name", getCardsInHand().get(i).getName());
            cardsInHandArr.add(card);
        }
        return cardsInHandArr;
    }

    /**
     * Method used to add all the cards from a player's deck to an array node.
     * It creates a new object node called card for each card in the deck and adds all the
     * fields of the card to the object node, then adds the object node to the array node.
     *
     * @param objectNode - the object node to which all the information is added
     *                   about the player's deck
     * @param mapper - the object mapper used to create the object nodes
     * @return an array node containing all the cards in the player's deck
     */
    public ArrayNode addPlayerDeckToArr(final ObjectNode objectNode, final ObjectMapper mapper) {
        objectNode.put("command", "getPlayerDeck");
        objectNode.put("playerIdx", getId());
        ArrayNode deckArr = mapper.createArrayNode();
        for (int i = 0; i < getDeck().size(); i++) {
            ObjectNode card = mapper.createObjectNode();
            card.put("mana", getDeck().get(i).getMana());
            card.put("attackDamage", getDeck().get(i).getAttackDamage());
            card.put("health", getDeck().get(i).getHealth());
            card.put("description", getDeck().get(i).getDescription());
            ArrayNode colors = mapper.createArrayNode();
            for (int j = 0; j < getDeck().get(i).getColors().size(); j++) {
                colors.add(getDeck().get(i).getColors().get(j));
            }
            card.set("colors", colors);
            card.put("name", getDeck().get(i).getName());
            deckArr.add(card);
        }
        return deckArr;
    }

    /**
     * Method used to add all the information about a player's hero to an object node.
     * It adds one by one all the fields of a hero card to the object node.
     *
     * @param objectNode - the object node to which all the information is added
     *                   about a player's hero
     * @param mapper - the object mapper used to create the object nodes
     * @return an object node containing all the information about a player's hero
     */
    public ObjectNode addPlayerHeroToArr(final ObjectNode objectNode, final ObjectMapper mapper) {
        objectNode.put("command", "getPlayerHero");
        objectNode.put("playerIdx", getId());
        ObjectNode heroObj = mapper.createObjectNode();
        heroObj.put("mana", getHero().getMana());
        heroObj.put("description", getHero().getDescription());
        ArrayNode colors = mapper.createArrayNode();
        for (int j = 0; j < getHero().getColors().size(); j++) {
            colors.add(getHero().getColors().get(j));
        }
        heroObj.set("colors", colors);
        heroObj.put("name", getHero().getName());
        heroObj.put("health", getHero().getHealth());
        return heroObj;
    }

    /**
     * Method used to add all the information required in the event of a
     * place card failure to an object node.
     *
     * @param objectNode - the object node to which the error message is added
     * @param handIdx - the index of the card in hand that the player wants to place on the table
     * @param message - the actual error message that is displayed in the output
     */
    private void placeCardFailed(final ObjectNode objectNode, final int handIdx,
                                 final String message) {
        objectNode.put("command", "placeCard");
        objectNode.put("error", message);
        objectNode.put("handIdx", handIdx);
    }

    /**
     * Method used to place a card in the back row of the table.
     * It checks if the card can be placed in a back row on the table and if it's possible, it adds
     * the card to the back row of the table removes it from the player's hand and reduces the
     * player mana.
     * On the other hand, if the card cannot be placed on the table, an error message is added
     * to the object node(possible scenarios are if the players does not have enough mana or
     * the row is full).
     *
     * @param objectNode - the object node to which the error message is added
     *                   in case the card cannot be placed on the table
     * @param handIdx - the index of the card in hand that the player wants to place on the table
     * @param table - the table where the players can place their cards
     * @return true if the card was placed successfully, false otherwise
     */
    private boolean placeCardInBackRow(final ObjectNode objectNode,
                                       final int handIdx, final Table table) {
        if (getCardsInHand().size() <= handIdx) {
            return true;
        }
        int cardMana = getCardsInHand().get(handIdx).getMana();
        if (cardMana > getMana()) {
            placeCardFailed(objectNode, handIdx, "Not enough mana to place card on table.");
            return false;
        } else {
            if (getId() == PLAYER_ONE_ID) {
                if (table.getTableCards().get(Table.PLAYER_ONE_BACK_ROW).size()
                        == Table.MAX_CARDS_ON_ROW) {
                    placeCardFailed(objectNode, handIdx,
                            "Cannot place card on table since row is full.");
                    return false;
                } else {
                    setMana(getMana() - cardMana);
                    ArrayList<Card> playerOneBackRow
                            = table.getTableCards().get(Table.PLAYER_ONE_BACK_ROW);
                    playerOneBackRow.add(cardsInHand.get(handIdx));
                    cardsInHand.remove(handIdx);
                }
            } else {
                if (table.getTableCards().get(Table.PLAYER_TWO_BACK_ROW).size()
                        == Table.MAX_CARDS_ON_ROW) {
                    placeCardFailed(objectNode, handIdx,
                            "Cannot place card on table since row is full.");
                    return false;
                } else {
                    setMana(getMana() - cardMana);
                    ArrayList<Card> playerTwoBackRow
                            = table.getTableCards().get(Table.PLAYER_TWO_BACK_ROW);
                    playerTwoBackRow.add(cardsInHand.get(handIdx));
                    getCardsInHand().remove(handIdx);
                }
            }
        }
        return true;
    }

    /**
     * Method used to place a card in the front row of the table.
     * It checks if the card can be placed in a front row on the table and if it's possible, it adds
     * the card to the front row of the table removes it from the player's hand and reduces the
     * player mana.
     * On the other hand, if the card cannot be placed on the table, an error message is added
     * to the object node(possible scenarios are if the players does not have enough mana or
     * the row is full).
     *
     * @param objectNode - the object node to which the error message is added
     *                   in case the card cannot be placed on the table
     * @param handIdx - the index of the card in hand that the player wants to place on the table
     * @param table - the table where the players can place their cards
     * @return true if the card was placed successfully, false otherwise
     */
    private boolean placeCardInFrontRow(final ObjectNode objectNode,
                                        final int handIdx, final Table table) {
        if (getCardsInHand().size() <= handIdx) {
            return true;
        }
        int cardMana = getCardsInHand().get(handIdx).getMana();
        if (cardMana > getMana()) {
            placeCardFailed(objectNode, handIdx, "Not enough mana to place card on table.");
            return false;
        } else {
            if (getId() == Player.PLAYER_ONE_ID) {
                if (table.getTableCards().get(Table.PLAYER_ONE_FRONT_ROW).size()
                        == Table.MAX_CARDS_ON_ROW) {
                    placeCardFailed(objectNode, handIdx,
                            "Cannot place card on table since row is full.");
                    return false;
                } else {
                    setMana(getMana() - cardMana);
                    ArrayList<Card> playerOneFrontRow
                            = table.getTableCards().get(Table.PLAYER_ONE_FRONT_ROW);
                    playerOneFrontRow.add(cardsInHand.get(handIdx));
                    getCardsInHand().remove(handIdx);
                }
            } else {
                if (table.getTableCards().get(Table.PLAYER_TWO_FRONT_ROW).size()
                        == Table.MAX_CARDS_ON_ROW) {
                    placeCardFailed(objectNode, handIdx,
                            "Cannot place card on table since row is full.");
                    return false;
                } else {
                    setMana(getMana() - cardMana);
                    ArrayList<Card> playerTwoFrontRow
                            = table.getTableCards().get(Table.PLAYER_TWO_FRONT_ROW);
                    playerTwoFrontRow.add(cardsInHand.get(handIdx));
                    getCardsInHand().remove(handIdx);
                }
            }
        }
        return true;
    }

    /**
     * Method used to check if a card can be placed on the table and if it's possible,
     * it actually does the action.
     * Depending on the allowed position of the card, it calls the helper methods for
     * the front row and back row(placeCardInFrontRow and placeCardInBackRow).
     *
     * @param currentAction - all the information useful about the context of placing a card
     * @param objectNode - the object node to which the error message is added
     * @param table - the table where the players can place their cards
     * @return true if the card was placed successfully, false otherwise
     *
     * @see #placeCardInFrontRow(ObjectNode, int, Table) method used to place a card
     * in the front row
     * @see #placeCardInBackRow(ObjectNode, int, Table) method used to place a card in the back row
     */
    boolean placeCard(final ActionsInput currentAction, final ObjectNode objectNode,
                      final Table table) {
        int handIdx = currentAction.getHandIdx();
        if (getCardsInHand().size() <= handIdx) {
            return true;
        }
        Card.Position position = getCardsInHand().get(handIdx).getAllowedPosition();
        if (position == Card.Position.BACK) {
            return placeCardInBackRow(objectNode, handIdx, table);
        } else {
            return placeCardInFrontRow(objectNode, handIdx, table);
        }
    }

    /**
     * Helper method used to find out the id of a player by the id of a row on the table.
     *
     * @param row - the id of the row on the table
     * @return the id of the player that corresponds to the row
     */
    public static int getPlayerByRow(final int row) {
        if (row == Table.PLAYER_ONE_BACK_ROW || row == Table.PLAYER_ONE_FRONT_ROW) {
            return 1;
        } else {
            return 2;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public int getMana() {
        return mana;
    }

    public void setMana(final int mana) {
        this.mana = mana;
    }

    public HeroCard getHero() {
        return hero;
    }

    public void setHero(final HeroCard hero) {
        this.hero = hero;
    }

    public ArrayList<Card> getDeck() {
        return deck;
    }

    public void setDeck(final ArrayList<Card> deck) {
        this.deck = deck;
    }

    public ArrayList<Card> getCardsInHand() {
        return cardsInHand;
    }

    public void setCardsInHand(final ArrayList<Card> cardsInHand) {
        this.cardsInHand = cardsInHand;
    }
}
