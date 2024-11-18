package org.poo.main.cards.herocards;



import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CardInput;
import org.poo.main.Player;
import org.poo.main.cards.Card;

import java.util.ArrayList;

public final class EmpressThorina extends HeroCard {
    /**
     * Default constructor for the EmpressThorina class which calls
     * the super constructor.
     */
    public EmpressThorina() {
        super();
    }

    /**
     * Copy constructor for the EmpressThorina class which calls
     * the super constructor for the actual coping.
     * @param card - the card to be copied
     */
    public EmpressThorina(final CardInput card) {
        super(card);
    }

    /**
     * Method overridden from the HeroCard class that uses the hero ability of the Empress Thorina
     * hero card on a row given in input.
     * It removes the card with the highest health from the row.
     * @param row - the row on which the hero's ability is used
     */
    @Override
    public void useHeroAbility(final ArrayList<Card> row) {
        int highestHealth = -1;
        int highestHealthIdx = -1;
        for (int i = 0; i < row.size(); i++) {
            int currentCardHealth = row.get(i).getHealth();
            if (currentCardHealth > highestHealth) {
                highestHealth = currentCardHealth;
                highestHealthIdx = i;
            }
        }
        row.remove(highestHealthIdx);
    }

    /**
     * Method overridden from the HeroCard class that checks if the Empress Thorina's ability
     * can be used on a row given in input.
     * It checks whether the row belongs to the opponent or not, if the hero
     * has already used its ability in the current turn and if the player
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
