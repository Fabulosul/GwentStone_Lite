package org.poo.main.cards;

import org.poo.fileio.CardInput;

import java.util.ArrayList;

public class KingMudface extends HeroCard {
    public KingMudface() {
        super();
    }

    public KingMudface(CardInput card) {
        super(card);
    }

    @Override
    public void useHeroAbility(ArrayList<Card> row) {
        for (Card currentCard : row) {
            currentCard.setHealth(currentCard.getHealth() + 1);
        }
    }
}
