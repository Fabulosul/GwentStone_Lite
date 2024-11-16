package org.poo.main.cards;


import org.poo.fileio.CardInput;

public class Disciple extends SpecialAbilityCard{
    public Disciple() {
        super();
        setAllowedPosition(Position.BACK);
    }

    public Disciple(CardInput card) {
        super(card);
        setAllowedPosition(Position.BACK);
    }

    @Override
    public void useAbility(MinionCard minionCard) {
        super.useAbility(minionCard);
        int cardHealth = minionCard.getHealth();
        minionCard.setHealth(cardHealth + 2);
    }
}
