package fileio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public final class DecksInput {
    private int nrCardsInDeck;
    private int nrDecks;
    private ArrayList<ArrayList<CardInput>> decks;

    public DecksInput() {
    }

    /**
     * Method that chooses the deck used for the current game.
     * It creates a deep copy of the deck placed on the position given by the index parameter
     * using a copy constructor in the CardInput class.
     *
     * @param playerDeckIdx - the index of the deck used for the current game
     * @param decks - all the decks available
     * @return the deck chosen for the current game
     */
    public static ArrayList<CardInput> chooseDeck(final int playerDeckIdx,
                                                  final ArrayList<ArrayList<CardInput>> decks) {
        ArrayList<CardInput> currentDeck = new ArrayList<>();
        for (CardInput card : decks.get(playerDeckIdx)) {
            currentDeck.add(new CardInput(card));
        }
        return currentDeck;
    }

    /**
     * Method that reorders the cards in a deck using the shuffle method from the Collections class
     * and the seed given for randomization.
     * It generates a random object using the seed and mixes the cards using the shuffle method.
     *
     * @param deck - the deck to be shuffled
     * @param seed - the shuffle seed used to mix the cards
     * @return the shuffled deck
     */
    public static ArrayList<CardInput> shuffleDeck(final ArrayList<CardInput> deck,
                                                   final int seed) {
        Random random = new Random(seed);
        Collections.shuffle(deck, random);
        return deck;
    }

    public int getNrCardsInDeck() {
        return nrCardsInDeck;
    }

    public void setNrCardsInDeck(final int nrCardsInDeck) {
        this.nrCardsInDeck = nrCardsInDeck;
    }

    public int getNrDecks() {
        return nrDecks;
    }

    public void setNrDecks(final int nrDecks) {
        this.nrDecks = nrDecks;
    }

    public ArrayList<ArrayList<CardInput>> getDecks() {
        return decks;
    }

    public void setDecks(final ArrayList<ArrayList<CardInput>> decks) {
        this.decks = decks;
    }

    @Override
    public String toString() {
        return "InfoInput{"
                + "nr_cards_in_deck="
                + nrCardsInDeck
                +  ", nr_decks="
                + nrDecks
                + ", decks="
                + decks
                + '}';
    }
}
