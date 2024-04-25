package edu.up.cs301.rook;

import edu.up.cs301.GameFramework.Card;
import edu.up.cs301.GameFramework.infoMessage.GameState;

import java.io.Serializable;
import java.util.*;




public class RookState extends GameState implements Serializable {
    public static final int BID_PHASE = 37;  //bidding
    public static final int PLAY_PHASE = 38; //play cards on trick
    public static final int ACK_PHASE = 39;  //acknowledge completed trick
    public static final int DISCARD_PHASE = 40;  //bidding

    int temp;
    public int team1Score;
    public int team2Score;
    public int bidNum;
    public int playerId;
    public int roundScoreTeam1, roundScoreTeam2;
    public int trickCount;
    private int phase;
    public int bidWinner;
    public boolean bidEnd;
    public int ackCount;  //how many players have acknowledged the current trick

    private boolean[] canBid = new boolean[4];
    public boolean[] wonBid = new boolean[4];
    public Card[] cardsPlayed = new Card[4];
    public Card[] deck = new Card[41];
    public int[] trickWinner = new int[9];
    public Card[][] playerHands = new Card[5][9];

    public String trumpSuit, leadingSuit;


    public RookState() {
        team1Score = 0;
        team2Score = 0;
        roundScoreTeam1 = 0;
        roundScoreTeam2 = 0;
        bidWinner = 4;//players are 0-3, 4 means no one has won
        phase = BID_PHASE;
        bidNum = 70;
        playerId = 0;
        trickCount = 0;
        trumpSuit = "Red";
        leadingSuit = null;//can give it a default value if necessary
        ackCount = 0;
        resetArrays();
        createDeck();
        shuffle();
        dealHands();
    }

    public RookState(RookState gameState) {
        team1Score = gameState.team1Score;
        team2Score = gameState.team2Score;
        bidNum = gameState.bidNum;
        playerId = gameState.playerId;
        roundScoreTeam1 = gameState.roundScoreTeam1;
        roundScoreTeam2 = gameState.roundScoreTeam2;
        bidWinner = gameState.bidWinner;
        phase = gameState.phase;
        trumpSuit = gameState.trumpSuit;
        leadingSuit = gameState.leadingSuit;
        trickCount = gameState.trickCount;
        ackCount = gameState.ackCount;

        for(int i = 0; i < deck.length; i++) { deck[i] = new Card (gameState.deck[i]); }

        for(int i = 0; i <= 4; i++) {//4 players + 1 nest
            for (int j = 0; j <= 8; j++) { playerHands[i][j] = new Card(gameState.playerHands[i][j]);}
        }
        for(int i = 0; i < canBid.length; i++) { this.canBid[i] = gameState.canBid[i];}
        for(int i = 0; i < wonBid.length; i++) { this.wonBid[i] = gameState.wonBid[i];}

        for(int i = 0; i <cardsPlayed.length; i++) { this.cardsPlayed[i] = new Card(gameState.cardsPlayed[i]); }

    }//Deep Copy Constructor

    @Override
    public String toString() {
        if (team1Score > team2Score) {
            return "It is player " + playerId + " turn. Team 1 is in the lead with: " + team1Score ;
        } else if (team2Score > team1Score) {
            return "It is player " + playerId + " turn. Team 2 is in the lead with: " + team2Score;
        }
        return "It is player " + playerId + " turn. Teams are tied.";
    }

    /**
     *
     * when player wins nest, they can discard
     */
    public boolean discardCard(DiscardingAction action){
        if( (phase != DISCARD_PHASE) ||  playerId != action.getPlayer().getPlayerNum() || bidWinner == 4) {
            return false;
        }

        return true;
    }


    /**
     *
     * when its the bidding round and their turn to bid
     */
    public boolean bid(BidAction action){
        if(phase != BID_PHASE ||  playerId != action.getPlayer().getPlayerNum() || canBid[playerId] == false){//1 needs to be replaced by the player who's turn it is
            return false;
        }
        return true;
    }

    public boolean passTurn(PassingAction action){
        if((phase != BID_PHASE) ||  playerId != action.getPlayer().getPlayerNum() || canBid[playerId] == false){
            return false;
        }
        return true;
    }


