package edu.up.cs301.GameFramework;

public class Card {
    private int cardVal;
    private int num;
    private String cardSuit; //color of card (red, yellow, black, green)

    public Card(int cardVal, int num, String cardSuit){
        this.cardVal = cardVal;
        this.num = num;
        this.cardSuit = cardSuit;
    }
    public Card(Card card){
        this.cardVal = card.cardVal;
        this.cardSuit = card.cardSuit;
        this.num = card.num;
    }

    public void setNum(int num) {
        this.num = num;
    }
    public void setCardSuit(String suit){this.cardSuit = suit;}

    public void setCardVal(int cardVal) {this.cardVal = cardVal;}
     public int getCardVal(){return cardVal;}
    public String getCardSuit() {return cardSuit;}

    public int getNum() {return num;}
}
