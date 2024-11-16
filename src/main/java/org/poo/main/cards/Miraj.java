package org.poo.main.cards;


import org.poo.fileio.CardInput;

public class Miraj extends SpecialAbilityCard{

    public Miraj() {
        super();
        setAllowedPosition(Position.FRONT);
    }

    public Miraj(CardInput card) {
        super(card);
        setAllowedPosition(Position.FRONT);
    }

    @Override
    public void useAbility(MinionCard minionCard) {
        super.useAbility(minionCard);
        int minionHealth = getHealth();
        setHealth(minionCard.getHealth());
        minionCard.setHealth(minionHealth);
    }
}
