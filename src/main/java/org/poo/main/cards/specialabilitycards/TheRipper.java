package org.poo.main.cards.specialabilitycards;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CardInput;
import org.poo.fileio.Coordinates;
import org.poo.main.Table;
import org.poo.main.cards.Card;
import org.poo.main.cards.minioncards.MinionCard;

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
     * The actual testing consists of calling the superclass method and checking
     * whether the player who attacks has the same id as the attacked player or not
     * and by finding out if the attacked card is a tank card.
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
