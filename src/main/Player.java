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
                if(table.getP1BackRowNrCards() == 5) {
                    placeCardFailed(objectNode, handIdx, "Cannot place card on table since row is full.");
                    return false;
                } else {
                    this.mana = this.mana - cardMana;
                    table.getTableCards()[3][table.getP1BackRowNrCards()] = this.cardsInHand.get(handIdx);
                    table.setP1BackRowNrCards(table.getP1BackRowNrCards() + 1);
                    this.cardsInHand.remove(handIdx);
                }
            } else {
                if(table.getP2BackRowNrCards() == 5) {
                    placeCardFailed(objectNode, handIdx, "Cannot place card on table since row is full.");
                    return false;
                } else {
                    this.mana = this.mana - cardMana;
                    table.getTableCards()[0][table.getP2BackRowNrCards()] = this.cardsInHand.get(handIdx);
                    table.setP2BackRowNrCards(table.getP2BackRowNrCards() + 1);
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
                if(table.getP1FrontRowNrCards() == 5) {
                    placeCardFailed(objectNode, handIdx, "Cannot place card on table since row is full.");
                    return false;
                } else {
                    this.mana = this.mana - cardMana;
                    table.getTableCards()[2][table.getP1FrontRowNrCards()] = this.cardsInHand.get(handIdx);
                    table.setP1FrontRowNrCards(table.getP1FrontRowNrCards() + 1);
                    this.cardsInHand.remove(handIdx);
                }
            } else {
                if(table.getP2FrontRowNrCards() == 5) {
                    placeCardFailed(objectNode, handIdx, "Cannot place card on table since row is full.");
                    return false;
                } else {
                    this.mana = this.mana - cardMana;
                    table.getTableCards()[1][table.getP2FrontRowNrCards()] = this.cardsInHand.get(handIdx);
                    table.setP2FrontRowNrCards(table.getP2FrontRowNrCards() + 1);
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

    int getPlayerMana() {
        return this.mana;
    }




}
