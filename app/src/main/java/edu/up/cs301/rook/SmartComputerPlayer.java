package edu.up.cs301.rook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import edu.up.cs301.GameFramework.Card;
import edu.up.cs301.GameFramework.players.GameComputerPlayer;
import edu.up.cs301.GameFramework.infoMessage.GameInfo;
import edu.up.cs301.GameFramework.utilities.Tickable;

/**
 * A computer-version of a counter-player.  Since this is such a simple game,
 * it just sends "+" and "-" commands with equal probability, at an average
 * rate of one per second.
 *
 * @author Steven R. Vegdahl
 * @author Andrew M. Nuxoll
 * @version September 2013
 */
public class SmartComputerPlayer extends GameComputerPlayer {

    /**
     * Constructor for objects of class CounterComputerPlayer1
     *
     * @param name
     * 		the player's name
     */
    String[] colors = new String[]{"Black","Green","Yellow","Red"};
    public SmartComputerPlayer(String name) {super(name);}

    /**
     * callback method--game's state has changed
     *
     * @param info
     * 		the information (presumably containing the game's state)
     */
    @Override
    protected void receiveInfo(GameInfo info)  {
        // Do nothing, as we ignore all state in deciding our next move. It
        // depends totally on the timer and random numbers.
        if(!(info instanceof RookState)){return;}

        RookState rookState = new RookState((RookState) info);

        if(rookState.playerId == playerNum){ // if it's the bots turn
            if(rookState.isBidPhase()){
                if(rookState.bidNum >= bidAmountCalc(rookState)){
                    // have they out bid us?
                    PassingAction passingAction = new PassingAction(this);
                    game.sendAction(passingAction);
                } else { // if not
                    BidAction bidAction = new BidAction(this,bidAmountCalc(rookState));
                    game.sendAction(bidAction);
                }
            } else if(rookState.getPhase() == rookState.TRUMP_PHASE) {
                // since the trumpSelection expects and index
                // hard coded the trump suit,phase change and player turn
                rookState.trumpSuit = selectTrumpSuit(rookState);
                rookState.setPhase(RookState.PLAY_PHASE);
                rookState.playerId = 0;
            } else if (rookState.getPhase() == RookState.ACK_PHASE) {
                game.sendAction(new AcknowledgeTrick(this));
            } else if(rookState.getPhase() == RookState.PLAY_PHASE){//if its not the bid phase
                // what cards can they play
                ArrayList<Integer> leadingSuitCards =  new ArrayList<Integer>();//contains indexes of the leadingSuitCards in the players hand
                ArrayList<Integer> trumpSuitCards =  new ArrayList<Integer>();//

                for(int i = 0; i < 9; i++) { // populate the arraylist
                    if(rookState.playerHands[playerNum][i].getCardSuit() == null){
                        // do nothing
                    } else if(rookState.playerHands[playerNum][i].getCardSuit() == rookState.leadingSuit
                            || rookState.playerHands[playerNum][i].getCardSuit() == "Rook"){
                        leadingSuitCards.add(i);
                    } else if(rookState.playerHands[playerNum][i].getCardSuit() == rookState.trumpSuit){
                        trumpSuitCards.add(i);
                    }

                } // card indexes for loop

                int cardIndex; // index of the card that will be played

                if(!leadingSuitCards.isEmpty()) { // if they have a leading suit card then play the highest one
                    cardIndex = highestOfColor(rookState, rookState.leadingSuit);
                    PlayCardAction pca = new PlayCardAction(this, rookState.playerHands[playerNum][cardIndex], cardIndex);
                    game.sendAction(pca);
                } else if(!trumpSuitCards.isEmpty()){
                    cardIndex = highestOfColor(rookState, rookState.trumpSuit);
                    PlayCardAction pca = new PlayCardAction(this, rookState.playerHands[playerNum][cardIndex], cardIndex);
                    game.sendAction(pca);
                } else {
                    for(int i = 0; i < 9; i++) {
                        if(rookState.playerHands[playerNum][i].getCardSuit() != null) { // if the slot is not null
                            PlayCardAction pca = new PlayCardAction(this, rookState.playerHands[playerNum][i], i);
                            game.sendAction(pca);
                            break;
                        }
                    }
                }
            }
        } // if its the players turn
    }

    public int highestOfColor(RookState rookState,String searchColor){
        // should return the index of the card that has the
        // highest cardNum of the searchColor
        int indexOfCardToPlay = -1;
        for(int i = 0; i < 9;i++){
            if(rookState.playerHands[playerNum][i].getCardSuit() == null){
                // do nothing we don't want a null card
            } else {
                if(rookState.playerHands[playerNum][i].getCardSuit().equals(searchColor)){
                    indexOfCardToPlay = i;
                }
            }
        }
        return indexOfCardToPlay;
    }

    public int bidAmountCalc(RookState rookState){
        // this method will calculate how much they can bid
        // based on their hand
        if(handQuality(rookState) <= 3 ) {
            return 75; // do not bid high because they have a bad hand
        } else if(handQuality(rookState) <= 5) {
            return 80; // can take more risk hand is pretty good
        } else {
            // if they have a really good hand
            return 90;
        }
        //bidding anything over 90 is hard to reach
    }

    public String selectTrumpSuit(RookState rookState){
        int dummy = -100;
        int arrayCountOfColor[] = new int[4];
        String returnString = "";
        for(int i = 0; i < 9; i++){
            if(rookState.playerHands[playerNum][i].getCardSuit() == null){
                // don't do anything card is empty(should not happen)
            }else if(rookState.playerHands[playerNum][i].getCardSuit().equals(colors[0])) {
                arrayCountOfColor[0]++;
            }else if(rookState.playerHands[playerNum][i].getCardSuit().equals(colors[1])) {
                arrayCountOfColor[1]++;
            }else if(rookState.playerHands[playerNum][i].getCardSuit().equals(colors[2])) {
                arrayCountOfColor[2]++;
            }else if(rookState.playerHands[playerNum][i].getCardSuit().equals(colors[3])) {
                arrayCountOfColor[3]++;
            }
        }
        // see which color they have the most of
        for(int i = 0; i < arrayCountOfColor.length; i++){
            if(arrayCountOfColor[i] > dummy){
                returnString = colors[i];
                dummy = arrayCountOfColor[i];
            }
        }
        return returnString;
    }
    // will return a number between 0 and nine
    // depending on how many good cards they have
    // good = cards that are worth points && cards with a higher number than 9
    public int handQuality(RookState rookState){
        int goodCardCount = 0;
        for(int i = 0; i < 9; i++) {
            if(rookState.playerHands[playerNum][i].getCardSuit() == null){
                //don't do anything card is empty(should not happen)
            } else if(rookState.playerHands[playerNum][i].getCardVal() > 0
                    || rookState.playerHands[playerNum][i].getNum() > 9) {
                goodCardCount++;
            }
            // don't do anything otherwise
        }
        return goodCardCount;
    }

    @Override
    public void setPlayerNum(int playerNum) {
        //ignore
    }
}