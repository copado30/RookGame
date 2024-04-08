package edu.up.cs301.rook;

import android.app.Activity;

import edu.up.cs301.GameFramework.Card;
import edu.up.cs301.GameFramework.Game;
import edu.up.cs301.GameFramework.GameMainActivity;
import edu.up.cs301.GameFramework.infoMessage.GameState;
import java.util.*;




public class RookState extends GameState {
    private int team1Score;
    private int team2Score;
    public int bidNum;
    public int playerId;
    public int roundScore;

    private boolean bidPhase;
    private int bidWinner;

    private String add;
    private int[] playerScores = new int[4];
    ;
    public Card[] deck = new Card[41];
    public Card[][] playerHands = new Card[5][9];

    public RookState() {
        team1Score = 0;
        team2Score = 0;
        roundScore = 0;
        bidWinner = 4;//players are 0-3, 4 means no one has won
        bidPhase = true;
        bidNum = 0;
        playerId = 0;
        add = "";
        for(int i = 0; i < playerScores.length; i++){playerScores[i] = 0;}
    }

    public RookState(RookState gameState) {
        team1Score = gameState.team1Score;
        team2Score = gameState.team2Score;
        bidNum = gameState.bidNum;
        playerId = gameState.playerId;
        roundScore = gameState.roundScore;
        bidWinner = gameState.bidWinner;
        bidPhase = gameState.bidPhase;
        add = gameState.add;


        for(int i = 0; i < playerScores.length; i++) { playerScores[i] = gameState.playerScores[i]; }

        for(int i = 0; i < deck.length; i++) { deck[i] = new Card (gameState.deck[i]); }

        for(int i = 0; i <= 4; i++) {//4 players + 1 nest
            for (int j = 0; j <= 8; j++) {playerHands[i][j] = new Card(gameState.playerHands[i][j]);}
        }
    }//Deep Copy Constructor

    @Override
    public String toString() {
        if (team1Score > team2Score) {
            return "It is player " + playerId + " turn. Team 1 is in the lead with: " + team1Score + " " + add;
        } else if (team2Score > team1Score) {
            return "It is player " + playerId + " turn. Team 2 is in the lead with: " + team2Score + " " + add;
        }
        return "It is player " + playerId + " turn. Teams are tied." + add;
    }


    public void createDeck(){
        String[] colors = new String[]{"Black","Green","Yellow","Red"};
            int newColorStart = 0; // where in array the new set of colored cards begins
        for(int j = 1; j <= colors.length; j++){//for loop for the suits
            if(j > 1){newColorStart += 10;}

            for(int i = 5; i < 15; i++){
                if(i == 5){deck[(i-5)+newColorStart] = new Card(5,i,colors[j-1]);}
                else if(i == 10 || i == 14){deck[(i-5)+newColorStart] = new Card(5,i,colors[j-1]);}
                else{
                    deck[(i-5)+newColorStart] = new Card(0,i,colors[j-1]);
                }
            }
        }
        deck[40] = new Card(20, 20, "Rook");
        add += ", deck created";
    }
    public void shuffle(){
        for(int i = 0; i < 1000; i++){
            Random spot = new Random();
            //temp1 and temp 2 represent the indexes of the cards
            int num1 = spot.nextInt(41), num2 = spot.nextInt(41);
            Card holderCard = new Card(deck[num1]);//create a copy of a card as a place holder
            deck[num1] = new Card(deck[num2]);//swaps the cards
            deck[num2] = holderCard;
        }
        add += ", shuffled";
        //chooses randomly what cards change spots and does it a bunch of times
    }
    public void dealHands(){
        //player hands are [4 players ][9 cards per player]
        int deckNumberToHand = 0;
        for(int i = 0; i <= 4; i++){//4 players + 1 nest
            for(int j = 0; j <= 8; j++){
                if(i == 4 && j > 4){//the nest does not need all 9 cards only 5 last 4 will be null
                    playerHands[i][j] = null;}
                else {
                    playerHands[i][j] = deck[deckNumberToHand];
                    deckNumberToHand++;}
            }
        }
        add += " ,hands have been dealt";
    }
    public void printDeck(){
        for(int i =0; i < deck.length; i++ ){
            add += " ,index:" + i + " ,number:" + deck[i].getNum() + " ,suit:" + deck[i].getCardSuit() + " ,cardVal:" + deck[i].getCardVal() ;
        }
    }


    /**
     *
     * when player wins nest, they can discard
     */
    public boolean discardCard(DiscardingAction action){
        if(!bidPhase ||  playerId != action.getPlayer().getPlayerNum() || bidWinner == 4) {
            return false;
        }

        bidPhase = false;//ask nux
        return true;
    }


    /**
     *
     * when its the bidding round and their turn to bid
     */
    public boolean bid(BidAction action){
        if(!bidPhase ||  playerId != action.getPlayer().getPlayerNum() || action.getPlayer().getCanBid() == false){//1 needs to be replaced by the player who's turn it is
            return false;
        }
        bidNum = action.totalBid;
        //add += " bid";
        return true;
    }

    /**
     *
     * if they want to pass on a bid turn
     */
    public boolean passTurn(PassingAction action){
        if((!bidPhase) ||  playerId != action.getPlayer().getPlayerNum() || action.getPlayer().getCanBid() == false){
            return false;
        }
        return true;
    }


    public boolean playCard(PlayCardAction action){
        if(!bidPhase || playerId != action.getPlayer().getPlayerNum()) { //remove ! from bidPhase when developing game
            return false;
        }
        //roundScore += card.getCardVal();

        return true;
    }
}
