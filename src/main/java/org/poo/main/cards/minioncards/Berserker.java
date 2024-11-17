package org.poo.main.cards.minioncards;


import org.poo.fileio.CardInput;

public final class Berserker extends MinionCard {
    /**
     * Default constructor for the Berserker class which calls the super constructor
     * and also sets the allowed position to BACK and the tank field to false.
     * The Berserker is not a Tank card, so it must stay on a back row and his
     * isTank field must be set to false.
     */
    public Berserker() {
        super();
        setAllowedPosition(Position.BACK);
        setTank(false);
    }

    /**
     * Copy constructor for the Berserker class which calls the super constructor
     * for the actual coping.
     * The Berserker is not a Tank card, so it must stay on a back row and his
     * isTank field must be set to false, so we apply these aspects by setting
     * the allowed position to BACK and the tank field to false.
     * @param card - the card to be copied
     */
    public Berserker(final CardInput card) {
        super(card);
        setAllowedPosition(Position.BACK);
        setTank(false);
    }


}
