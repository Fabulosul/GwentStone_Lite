package org.poo.main.cards;



import org.poo.fileio.CardInput;

import java.util.ArrayList;

public final class EmpressThorina extends HeroCard {
    public EmpressThorina() {
        super();
    }

    public EmpressThorina(final CardInput card) {
        super(card);
    }

    @Override
    public void useHeroAbility(final ArrayList<Card> row) {
        int highestHealth = -1;
        int highestHealthIdx = -1;
        for (int i = 0; i < row.size(); i++) {
            int currentCardHealth = row.get(i).getHealth();
            if (currentCardHealth > highestHealth) {
                highestHealth = currentCardHealth;
                highestHealthIdx = i;
            }
        }
        row.remove(highestHealthIdx);
    }
}
