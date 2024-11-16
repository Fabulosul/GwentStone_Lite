package org.poo.main.cards;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CardInput;
import org.poo.fileio.Coordinates;
import org.poo.main.Player;
import org.poo.main.Table;

import java.util.ArrayList;

public class SpecialAbilityCard extends Card {
    private boolean hasUsedAbility;

    public SpecialAbilityCard() {
        super();
        setHasSpecialAbility(true);
    }

    public SpecialAbilityCard(CardInput card) {
        super(card);
        hasUsedAbility = false;
        setHasSpecialAbility(true);

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
     * Method that handles the case when a card tries to use an ability on another card
     * and succeeds.
     * It sets the hasUsedAbility field to true to indicate that that specific card has used its
     * ability this turn.
     * It also checks the name of card attacker to apply the correct feature.
     * The Ripper - reduces the attack damage of the attacked card by 2 points(the attack damage
     * can't be negative so it truncates to 0 if the attack damage is less than 2)
     * Miraj - swaps the health of the attacker card with the health of the attacked card
     * The Cursed One - swaps the health of the attacked card with its attack damage(if the attack
     * damage is 0, the card is removed from the table)
     * Disciple - increases the health of the attacked card by 2 points
     *
     * @param cardAttacked - the card that is attacked
     * @param cardAttackedCoordinates - the coordinates of the card that is attacked
     * @param table - the table(game board) where the cards are placed in two rows for each player
     */
    public void cardUsesAbilitySucceeded(final Card cardAttacked,
                                         final Coordinates cardAttackedCoordinates,
                                         final Table table) {
        setHasUsedAbility(true);
        if (getName().equals("The Ripper")) {
            if (cardAttacked.getAttackDamage() < 2) {
                cardAttacked.setAttackDamage(0);
            } else {
                cardAttacked.setAttackDamage(cardAttacked.getAttackDamage() - 2);
            }
        }
        if (getName().equals("Miraj")) {
            int cardAttackerHealth = getHealth();
            setHealth(cardAttacked.getHealth());
            cardAttacked.setHealth(cardAttackerHealth);
        }
        if (getName().equals("The Cursed One")) {
            if (cardAttacked.getAttackDamage() == 0) {
                ArrayList<Card> cardAttackedRow
                        = table.getTableCards().get(cardAttackedCoordinates.getX());
                cardAttackedRow.remove(cardAttackedCoordinates.getY());
            } else {
                int cardAttackedHealth = cardAttacked.getHealth();
                cardAttacked.setHealth(cardAttacked.getAttackDamage());
                cardAttacked.setAttackDamage(cardAttackedHealth);
            }
        }
        if (getName().equals("Disciple")) {
            int cardHealth = cardAttacked.getHealth();
            cardAttacked.setHealth(cardHealth + 2);
        }
    }

    /**
     * Method that handles the case when a card tries to use an ability on another card.
     * It checks if the card can use its ability and if it's possible the cardUsesAbilitySucceeded
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

        if (getName().equals("Disciple") && cardAttackerId != cardAttackedId) {
            cardUsesAbilityFailed(cardAttackerCoordinates, cardAttackedCoordinates,
                    "Attacked card does not belong to the current player.", objectNode, mapper);
            return false;
        }

        if ((getName().equals("The Ripper") || getName().equals("Miraj")
                || getName().equals("The Cursed One"))) {
            if (cardAttackerId == cardAttackedId) {
                cardUsesAbilityFailed(cardAttackerCoordinates, cardAttackedCoordinates,
                        "Attacked card does not belong to the enemy.", objectNode, mapper);
                return false;
            }
            if (table.hasTankCards(cardAttackedId)
                    && !cardAttacked.getName().equals("Goliath")
                    && !cardAttacked.getName().equals("Warden")) {
                cardUsesAbilityFailed(cardAttackerCoordinates, cardAttackedCoordinates,
                        "Attacked card is not of type 'Tank'.", objectNode, mapper);
                return false;
            }
        }
        cardUsesAbilitySucceeded(cardAttacked, cardAttackedCoordinates, table);

        return true;
    }

    public void useAbility(MinionCard minionCard) {
        hasUsedAbility = true;
    }

    public boolean hasUsedAbility() {
        return hasUsedAbility;
    }

    public void setHasUsedAbility(boolean hasUsedAbility) {
        this.hasUsedAbility = hasUsedAbility;
    }
}
