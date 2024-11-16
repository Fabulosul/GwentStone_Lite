package org.poo.main.cards;

import org.poo.fileio.CardInput;

import java.util.ArrayList;

public final class GeneralKocioraw extends HeroCard {
    public GeneralKocioraw() {
        super();
    }

    public GeneralKocioraw(final CardInput card) {
        super(card);
    }

    @Override
    public void useHeroAbility(final ArrayList<Card> row) {
        for (Card currentCard : row) {
            currentCard.setAttackDamage(currentCard.getAttackDamage() + 1);
        }
    }
}
