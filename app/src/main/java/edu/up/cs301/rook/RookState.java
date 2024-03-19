package edu.up.cs301.rook;

import edu.up.cs301.GameFramework.Card;
import edu.up.cs301.GameFramework.infoMessage.GameState;




public class RookState extends GameState {
    private int team1Score;
    private int team2Score;
    private int player1Score;
    private int player2Score;
    private int player3Score;
    private int player4Score;
    private int bidNum;
    public int playerId;
    private boolean bidPhase = true;
    private boolean bidWinner = false;
    public Card[] deck = new Card[41];

    public Card[] hand1 = new Card[9];

    public Card[] hand2 = new Card[9];

    public Card[] hand3 = new Card[9];

    public Card[] hand4 = new Card[9];

    public RookState() {
        team1Score = 0;
        team2Score = 0;
        player1Score = 0;
        player2Score = 0;
        player3Score = 0;
        player4Score = 0;
        bidNum = 0;
        playerId = 1;
    }

    public RookState(int i) {

    }

    public RookState(RookState gameState) {
        this.team1Score = gameState.team1Score;
        this.team2Score = gameState.team2Score;
        this.player1Score = gameState.player1Score;
        this.player2Score = gameState.player2Score;
        this.player3Score = gameState.player3Score;
        this.player4Score = gameState.player4Score;
        this.bidNum = gameState.bidNum;
        this.playerId = gameState.playerId;

    }

    @Override
    public String toString() {
        if (team1Score > team2Score) {
            return "It is player " + playerId + " turn. Team 1 is in the lead with: " + team1Score;
        } else if (team2Score > team1Score) {
            return "It is player " + playerId + " turn. Team 2 is in the lead with: " + team2Score;
        }
        return "It is player " + playerId + " turn. Teams are tied.";
    }



    public void shuffle(){
        for(int i = 0; i < deck.length; i++)
            if(i < 4){
                deck[i].setNum(5);
            }
            else if (i < 8){
                deck[i].setNum(6);
            }
            else if (i < 12){
                deck[i].setNum(7);
            }
            else if (i < 16){
                deck[i].setNum(8);
            }
            else if (i < 20){
                deck[i].setNum(9);
            }
            else if (i < 24){
                deck[i].setNum(10);
            }
            else if (i < 28){
                deck[i].setNum(11);
            }
            else if (i < 32){
                deck[i].setNum(12);
            }
            else if (i < 36){
                deck[i].setNum(13);
            }
            else if (i < 40){
                deck[i].setNum(14);
            }
    }


    /**
     *
     * when player wins nest, they can discard
     */
    public boolean discardCard(int index){
        if((!bidPhase || !(playerId == 1)) && !bidWinner) {
            return false;
        }
        bidPhase = false;
        return true;
    }


    /**
     *
     * when its the bidding round and their turn to bid
     */
    public boolean bid(){
        if(!bidPhase || !(playerId == 1)) {//1 needs to be replaced by the player who's turn it is
            return false;
        }
        return true;
    }

    /**
     *
     * if they want to pass on a bid turn
     */
    public boolean passTurn(int playerNum){
        if((!bidPhase) || (playerNum != playerId)){
            return false;
        }
        return true;
    }


    public boolean playCard(int playerNum){
        if(bidPhase || playerNum != playerId) {
            return false;
        }
        return true;
    }
}