    public boolean playCard(PlayCardAction action){
        if(phase != PLAY_PHASE || playerId != action.getPlayer().getPlayerNum()) {
            return false;
        }
        return true;
    }

    //^^ checking if the actions are legal moves
    public void createDeck(){
        String[] colors = new String[]{"Black","Green","Yellow","Red"};
        int newColorStart = 0; // where in array the new set of colored cards begins
        for(int j = 1; j <= colors.length; j++){//for loop for the suits
            if(j > 1){newColorStart += 10;}

            for(int i = 5; i < 15; i++){
                if(i == 5){deck[(i-5)+newColorStart] = new Card(5,i,colors[j-1]);}
                else if(i == 10 || i == 14){deck[(i-5)+newColorStart] = new Card(10,i,colors[j-1]);}
                else{
                    deck[(i-5)+newColorStart] = new Card(0,i,colors[j-1]);
                }
            }
        }
        deck[40] = new Card(20, 20, "Rook");
    }
    public void shuffle(){
        for(int i = 0; i < 1000; i++){
            Random spot = new Random();
            //temp1 and temp 2 represent the indexes of the cards
            int num1 = spot.nextInt(41), num2 = spot.nextInt(41);
            Card holderCard =  deck[num1];//create a copy of a card as a place holder
            deck[num1] = deck[num2];//swaps the cards
            deck[num2] = holderCard;
        }

        //chooses randomly what cards change spots and does it a bunch of times
    }
    public void dealHands(){
        //player hands are [4 players][9 cards per player]
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
    }
    public int winner(){
        int winningSuitPlayer = 0, winningTrumpPlayer = 0, winningSuitNum = 0, winningTrumpNum = 0, randomWin = 0, randomWinPlayer = 0;


        for(int i = 0; i < cardsPlayed.length; i++){
            if(cardsPlayed[i].getCardSuit() == "Rook"){
                return i;
            } else if (cardsPlayed[i].getCardSuit().equals(trumpSuit)) {
                if(cardsPlayed[i].getNum() > winningTrumpNum) {
                    winningTrumpNum = cardsPlayed[i].getNum();
                    winningTrumpPlayer = i;
                }
            } else if(cardsPlayed[i].getCardSuit().equals(leadingSuit)){
                if(cardsPlayed[i].getNum() > winningSuitNum) {
                    winningSuitNum = cardsPlayed[i].getNum();
                    winningSuitPlayer = i;
                }
            } else {
                if(cardsPlayed[i].getNum() > randomWin) {
                    randomWin = cardsPlayed[i].getNum();
                    randomWinPlayer = i;
                }
            }
        }
        if(winningTrumpNum != 0){
            return winningTrumpPlayer;
        }
        else if(winningSuitNum != 0){
            return winningSuitPlayer;
        } else {
            return randomWinPlayer;
        }
    }

    public int lastPlayerOfTrick(){
        if(trickCount != 0){//if its not the zero trick
            //if the zero player wins then do nothing nothing cause that is the default
            if(trickWinner[trickCount -1] == 1){//winner of the last trick
                return 0;
            } else if(trickWinner[trickCount -1] == 2){//winner of the last trick
                return 1;
            }
            else if(trickWinner[trickCount -1] == 3){//winner of the last trick
                return 2;
            }
        }
        return 3;//default is player 3
    }

    //using who went last decide who goes first for the next trick calls the lastPlayerOfTrickMethod
    public int firstPlayerOfTrick(){
        //timing seems to be off, probably because of trick count.

        if(lastPlayerOfTrick() == 0){
            return 1;
        } else if (lastPlayerOfTrick() == 1) {
            return 2;
        }else if (lastPlayerOfTrick() == 2) {
            return 3;
        }

        return 0;//if player 3 went last then player zero went first
    }

