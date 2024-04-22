package edu.up.cs301.GameFramework;

import java.io.Serializable;

public class Card implements Serializable {
    private int cardVal; //point value
    private int num; //card number
    private String cardSuit; //color of card (red, yellow, black, green)

    public Card(int cardVal, int num, String cardSuit){
        this.cardVal = cardVal;
        this.num = num;
        this.cardSuit = cardSuit;
    }
    public Card(Card card){
        if (card != null) {
            this.cardVal = card.getCardVal();
            this.cardSuit = card.getCardSuit();
            this.num = card.getNum();
        }
    }

    public void setNum(int num) {
        this.num = num;
    }
    public void setCardSuit(String suit){this.cardSuit = suit;}

    public void setCardVal(int cardVal) {this.cardVal = cardVal;}
     public int getCardVal(){return cardVal;}
    public String getCardSuit() {return cardSuit;}

    public int getNum() {return num;}

    @Override
    public String toString() {
        return num + " of " + cardSuit;
    }
}
