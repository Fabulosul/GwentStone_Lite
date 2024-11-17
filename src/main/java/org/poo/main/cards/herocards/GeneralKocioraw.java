package org.poo.main.cards.herocards;

import org.poo.fileio.CardInput;
import org.poo.main.cards.Card;

import java.util.ArrayList;

public final class GeneralKocioraw extends HeroCard {
    /**
     * Default constructor for the GeneralKocioraw class which calls
     * the super constructor.
     */
    public GeneralKocioraw() {
        super();
    }

    /**
     * Copy constructor for the GeneralKocioraw class which calls
     * the super constructor for the actual coping.
     * @param card - the card to be copied
     */
    public GeneralKocioraw(final CardInput card) {
        super(card);
    }

    /**
     * Method overridden from the HeroCard class that uses the hero ability of the General Kocioraw
     * hero card on a row given in input.
     * It increases the attack damage of all the cards in the row by 1.
     * @param row - the row on which the hero's ability is used
     */
    @Override
    public void useHeroAbility(final ArrayList<Card> row) {
        for (Card currentCard : row) {
            currentCard.setAttackDamage(currentCard.getAttackDamage() + 1);
        }
    }

    /**
     * Method overridden from the HeroCard class that checks if the General Kocioraw's ability
     * can be used on a row given in input.
     * It checks whether the row belongs to the current player or not.
     * @param affectedRow - the row on which the hero's ability might be used
     * @param currentPlayerTurn - the id of the player with the turn in progress
     * @return true if the row belongs to the current player, false otherwise
     */
    @Override
    public boolean canUseHeroAbility(final int affectedRow, final int currentPlayerTurn) {
        return !isOpponentRow(affectedRow, currentPlayerTurn);
    }
}
