package org.poo.main.cards;


import org.poo.fileio.CardInput;

public class Berserker extends MinionCard{
    public Berserker() {
        super();
        setAllowedPosition(Position.BACK);
    }

    public Berserker(CardInput card) {
        super(card);
        setAllowedPosition(Position.BACK);
    }


}
