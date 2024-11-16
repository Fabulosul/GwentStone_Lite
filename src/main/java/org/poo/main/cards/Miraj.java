package org.poo.main.cards;


import org.poo.fileio.CardInput;

public final class Miraj extends SpecialAbilityCard {

    public Miraj() {
        super();
        setAllowedPosition(Position.FRONT);
    }

    public Miraj(final CardInput card) {
        super(card);
        setAllowedPosition(Position.FRONT);
    }

    @Override
    public void useAbility(final MinionCard minionCard) {
        super.useAbility(minionCard);
        int minionHealth = getHealth();
        setHealth(minionCard.getHealth());
        minionCard.setHealth(minionHealth);
    }
}
