package main;

import fileio.CardInput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Utility {

    private Utility() { }


    public static ArrayList<CardInput> chooseDeck(final int playerDeckIdx,
                                                  final ArrayList<ArrayList<CardInput>> decks) {
        ArrayList<CardInput> currentDeck = new ArrayList<>();
        for (CardInput card : decks.get(playerDeckIdx)) {
            currentDeck.add(new CardInput(card));
        }
        return currentDeck;
    }


    public static ArrayList<CardInput> shuffleDeck(final ArrayList<CardInput> deck,
                                                   final int seed) {
        Random random = new Random(seed);
        Collections.shuffle(deck, random);
        return deck;
    }

    public static int getPlayerId(final int row) {
        if (row == 2 || row == 3) {
            return 1;
        } else {
            return 2;
        }
    }

}
