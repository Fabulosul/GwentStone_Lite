package org.poo.main.cards.minioncards;

import org.poo.fileio.CardInput;
import org.poo.main.cards.Card;

public class MinionCard extends Card {
    /** field that stores if the minion is a tank or not */
    private boolean isTank;

    /**
     * Default constructor for the MinionCard class which calls the super constructor
     * and also sets the hasSpecialAbility field to false because minions don't have
     * special abilities
     */
    public MinionCard() {
        super();
        setHasSpecialAbility(false);
    }

    /**
     * Copy constructor for the MinionCard class which calls the super constructor
     * for the actual coping and also sets the hasSpecialAbility field to false
     * because minions don't have special abilities
     * @param card - the card to be copied
     */
    public MinionCard(final CardInput card) {
        super(card);
        setHasSpecialAbility(false);
    }

    /**
     * Getter for the isTank field
     * @return true if the minion is a tank, false otherwise
     */
    public final boolean isTank() {
        return isTank;
    }

    /**
     * Setter for the isTank field
     * @param tank - the value set to true if the minion is a tank, false otherwise
     */
    public final void setTank(final boolean tank) {
        isTank = tank;
    }
}
