package org.poo.main.cards;


import org.poo.fileio.CardInput;

public final class Berserker extends MinionCard {
    public Berserker() {
        super();
        setAllowedPosition(Position.BACK);
    }

    public Berserker(final CardInput card) {
        super(card);
        setAllowedPosition(Position.BACK);
    }


}