    public void scoreCalc(){
        //team 1 player 0, player 2
        //team 2 player 1, 3
        int scoreForRound = 0;

        for(int i = 0; i < cardsPlayed.length; i++){
            scoreForRound += cardsPlayed[i].getCardVal();
        }

        if(winner() == 0){
            team1Score += scoreForRound;//add it to the teams total
            roundScoreTeam1 += scoreForRound;//add it to the teams score for the round
            trickWinner[trickCount - 1] = 0;
        }
        else if( winner() == 2){//player 0 or 2 won then add to team 1
            team1Score += scoreForRound;
            roundScoreTeam1 += scoreForRound;//add it to the teams score for the round
            trickWinner[trickCount - 1] = 2;

        }
        //team 2 below
        else if( winner() == 3){//player 0 or 2 won then add to team 1
            team2Score += scoreForRound;
            roundScoreTeam2 += scoreForRound;//add it to the teams score for the round
            trickWinner[trickCount - 1] = 3;

        }
        else if(winner() == 1 ){//player 1 or 3 then add to team 2
            trickWinner[trickCount - 1] = 1;
            roundScoreTeam2 += scoreForRound;//add it to the teams score for the round
            team2Score += scoreForRound;
        }

    }

    //Method to check if the team that won the bid reached it
   /* public void reachedBidAmount(){
        if(bidWinner == 0 || bidWinner == 2){//if team1 won the bid
            if(roundScoreTeam1 >= bidNum){
                //do nothing they reached the bid
            }else{
                team1Score = team1Score - (bidNum + roundScoreTeam1);
            }
        }else if(bidWinner == 1 || bidWinner == 3){
            if(roundScoreTeam2 >= bidNum){
                //do nothing they reached the bid
            }else{
                team2Score -= (bidNum + roundScoreTeam1);
            }
        }
    }*/

    public int addNest(){
        int nestVal = 0;
        for(int i = 0; i < 5; i++){nestVal += playerHands[4][i].getCardVal();}

        if(winner() == 0 || winner() == 2){//player 0 or 2 won then add to team 1
            team1Score += nestVal;
        } else if(winner() == 1 || winner() == 3){//player 1 or 3 then add to team 2
            team2Score += nestVal;
        }
        return nestVal;
    }
    public void resetArrays(){
        for(int i = 0; i < canBid.length; i++){canBid[i] = true;}
        for(int i = 0; i < wonBid.length; i++){wonBid[i] = false;}
        for(int i = 0; i < cardsPlayed.length; i++){cardsPlayed[i] = null;}
        for(int i = 0; i < trickWinner.length; i++){trickWinner[i] = 4;}
    }

    public void resetRound(){
        //reachedBidAmount();//check if the team that won the bid reached that amount
        addNest();
        shuffle();
        dealHands();
        phase = BID_PHASE;
        trickCount = 0;
        playerId = 0;
        roundScoreTeam1 = 0;
        roundScoreTeam2 = 0;
        ackCount = 0;
        bidNum = 70;
        leadingSuit = null;
        resetArrays();

    }//resetRound
    public boolean isBiddingOver(){
        int passCount = 0;//how many people have passed
        for(int i = 0; i < canBid.length; i++){
            if(canBid[i] == false){
                passCount++;
            }
        }
        if(passCount == 3){
            for(int i = 0; i < canBid.length; i++){
                if(canBid[i] == true){
                    bidWinner = i;
                    wonBid[i] = true;
                    bidEnd = true;
                    return true;
                }
            }
        }
        return false;
    }

    public int getBidNum() {
        return this.bidNum;
    }

    public void setBidNum(int bidNum) {
        this.bidNum = bidNum;
    }

    public boolean getCanBid(int playerId) {
        return canBid[playerId];
    }
    public void setCanBid(int playerNumber, boolean canBid) {
        this.canBid[playerNumber] = canBid;
    }

    public boolean isBidPhase(){
        return (phase == BID_PHASE);
    }

    public void setPhase(int newPhase){
        this.phase = newPhase;
    }

    public int getPhase() {
        return this.phase;
    }

    //method to reset the 4 cards that are displayed in the middle
    public void clearPlayedCards() {
        for (int i = 0; i < cardsPlayed.length; i++) {
            cardsPlayed[i] = null; // Clear each card.
        }
    }



    /** user acknowledges current trick */
    public void ackTrick() {
        ackCount++;
        if (ackCount == 4) {
            ackCount = 0;
            clearPlayedCards();
            phase = PLAY_PHASE;
        }
    }

}