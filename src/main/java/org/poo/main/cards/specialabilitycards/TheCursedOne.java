package org.poo.main.cards.specialabilitycards;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CardInput;
import org.poo.fileio.Coordinates;
import org.poo.main.Table;
import org.poo.main.cards.Card;
import org.poo.main.cards.minioncards.MinionCard;

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
     * called The Cursed One can be used on another card given as parameter by calling
     * the superclass method, by measuring whether the player who attacks
     * has the same id as the attacked player or not and by checking if the attacked card
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
