package edu.up.cs301.GameFramework;

public class Card {
    private int cardVal;
    private int num;
    private int cardSuit; //color of card (red, yellow, black, green)

    public Card(int cardVal, int num, int cardSuit){
        this.cardVal = cardVal;
        this.num = num;
        this.cardSuit = cardSuit;
    }

}
