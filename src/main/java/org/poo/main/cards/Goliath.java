package org.poo.main.cards;

import org.poo.fileio.CardInput;

public class Goliath extends MinionCard{
    public Goliath() {
        super();
        setAllowedPosition(Position.FRONT);
    }

    public Goliath(CardInput card) {
        super(card);
        setAllowedPosition(Position.FRONT);
    }

}
