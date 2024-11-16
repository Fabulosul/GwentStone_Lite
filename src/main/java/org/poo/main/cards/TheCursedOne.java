package org.poo.main.cards;


import org.poo.fileio.CardInput;

public final class TheCursedOne extends SpecialAbilityCard {

    public TheCursedOne() {
        super();
        setAllowedPosition(Position.BACK);
    }

    public TheCursedOne(final CardInput card) {
        super(card);
        setAllowedPosition(Position.BACK);
    }

    @Override
    public void useAbility(final MinionCard minionCard) {
        super.useAbility(minionCard);

        int cardAttackedHealth = minionCard.getHealth();
        minionCard.setHealth(minionCard.getAttackDamage());
        minionCard.setAttackDamage(cardAttackedHealth);

    }
}
