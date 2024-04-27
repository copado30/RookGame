package edu.up.cs301.rook;

import edu.up.cs301.GameFramework.Card;
import edu.up.cs301.GameFramework.actionMessage.GameAction;
import edu.up.cs301.GameFramework.infoMessage.GameState;
import edu.up.cs301.GameFramework.players.GamePlayer;

import java.io.Serializable;
import java.util.*;


public class RookState extends GameState implements Serializable {
    public static final int BID_PHASE = 37;  //bidding
    public static final int DISCARD_PHASE = 40;  //bidding
    public static final int PLAY_PHASE = 38; //play cards on trick
    public static final int TRUMP_PHASE = 41; //trump suit selection
    public static final int ACK_PHASE = 39;  //acknowledge completed trick
    int discardCount;
    public int team1Score;
    public int team2Score;
    public int bidNum;
    public int playerId;
    public int roundScoreTeam1;
    public int roundScoreTeam2;
    public int trickCount;
    private int phase;
    public int bidWinner;
    public boolean bidEnd;
    public int roundsPlayed;
    public int ackCount;  //how many players have acknowledged the current trick

    private boolean[] canBid = new boolean[4];
    public boolean[] wonBid = new boolean[4];
    public Card[] cardsPlayed = new Card[4];
    public Card[] discardedCards = new Card[5];
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
        bidNum = 0;
        discardCount = 0;
        playerId = 0;
        trickCount = 0;
        roundsPlayed = 0;
        trumpSuit = null;
        leadingSuit = null;//can give it a default value if necessary
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
        discardCount = gameState.discardCount;
        roundScoreTeam1 = gameState.roundScoreTeam1;
        roundScoreTeam2 = gameState.roundScoreTeam2;
        bidWinner = gameState.bidWinner;
        phase = gameState.phase;
        trumpSuit = gameState.trumpSuit;
        leadingSuit = gameState.leadingSuit;
        trickCount = gameState.trickCount;
        ackCount = gameState.ackCount;
        roundsPlayed = gameState.roundsPlayed;

        for(int i = 0; i < discardedCards.length; i++) { discardedCards[i] = new Card (gameState.discardedCards[i]); }

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
     * when player wins nest, they can discard up to 5 cards
     * makes sure the discarding action is legal, used in the RookLocalPLayer
     * @param action - the discarding action it is checking
     *@return whether the move was legal or not
     */
    public boolean discardCard(DiscardingAction action){
        if( (phase != DISCARD_PHASE) ||  playerId != action.getPlayer().getPlayerNum() || bidWinner == 4) {
            return false;
        }

        return true;
    }


    /**
     * During the Bid Phase the players will send bid actions this method
     * makes sure that the bid action is legal
     * @param action - the discarding action it is checking
     * @return whether the move was legal or not
     */
    public boolean bid(BidAction action){
        boolean bidIsLegal;
        if(bidNum >= action.getTotalBid() ){
           bidIsLegal = false;
        } else { bidIsLegal = true; }

        if(phase != BID_PHASE ||  playerId != action.getPlayer().getPlayerNum() || canBid[playerId] == false || !bidIsLegal){//1 needs to be replaced by the player who's turn it is
            return false;
        }
        return true;
    }

    /**
     * Passes the turn during the bidding phase
     *
     * @param action
     * @return True if the pass is lega, false otherwise
     */
    public boolean passTurn(PassingAction action){
        if((phase != BID_PHASE) ||  playerId != action.getPlayer().getPlayerNum() || canBid[playerId] == false){
            return false;
        }
        return true;
    }

    /**
     * Plays a card during the play phase
     *
     * @param action
     * @return True if the action is legal and the card can be played, false otherwise
     */
    public boolean playCard(PlayCardAction action){
        if(phase != PLAY_PHASE || playerId != action.getPlayer().getPlayerNum()) {
            return false;
        }
        return true;
    }


    /**
     * creates the deck of cards for the game
     */
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

    /**
     *  Shuffles the deck of cards
     */
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

    /**
     * Deals hands to players and the nest
     */
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

    /**
     * Determines the winner of a trick based on the cards played.
     * @return The index of the winning player
     */
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

