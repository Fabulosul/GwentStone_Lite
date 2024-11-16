package org.poo.main.cards;


import org.poo.fileio.CardInput;

import java.util.ArrayList;

public final class LordRoyce extends HeroCard {
    public LordRoyce() {
        super();
    }

    public LordRoyce(final CardInput card) {
        super(card);
    }

    @Override
    public void useHeroAbility(final ArrayList<Card> row) {
        for (Card currentCard : row) {
            currentCard.setIsFrozen(true);
        }
    }
}
