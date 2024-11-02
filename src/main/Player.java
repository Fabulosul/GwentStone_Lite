package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.CardInput;

import java.util.ArrayList;

public class Player {
    private int id;
    private int mana;
    private CardInput hero;
    private ArrayList<CardInput> deck;
    private ArrayList<CardInput> cardsInHand;


    public Player(final int id, final CardInput hero, final ArrayList<CardInput> deck) {
        this.id = id;
        this.mana = 1;
        this.deck = deck;
        this.hero = hero;
        this.cardsInHand = new ArrayList<>(5);
    }

    void drawCard() {
        if (!deck.isEmpty()) {
            cardsInHand.add(deck.get(0));
            deck.remove(0);
        }
    }

    private boolean isBackRowCard(final CardInput card) {
        return card.getName().equals("Sentinel") || card.getName().equals("Berserker")
                || card.getName().equals("The Cursed One") || card.getName().equals("Disciple");
    }

    private void placeCardFailed(final ObjectNode objectNode, final int handIdx,
                                 final String message) {
        objectNode.put("command", "placeCard");
        objectNode.put("error", message);
        objectNode.put("handIdx", handIdx);
    }

    private boolean placeCardInBackRow(final ObjectNode objectNode, final ObjectMapper mapper,
                                       final int handIdx, final Table table) {
        if (this.cardsInHand.size() <= handIdx) {
            return true;
        }
        int cardMana = this.cardsInHand.get(handIdx).getMana();;
        if (cardMana > this.mana) {
            placeCardFailed(objectNode, handIdx, "Not enough mana to place card on table.");
            return false;
        } else {
            if (this.id == 1) {
                if(table.getTableCards().get(3).size() == 5) {
                    placeCardFailed(objectNode, handIdx,
                            "Cannot place card on table since row is full.");
                    return false;
                } else {
                    this.mana = this.mana - cardMana;
                    table.getTableCards().get(3).add(cardsInHand.get(handIdx));
                    cardsInHand.remove(handIdx);
                }
            } else {
                if (table.getTableCards().get(0).size() == 5) {
                    placeCardFailed(objectNode, handIdx,
                            "Cannot place card on table since row is full.");
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

    private boolean placeCardInFrontRow(final ObjectNode objectNode, final ObjectMapper mapper,
                                        final int handIdx, final Table table) {
        if (this.cardsInHand.size() <= handIdx)
            return true;
        int cardMana = this.cardsInHand.get(handIdx).getMana();
        if (cardMana > this.mana) {
            placeCardFailed(objectNode, handIdx, "Not enough mana to place card on table.");
            return false;
        } else {
            if (this.id == 1) {
                if (table.getTableCards().get(2).size() == 5) {
                    placeCardFailed(objectNode, handIdx,
                            "Cannot place card on table since row is full.");
                    return false;
                } else {
                    this.mana = this.mana - cardMana;
                    table.getTableCards().get(2).add(cardsInHand.get(handIdx));
                    this.cardsInHand.remove(handIdx);
                }
            } else {
                if (table.getTableCards().get(1).size() == 5) {
                    placeCardFailed(objectNode, handIdx,
                            "Cannot place card on table since row is full.");
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

    boolean placeCard(final ObjectNode objectNode, final ObjectMapper mapper,
                      final int handIdx, final Table table) {
        if (this.cardsInHand.size() <= handIdx) {
            return true;
        }
        if (isBackRowCard(this.cardsInHand.get(handIdx))) {
            return placeCardInBackRow(objectNode, mapper, handIdx, table);
        } else {
            return placeCardInFrontRow(objectNode, mapper, handIdx, table);
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

    public CardInput getHero() {
        return hero;
    }

    public void setHero(final CardInput hero) {
        this.hero = hero;
    }

    public ArrayList<CardInput> getDeck() {
        return deck;
    }

    public void setDeck(final ArrayList<CardInput> deck) {
        this.deck = deck;
    }

    public ArrayList<CardInput> getCardsInHand() {
        return cardsInHand;
    }

    public void setCardsInHand(final ArrayList<CardInput> cardsInHand) {
        this.cardsInHand = cardsInHand;
    }
}
