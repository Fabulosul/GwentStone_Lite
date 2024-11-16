package org.poo.main.cards;

import org.poo.fileio.CardInput;

import java.util.ArrayList;

public class GeneralKocioraw extends HeroCard {
    public GeneralKocioraw() {
        super();
    }

    public GeneralKocioraw(CardInput card) {
        super(card);
    }

    @Override
    public void useHeroAbility(ArrayList<Card> row) {
        for (Card currentCard : row) {
            currentCard.setAttackDamage(currentCard.getAttackDamage() + 1);
        }
    }
}
