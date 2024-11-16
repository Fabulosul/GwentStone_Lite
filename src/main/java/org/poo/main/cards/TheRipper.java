package org.poo.main.cards;


import org.poo.fileio.CardInput;

public final class TheRipper extends SpecialAbilityCard {
    public TheRipper() {
        super();
        setAllowedPosition(Position.FRONT);
    }

    public TheRipper(final CardInput card) {
        super(card);
        setAllowedPosition(Position.FRONT);
    }

    @Override
    public void useAbility(final MinionCard minionCard) {
        super.useAbility(minionCard);
        if (minionCard.getAttackDamage() < 2) {
            minionCard.setAttackDamage(0);
        } else {
            minionCard.setAttackDamage(minionCard.getAttackDamage() - 2);
        }
    }
}
