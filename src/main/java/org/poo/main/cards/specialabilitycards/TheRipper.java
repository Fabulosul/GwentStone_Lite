package org.poo.main.cards.specialabilitycards;


import org.poo.fileio.CardInput;
import org.poo.main.cards.Card;

public final class TheRipper extends SpecialAbilityCard {
    /**
     * Default constructor for The Ripper class which calls
     * the constructor from the superclass and sets the allowed
     * position to FRONT because it is a front row card.
     */
    public TheRipper() {
        super();
        setAllowedPosition(Position.FRONT);
    }

    /**
     * Copy constructor for The Ripper class which calls the constructor
     * from the superclass for the actual coping.
     * The Ripper is a front row card so the allowed position is set to FRONT.
     * @param card - the card to be copied
     */
    public TheRipper(final CardInput card) {
        super(card);
        setAllowedPosition(Position.FRONT);
    }

    /**
     * Method overridden from the superclass to use the actual ability
     * of a card called The Ripper.
     * Its ability is to decrease the attack damage of card given as parameter by 2
     * (from that point on, the card will deal 2 points less damage).
     * @param card - the card affected by the ability
     */
    @Override
    public void useAbility(final Card card) {
        setHasUsedAbility(true);
        if (card.getAttackDamage() < 2) {
            card.setAttackDamage(0);
        } else {
            card.setAttackDamage(card.getAttackDamage() - 2);
        }
    }

    /**
     * Method overridden from the superclass to check if the ability of the card
     * called The Ripper can be used on another card given as parameter.
     * The actual testing consists of finding out whether the player who attacks
     * has the same id as the attacked player.
     * @param cardAttackerId - the id of the player who owns the attacking card
     * @param cardAttackedId - the id of the player who owns the attacked card
     * @return true if the ids are different and false if they are the same
     */
    @Override
    public boolean canUseAbility(final int cardAttackerId, final int cardAttackedId) {
        return cardAttackerId != cardAttackedId;
    }

}
