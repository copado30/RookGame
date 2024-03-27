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


    public void createDeck(){
        String[] colors = new String[]{"Black","Green","Yellow","Red"};

        for(int j = 1; j <= colors.length; j++){
            for(int i = 4; i < 14; i++){
                deck[i-4].setNum(i);
                deck[i-4].setCardSuit(colors[j]);

                if(i == 5){deck[i*j].setCardVal(5);}
                else if(i == 10 || i == 14){deck[i*j].setCardVal(10);}
            }
        }
        deck[41].setCardSuit("Rook");
        deck[41].setNum(20);
        deck[41].setCardVal(20);
    }
    public void shuffle(){



    }


    /**
     *
     * when player wins nest, they can discard
     */
    public boolean discardCard(DiscardingAction action){
        if(!bidPhase ||  playerId != action.getPlayerNum() || !bidWinner) {
            return false;
        }

        bidPhase = false;
        return true;
    }


    /**
     *
     * when its the bidding round and their turn to bid
     */
    public boolean bid(BidAction action){
        if(!bidPhase ||  playerId != action.getPlayerNum()) {//1 needs to be replaced by the player who's turn it is
            return false;
        }
        return true;
    }

    /**
     *
     * if they want to pass on a bid turn
     */
    public boolean passTurn(PassingAction action){
        if((!bidPhase) ||  playerId != action.getPlayerNum()){
            return false;
        }
        return true;
    }


    public boolean playCard(PlayCardAction action){
        if(bidPhase || playerId != action.getPlayerNum()) {
            return false;
        }
        return true;
    }
}
