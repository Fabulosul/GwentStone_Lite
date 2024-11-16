package org.poo.main.cards;

import org.poo.fileio.CardInput;

public class Sentinel extends MinionCard{
    public Sentinel() {
        super();
        setAllowedPosition(Position.BACK);
    }

    public Sentinel(CardInput card) {
        super(card);
        setAllowedPosition(Position.BACK);
    }

}
