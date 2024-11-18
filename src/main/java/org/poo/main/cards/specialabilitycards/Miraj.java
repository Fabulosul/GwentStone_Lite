package org.poo.main.cards.specialabilitycards;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CardInput;
import org.poo.fileio.Coordinates;
import org.poo.main.Table;
import org.poo.main.cards.Card;
import org.poo.main.cards.minioncards.MinionCard;

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
     * that checks if the ability of the Miraj can be used on a card given as parameter
     * by calling the superclass method and by measuring whether the player who attacks
     * has the same id as the player who is attacked or not and by finding out if the attacked card
     * is a tank card.
     *
     * @param cardAttackerId - the id of the player who owns the attacking card
     * @param cardAttackedId - the id of the player who owns the attacked card
     * @return true if the ability can be used and false if it's not possible to use the ability
     */
    @Override
    public boolean canUseAbility(final Card cardAttacked,
                                 final int cardAttackerId, final int cardAttackedId,
                                 final Coordinates cardAttackerCoordinates,
                                 final Coordinates cardAttackedCoordinates,
                                 final Table table,
                                 final ObjectNode objectNode, final ObjectMapper mapper) {

        if (!super.canUseAbility(cardAttacked, cardAttackerId, cardAttackedId,
                cardAttackerCoordinates, cardAttackedCoordinates, table, objectNode, mapper)) {
            return false;
        }
        if (cardAttackerId == cardAttackedId) {
            cardUsesAbilityFailed(cardAttackerCoordinates, cardAttackedCoordinates,
                    "Attacked card does not belong to the enemy.", objectNode, mapper);
            return false;
        }

        if (table.hasTankCards(cardAttackedId)
                && (cardAttacked.hasSpecialAbility()
                || !((MinionCard) cardAttacked).isTank())) {
            cardUsesAbilityFailed(cardAttackerCoordinates, cardAttackedCoordinates,
                    "Attacked card is not of type 'Tank'.", objectNode, mapper);
            return false;
        }
        return true;
    }

}
