package org.poo.main;

public class Utility {

    private Utility(){
        // do nothing
    }


    public static int getPlayerId(int Row) {
        if(Row == 2 || Row == 3) {
            return 1;
        } else {
            return 2;
        }
    }

}
