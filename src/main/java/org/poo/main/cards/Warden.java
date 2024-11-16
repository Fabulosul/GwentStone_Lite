package org.poo.main.cards;


import org.poo.fileio.CardInput;

public class Warden extends MinionCard{

    public Warden() {
        super();
        setAllowedPosition(Position.FRONT);
    }

    public Warden(CardInput card) {
        super(card);
        setAllowedPosition(Position.FRONT);
    }
}
