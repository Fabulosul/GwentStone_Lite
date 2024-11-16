package org.poo.main.cards;


import org.poo.fileio.CardInput;

public final class Warden extends MinionCard {

    public Warden() {
        super();
        setAllowedPosition(Position.FRONT);
    }

    public Warden(final CardInput card) {
        super(card);
        setAllowedPosition(Position.FRONT);
    }
}
