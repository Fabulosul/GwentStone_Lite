package org.poo.main.cards;


import org.poo.fileio.CardInput;

import java.util.ArrayList;

public class LordRoyce extends HeroCard {
    public LordRoyce() {
        super();
    }

    public LordRoyce(CardInput card) {
        super(card);
    }

    @Override
    public void useHeroAbility(ArrayList<Card> row) {
        for (Card currentCard : row) {
            currentCard.setIsFrozen(true);
        }
    }
}
