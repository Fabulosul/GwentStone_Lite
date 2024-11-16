package org.poo.main.cards;

import org.poo.fileio.CardInput;

public final class Goliath extends MinionCard{
    public Goliath() {
        super();
        setAllowedPosition(Position.FRONT);
    }

    public Goliath(final CardInput card) {
        super(card);
        setAllowedPosition(Position.FRONT);
    }

}
