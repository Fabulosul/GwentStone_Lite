package main;

import fileio.CardInput;

import java.util.ArrayList;

public class Player {
    int id;
    int turn;
    ArrayList<CardInput> deck;
    ArrayList<CardInput> handCards;

    public Player(int id, int turn, ArrayList<CardInput> deck) {
        this.id = id;
        if(id == turn) {
            this.turn = 1;
        } else {
            this.turn = 0;
        }
        this.deck = deck;
        this.handCards = new ArrayList<>();
    }

    void drawCard() {
        if(!deck.isEmpty()) {
            handCards.add(deck.get(0));
            deck.remove(0);
        }
    }


}
