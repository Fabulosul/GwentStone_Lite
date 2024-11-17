package org.poo.main.cards.minioncards;


import org.poo.fileio.CardInput;

public final class Warden extends MinionCard {
    /**
     * Default constructor for the Warden class which calls
     * the super constructor and also sets the allowed position
     * to FRONT and the tank field to true.
     * The Warden is a Tank card, so it must stay in the front row and his
     * isTank field must be set to true.
     */
    public Warden() {
        super();
        setAllowedPosition(Position.FRONT);
        setTank(true);
    }

    /**
     * Copy constructor for the Warden class which calls the super constructor
     * for the actual coping and also sets the allowed position to FRONT and the isTank
     * field to true.
     * The Warden is a Tank card, so it must always be placed in the front row.
     * @param card - the card to be copied
     */
    public Warden(final CardInput card) {
        super(card);
        setAllowedPosition(Position.FRONT);
        setTank(true);
    }
}
