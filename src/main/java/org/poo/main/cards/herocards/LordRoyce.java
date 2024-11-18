package org.poo.main.cards.herocards;


import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CardInput;
import org.poo.main.Player;
import org.poo.main.cards.Card;

import java.util.ArrayList;

public final class LordRoyce extends HeroCard {
    /**
     * Default constructor for the LordRoyce class which calls
     * the super constructor.
     */
    public LordRoyce() {
        super();
    }

    /**
     * Copy constructor for the LordRoyce class which calls
     * the super constructor for the actual coping.
     * @param card - the card to be copied
     * */
    public LordRoyce(final CardInput card) {
        super(card);
    }

    /**
     * Method overridden from the HeroCard class that uses the hero ability of the Lord Royce
     * hero card on a row given in input.
     * It applies the isFrozen effect on all the cards in the row.
     * @param row - the row on which the hero's ability is used
     */
    @Override
    public void useHeroAbility(final ArrayList<Card> row) {
        for (Card currentCard : row) {
            currentCard.setIsFrozen(true);
        }
    }

    /**
     * Method overridden from the HeroCard class that checks if the Lord Royce's ability
     * can be used on a row given in input.
     * It checks whether the row belongs to the opponent, if the hero
     * has already used its ability in the current turn an if the player
     * has enough mana to use the hero's ability.
     *
     * @param affectedRow - the row on which the hero's ability might be used
     * @param currentPlayerTurn - the id of the player with the turn in progress
     * @return true if the row belongs to the opponent, false otherwise
     */
    @Override
    public boolean canUseHeroAbility(final int affectedRow, final int currentPlayerTurn,
                                     final Player player, final ObjectNode objectNode) {
        if (!super.canUseHeroAbility(affectedRow, currentPlayerTurn, player, objectNode)) {
            return false;
        }
        if (!isOpponentRow(affectedRow, currentPlayerTurn)) {
            useHeroAbilityFailed(affectedRow, "Selected row does not belong to the enemy.",
                    objectNode);
            return false;
        }
        return true;
    }
}
