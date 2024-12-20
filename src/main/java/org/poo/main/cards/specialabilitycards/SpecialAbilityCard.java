package org.poo.main.cards.specialabilitycards;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CardInput;
import org.poo.fileio.Coordinates;
import org.poo.main.Player;
import org.poo.main.Table;
import org.poo.main.cards.Card;

public class SpecialAbilityCard extends Card {
    private boolean hasUsedAbility;

    /**
     * Default constructor for the SpecialAbilityCard class.
     * It calls the constructor from the superclass, sets the hasSpecialAbility field to true
     * and the hasUsedAbility field to false.
     */
    public SpecialAbilityCard() {
        super();
        setHasSpecialAbility(true);
    }

    /**
     * Copy constructor for the SpecialAbilityCard class.
     * It calls the constructor from the superclass for the actual coping,
     * sets the hasSpecialAbility field to true and the hasUsedAbility field to false.
     *
     * @param card - the card to be copied
     */
    public SpecialAbilityCard(final CardInput card) {
        super(card);
        hasUsedAbility = false;
        setHasSpecialAbility(true);

    }

    /**
     * Method specially designed to be overridden in the subclasses to check whether
     * a card can use its ability or not.
     * In this class the method checks if the card is frozen, if it has already attacked
     * or used its ability this turn.
     *
     * @param cardAttackerId - the id of the card that tries to use the ability
     * @param cardAttackedId - the id of the card that is affected by the ability
     * @return false
     *
     * #in the subclasses it returns true if the card can use its ability,
     * false if the card can't use its ability,
     * @see #canUseAbility in the subclasses for more details about the implementation
     */
    public boolean canUseAbility(final Card cardAttacked,
                                 final int cardAttackerId, final int cardAttackedId,
                                 final Coordinates cardAttackerCoordinates,
                                 final Coordinates cardAttackedCoordinates,
                                 final Table table,
                                 final ObjectNode objectNode, final ObjectMapper mapper) {
        if (isFrozen()) {
            cardUsesAbilityFailed(cardAttackerCoordinates, cardAttackedCoordinates,
                    "Attacker card is frozen.", objectNode, mapper);
            return false;
        }

        if (getHasAttacked() || hasUsedAbility()) {
            cardUsesAbilityFailed(cardAttackerCoordinates, cardAttackedCoordinates,
                    "Attacker card has already attacked this turn.", objectNode, mapper);
            return false;
        }
        return true;
    }

    /**
     * Method that handles the case when a card tries to use an ability on another card and fails.
     * It adds the command, the attacker and attacked card coordinates and the error message
     * to the objectNode to be sent to the output using the cardUseAction method (which was
     * designed to avoid code duplication).
     *
     * @param cardAttackerCoordinates - the coordinates of the card that tries to use an ability
     * @param cardAttackedCoordinates - the coordinates of the card that is affected
     * @param message - the message that will be displayed(in the error section)
     * @param objectNode - the object node that will be sent to the output
     * @param mapper - the object mapper used to create the objectNode
     *
     * @see #cardUseAction method used to add the information to the objectNode and reduce code
     * duplication
     */
    public void cardUsesAbilityFailed(final Coordinates cardAttackerCoordinates,
                                      final Coordinates cardAttackedCoordinates,
                                      final String message, final ObjectNode objectNode,
                                      final ObjectMapper mapper) {
        objectNode.put("command", "cardUsesAbility");
        cardUseAction(cardAttackerCoordinates, cardAttackedCoordinates,
                message, objectNode, mapper);
    }

    /**
     * Method that handles the case when a card tries to use an ability on another card.
     * It checks if the card can use its ability and if it's possible the useAbility
     * method is called to handle this case.
     * On the other hand, if the card can't use its feature, the cardUsesAbilityFailed is used
     * to add information to the objectNode for the output(possible scenarios are when the
     * card that attacker is frozen, has already attacked/used ability this turn, the enemy has
     * tank cards, the attacker is a Disciple card and the attacked card doesn't belong to
     * the current player, the attacker is a Ripper/Miraj/The Cursed One and the attacked card
     * doesn't belong to the enemy player).
     *
     * @param cardAttacked - the card that is affected
     * @param cardAttackerCoordinates - the coordinates of the card that uses the ability
     * @param cardAttackedCoordinates - the coordinates of the card that is affected
     * @param table - the game board where the cards that can be played are stored
     * @param objectNode - the object node that will be sent to the output
     * @param mapper - the object mapper used to create the objectNode
     * @return true if the card can use its ability, false if the card can't use its ability
     */
    public boolean cardUsesAbility(final Card cardAttacked,
                                   final Coordinates cardAttackerCoordinates,
                                   final Coordinates cardAttackedCoordinates,
                                   final Table table, final ObjectNode objectNode,
                                   final ObjectMapper mapper) {

        int cardAttackerId = Player.getPlayerByRow(cardAttackerCoordinates.getX());
        int cardAttackedId = Player.getPlayerByRow(cardAttackedCoordinates.getX());

        if (!canUseAbility(cardAttacked, cardAttackerId, cardAttackedId,
                cardAttackerCoordinates, cardAttackedCoordinates, table, objectNode, mapper)) {
            return false;
        }

        useAbility(cardAttacked);

        return true;
    }

    /**
     * Method designed to be overridden in the subclasses to handle the situation when a card
     * uses its ability.
     * In this class the method always sets the hasUsedAbility field to true because no matter
     * the card if this method is called it means that the card can use its ability.
     * @param minionCard - the card that uses the ability
     */
    public void useAbility(final Card minionCard) {
        hasUsedAbility = true;
    }

    /**
     * Getter for the hasUsedAbility field
     * @return true if the card has already used its ability this turn, false otherwise
     */
    public final boolean hasUsedAbility() {
        return hasUsedAbility;
    }

    public final void setHasUsedAbility(final boolean hasUsedAbility) {
        this.hasUsedAbility = hasUsedAbility;
    }
}
