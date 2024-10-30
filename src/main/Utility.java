package main;

import fileio.CardInput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Utility {

    private Utility(){
        // do nothing
    }


    public static ArrayList<CardInput> chooseDeck(int playerDeckIdx, ArrayList<ArrayList<CardInput>> decks){
        ArrayList<CardInput> currentDeck = new ArrayList<>();
        for(int i = 0; i < decks.get(playerDeckIdx).size(); i++) {
            currentDeck.add(i, decks.get(playerDeckIdx).get(i));
        }
        return currentDeck;
    }


    public static ArrayList<CardInput> shuffleDeck(ArrayList<CardInput> deck, int seed){
        Random random = new Random(seed);
        Collections.shuffle(deck, random);
        return deck;
    }

    public static int getPlayerId(int Row) {
        if(Row == 2 || Row == 3) {
            return 1;
        } else {
            return 2;
        }
    }
}
