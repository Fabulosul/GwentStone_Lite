package org.poo.main.cards;


import org.poo.fileio.CardInput;

public class TheRipper extends SpecialAbilityCard{
    public TheRipper() {
        super();
        setAllowedPosition(Position.FRONT);
    }

    public TheRipper(CardInput card) {
        super(card);
        setAllowedPosition(Position.FRONT);
    }

    @Override
    public void useAbility(MinionCard minionCard) {
        super.useAbility(minionCard);
        if (minionCard.getAttackDamage() < 2) {
            minionCard.setAttackDamage(0);
        } else {
            minionCard.setAttackDamage(minionCard.getAttackDamage() - 2);
        }
    }
}
