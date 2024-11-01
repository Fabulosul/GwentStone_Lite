package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.CardInput;

import java.util.ArrayList;

public class Player {
    int id;
    int mana;
    CardInput hero;
    ArrayList<CardInput> deck;
    ArrayList<CardInput> cardsInHand;


    public Player(int id, CardInput hero, ArrayList<CardInput> deck) {
        this.id = id;
        this.mana = 1;
        this.deck = deck;
        this.hero = hero;
        this.cardsInHand = new ArrayList<>(5);
    }

    void drawCard() {
        if(!deck.isEmpty()) {
            cardsInHand.add(deck.get(0));
            deck.remove(0);
        }
    }

    private boolean isBackRowCard(CardInput card) {
        return card.getName().equals("Sentinel") || card.getName().equals("Berserker")
                || card.getName().equals("The Cursed One") || card.getName().equals("Disciple");
    }

    private void placeCardFailed(ObjectNode objectNode, int handIdx, String message) {
        objectNode.put("command", "placeCard");
        objectNode.put("error", message);
        objectNode.put("handIdx", handIdx);
    }

    private boolean placeCardInBackRow(ObjectNode objectNode, ObjectMapper mapper, int handIdx, Table table) {
        if(this.cardsInHand.size() <= handIdx)
            return true;
        int cardMana = this.cardsInHand.get(handIdx).getMana();;
        if(cardMana > this.mana) {
            placeCardFailed(objectNode, handIdx, "Not enough mana to place card on table.");
            return false;
        } else {
            if(this.id == 1) {
                if(table.getTableCards().get(3).size() == 5) {
                    placeCardFailed(objectNode, handIdx, "Cannot place card on table since row is full.");
                    return false;
                } else {
                    this.mana = this.mana - cardMana;
                    table.getTableCards().get(3).add(cardsInHand.get(handIdx));
                    cardsInHand.remove(handIdx);
                }
            } else {
                if(table.getTableCards().get(0).size() == 5) {
                    placeCardFailed(objectNode, handIdx, "Cannot place card on table since row is full.");
                    return false;
                } else {
                    this.mana = this.mana - cardMana;
                    table.getTableCards().get(0).add(cardsInHand.get(handIdx));
                    this.cardsInHand.remove(handIdx);
                }
            }
        }
        return true;
    }

    private boolean placeCardInFrontRow(ObjectNode objectNode, ObjectMapper mapper, int handIdx, Table table) {
        if(this.cardsInHand.size() <= handIdx)
            return true;
        int cardMana = this.cardsInHand.get(handIdx).getMana();
        if(cardMana > this.mana) {
            placeCardFailed(objectNode, handIdx, "Not enough mana to place card on table.");
            return false;
        } else {
            if(this.id == 1) {
                if(table.getTableCards().get(2).size() == 5) {
                    placeCardFailed(objectNode, handIdx, "Cannot place card on table since row is full.");
                    return false;
                } else {
                    this.mana = this.mana - cardMana;
                    table.getTableCards().get(2).add(cardsInHand.get(handIdx));
                    this.cardsInHand.remove(handIdx);
                }
            } else {
                if(table.getTableCards().get(1).size() == 5) {
                    placeCardFailed(objectNode, handIdx, "Cannot place card on table since row is full.");
                    return false;
                } else {
                    this.mana = this.mana - cardMana;
                    table.getTableCards().get(1).add(cardsInHand.get(handIdx));
                    this.cardsInHand.remove(handIdx);
                }
            }
        }
        return true;
    }

    boolean placeCard(ObjectNode objectNode, ObjectMapper mapper, int handIdx, Table table) {
        if(this.cardsInHand.size() <= handIdx)
            return true;
        if(isBackRowCard(this.cardsInHand.get(handIdx))) {
            return placeCardInBackRow(objectNode, mapper, handIdx, table);
        } else {
            return placeCardInFrontRow(objectNode, mapper, handIdx, table);
        }
    }


    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }
}
