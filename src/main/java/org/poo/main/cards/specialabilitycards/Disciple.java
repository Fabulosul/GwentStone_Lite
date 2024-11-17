package org.poo.main.cards.specialabilitycards;


import org.poo.fileio.CardInput;
import org.poo.main.cards.Card;

public final class Disciple extends SpecialAbilityCard {
    /**
     * Default constructor for the Disciple class that calls the constructor of the superclass.
     * The Disciple is a back row card so the allowed position is set to BACK.
     */
    public Disciple() {
        super();
        setAllowedPosition(Position.BACK);
    }

    /**
     * Copy constructor for the Disciple class that calls the constructor of the superclass
     * for the actual coping.
     * The Disciple is a back row card so the allowed position is set to BACK.
     * @param card - the card to be copied
     */
    public Disciple(final CardInput card) {
        super(card);
        setAllowedPosition(Position.BACK);
    }

    /**
     * Method overridden from the superclass called SpecialAbilityCard
     * that uses the ability of the Disciple on another card given as parameter
     * and sets the hasUsedAbility field to true.
     * The ability of the Disciple card is to increase the card's health by 2.
     * @param card - the card on which the ability is used
     */
    @Override
    public void useAbility(final Card card) {
        setHasUsedAbility(true);
        int cardHealth = card.getHealth();
        card.setHealth(cardHealth + 2);
    }

    /**
     * Method overridden from the superclass called SpecialAbilityCard
     * that checks if the ability of the Disciple can be used on a card.
     * The actual testing consists of measuring whether the player who attacks
     * has the same id as the player who is attacked.
     * @param cardAttackerId - the id of the player who owns the attacking card
     * @param cardAttackedId - the id of the player who owns the attacked card
     * @return true if the ability can be used and false if it's not possible to use the ability
     */
    @Override
    public boolean canUseAbility(final int cardAttackerId, final int cardAttackedId) {
        return cardAttackerId == cardAttackedId;
    }
}
