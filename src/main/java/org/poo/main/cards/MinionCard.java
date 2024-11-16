package org.poo.main.cards;

import org.poo.fileio.CardInput;

public class MinionCard extends Card {
    private boolean isTank;

    public MinionCard() {
        super();
        isTank = getName().equals("Warden") || getName().equals("Goliath");
        setHasSpecialAbility(false);
    }

    public MinionCard(CardInput card) {
        super(card);
        isTank = card.getName().equals("Warden") || card.getName().equals("Goliath");
        setHasSpecialAbility(false);
    }

    public boolean isTank() {
        return isTank;
    }

    public void setTank(boolean tank) {
        isTank = tank;
    }
}
