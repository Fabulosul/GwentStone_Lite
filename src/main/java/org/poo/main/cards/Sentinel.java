package org.poo.main.cards;

import org.poo.fileio.CardInput;

public final class Sentinel extends MinionCard {
    public Sentinel() {
        super();
        setAllowedPosition(Position.BACK);
    }

    public Sentinel(final CardInput card) {
        super(card);
        setAllowedPosition(Position.BACK);
    }

}
