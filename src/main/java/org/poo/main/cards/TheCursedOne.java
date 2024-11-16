package org.poo.main.cards;


import org.poo.fileio.CardInput;

public class TheCursedOne extends SpecialAbilityCard{

    public TheCursedOne() {
        super();
        setAllowedPosition(Position.BACK);
    }

    public TheCursedOne(CardInput card) {
        super(card);
        setAllowedPosition(Position.BACK);
    }

    @Override
    public void useAbility(MinionCard minionCard) {
        super.useAbility(minionCard);

        int cardAttackedHealth = minionCard.getHealth();
        minionCard.setHealth(minionCard.getAttackDamage());
        minionCard.setAttackDamage(cardAttackedHealth);

    }
}
