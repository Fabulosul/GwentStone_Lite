package org.poo.main.cards;

import org.poo.fileio.CardInput;

import java.util.ArrayList;

public final class KingMudface extends HeroCard {
    public KingMudface() {
        super();
    }

    public KingMudface(final CardInput card) {
        super(card);
    }

    @Override
    public void useHeroAbility(final ArrayList<Card> row) {
        for (Card currentCard : row) {
            currentCard.setHealth(currentCard.getHealth() + 1);
        }
    }
}
