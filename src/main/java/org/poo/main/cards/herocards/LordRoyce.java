package org.poo.main.cards.herocards;


import org.poo.fileio.CardInput;
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
     * It checks whether the row belongs to the opponent or not.
     * @param affectedRow - the row on which the hero's ability might be used
     * @param currentPlayerTurn - the id of the player with the turn in progress
     * @return true if the row belongs to the opponent, false otherwise
     */
    @Override
    public boolean canUseHeroAbility(final int affectedRow, final int currentPlayerTurn) {
        return isOpponentRow(affectedRow, currentPlayerTurn);
    }
}