    /**
     * Resets arrays used to track game state.
     */
    public void resetArrays(){
        for(int i = 0; i < canBid.length; i++){canBid[i] = true;}
        for(int i = 0; i < wonBid.length; i++){wonBid[i] = false;}
        for(int i = 0; i < cardsPlayed.length; i++){cardsPlayed[i] = null;}
        for(int i = 0; i < trickWinner.length; i++){trickWinner[i] = 4;}
        for(int i = 0; i < discardedCards.length; i++){discardedCards[i] = null;}
    }

    /**
     * Adds points from the nest to the winning team's score
     */
    public void addNest(){
        int nestVal = 0;
        for(int i = 0; i < 5; i++){nestVal += playerHands[4][i].getCardVal();}

        if(winner() == 0 || winner() == 2){//player 0 or 2 won then add to team 1
            team1Score += nestVal;
        } else if(winner() == 1 || winner() == 3){//player 1 or 3 then add to team 2
            team2Score += nestVal;
        }
    }

    /**
     * Removes bid score from team's score if not reached.
     */
    public void removeBidScore() {
        // if they don't reach the points bid by end of round, remove from their teams score
        if (bidWinner == 0 || bidWinner == 2) {
            if (roundScoreTeam1 < bidNum) {
                team1Score = team1Score - roundScoreTeam1 - bidNum;
            } else {
                if (roundScoreTeam2 < bidNum) {
                    team2Score = team2Score - roundScoreTeam1 - bidNum;
                }
            }
        }
    }

    /**
     * Resets the game state for a new round.
     */
    public void resetRound(){
        removeBidScore();

        //before the thing gets reset add the nest to the winning teams score
        shuffle();
        dealHands();
        phase = BID_PHASE;
        trickCount = 0;
        ackCount = 0;
        discardCount = 0;
        playerId = 0;
        bidNum = 0;
        leadingSuit = null;
        roundScoreTeam1 = 0;
        roundScoreTeam2 = 0;

        resetArrays();

    }//resetRound

    /**
     * Chccks if the bidding phase is over and determines the bid winner.
     * @return True if the bidding phase is over, false otherwise.
     */
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

    /**
     * Gets the current bid number.
     * @return The current bid number
     */
    public int getBidNum() {
        return this.bidNum;
    }

    /**
     * Sets the bid number to the given value.
     * @param bidNum The new bid number to set.
     */
    public void setBidNum(int bidNum) {
        this.bidNum = bidNum;
    }

    /**
     * Gets whether the given player can bid.
     *
     * @param playerId The ID of the player
     * @return True if the player can bid, false otherwise.
     */
    public boolean getCanBid(int playerId) {
        return canBid[playerId];
    }

    /**
     * Sets whether the given player can bid.
     * @param playerNumber
     * @param canBid
     */
    public void setCanBid(int playerNumber, boolean canBid) {
        this.canBid[playerNumber] = canBid;
    }

    /**
     * Checks if it's the bidding phase.
     * @return True if it's the bidding phase, false otherwise
     */
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

    public void scoreCalc(){
        //team 1 player 0, player 2
        //team 2 player 1, 3
        int scoreForRound = 0;

        for(int i = 0; i < cardsPlayed.length; i++){
            scoreForRound += cardsPlayed[i].getCardVal();
        }

        if(winner() == 0){
            team1Score += scoreForRound;
            roundScoreTeam1 += scoreForRound;
            trickWinner[trickCount - 1] = 0;
        }
        else if(winner() == 2){//player 0 or 2 won then add to team 1
            team1Score += scoreForRound;
            roundScoreTeam1 += scoreForRound;
            trickWinner[trickCount - 1] = 2;
        }
        //team 2 below
        else if(winner() == 3){//player 0 or 2 won then add to team 1
            team2Score += scoreForRound;
            roundScoreTeam2 += scoreForRound;
            trickWinner[trickCount - 1] = 3;

        }
        else if(winner() == 1 ){//player 1 or 3 then add to team 2
            trickWinner[trickCount - 1] = 1;
            roundScoreTeam2 += scoreForRound;
            team2Score += scoreForRound;
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

    /**
     * Counts discarded cards and moves to the next phase if necessary.
     */
    public void discardCardCount() {
        discardCount++;
        if (discardCount == 5) {
            discardCount = 0;
            phase = TRUMP_PHASE;
        }
    }


}
