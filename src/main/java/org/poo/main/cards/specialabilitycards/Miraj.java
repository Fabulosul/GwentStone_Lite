package org.poo.main.cards.specialabilitycards;


import org.poo.fileio.CardInput;
import org.poo.main.cards.Card;

public final class Miraj extends SpecialAbilityCard {
    /**
     * Default constructor for the Miraj class that calls the constructor of the superclass.
     * The Miraj is a front row card so the allowed position is set to FRONT.
     */
    public Miraj() {
        super();
        setAllowedPosition(Position.FRONT);
    }

    /**
     * Copy constructor for the Miraj class that calls the constructor of the superclass
     * for the actual coping.
     * The Miraj is a front row card so the allowed position is set to FRONT.
     * @param card - the card to be copied
     */
    public Miraj(final CardInput card) {
        super(card);
        setAllowedPosition(Position.FRONT);
    }

    /**
     * Method overridden from the superclass called SpecialAbilityCard
     * that uses the ability of the Miraj on another card given as parameter
     * and sets the hasUsedAbility field to true.
     * @param card - the card affected by the ability of the Miraj
     */
    @Override
    public void useAbility(final Card card) {
        setHasUsedAbility(true);
        int minionHealth = getHealth();
        setHealth(card.getHealth());
        card.setHealth(minionHealth);
    }

    /**
     * Method overridden from the superclass called SpecialAbilityCard
     * that checks if the ability of the Miraj can be used on a card.
     * The actual testing consists of measuring whether the player who attacks
     * has the same id as the player who is attacked or not.
     * @param cardAttackerId - the id of the player who owns the attacking card
     * @param cardAttackedId - the id of the player who owns the attacked card
     * @return true if the ids are different and false if they are the same
     */
    @Override
    public boolean canUseAbility(final int cardAttackerId, final int cardAttackedId) {
        return cardAttackerId != cardAttackedId;
    }

}
