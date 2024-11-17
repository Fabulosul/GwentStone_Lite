package org.poo.main.cards.specialabilitycards;


import org.poo.fileio.CardInput;
import org.poo.main.cards.Card;

public final class TheCursedOne extends SpecialAbilityCard {
    /**
     * Default constructor for The Cursed One class which calls
     * the constructor from the superclass and sets the allowed
     * position to BACK because it is a back row card.
     */
    public TheCursedOne() {
        super();
        setAllowedPosition(Position.BACK);
    }

    /**
     * Copy constructor for The Cursed One class which calls the constructor
     * from the superclass for the actual coping and sets the allowed position
     * to BACK because it is a back row card.
     * @param card - the card to be copied
     */
    public TheCursedOne(final CardInput card) {
        super(card);
        setAllowedPosition(Position.BACK);
    }

    /**
     * Method overridden from the superclass to use the actual ability
     * of a card called The Cursed One.
     * Its ability is to swap the health and attack damage of card given as parameter.
     * @param card - the card affected by the ability
     */
    @Override
    public void useAbility(final Card card) {
        setHasUsedAbility(true);

        int cardAttackedHealth = card.getHealth();
        card.setHealth(card.getAttackDamage());
        card.setAttackDamage(cardAttackedHealth);
    }

    /**
     * Method overridden from the superclass to check if the ability of the card
     * called The Cursed One can be used on another card given as parameter.
     * The actual testing consists of finding out whether the player who attacks
     * is the same as the player who is attacked or not.
     * @param cardAttackerId - the id of the player who owns the attacking card
     * @param cardAttackedId - the id of the player who owns the attacked card
     * @return true if the ids are different and false if they are the same
     */
    @Override
    public boolean canUseAbility(final int cardAttackerId, final int cardAttackedId) {
        return cardAttackerId != cardAttackedId;
    }

}
