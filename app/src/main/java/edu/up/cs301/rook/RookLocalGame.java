package edu.up.cs301.rook;

import edu.up.cs301.GameFramework.infoMessage.GameState;
import edu.up.cs301.GameFramework.players.GamePlayer;
import edu.up.cs301.GameFramework.LocalGame;
import edu.up.cs301.GameFramework.actionMessage.GameAction;
import edu.up.cs301.GameFramework.players.ProxyPlayer;

import android.util.Log;

/**
 * BASE CODE: A class that represents the state of a game. In our game, the only
 * relevant piece of information is the value of the game's counter. The
 * CounterState object is therefore very simple.
 *
 * ROOK: Sends all actions: bidAction, PlayCardAction, PassingAction, DiscardingAction, and TrumpSelection
 * Includes helper methods for makeMove method responding to these actions
 *
 * @author Steven R. Vegdahl
 * @author Andrew M. Nuxoll
 * @author Rafael Copado
 * @author Shubu Aryal
 * @author Carolyn Sousa
 * @version July 2013
 */
public class RookLocalGame extends LocalGame {
    private RookState rookState; // the game's state

    /**
     * can this player move
     *
     * @return true, because all player are always allowed to move at all times,
     * as this is a fully asynchronous game
     */
    @Override
    protected boolean canMove(int playerIdx) {
        return true;
    }

    /**
     * This ctor should be called when a new rook game is started
     */
    public RookLocalGame(GameState state) {
        // initialize the game state, with the counter value starting at 0
        if (!(state instanceof RookState)) {
            state = new RookState();//was RookState(0)
        }
        this.rookState = (RookState) state;
        super.state = state;
    }

    /**
     * Types of GameAction is BidAction, PassingAction, PlayCardAction, DiscardingAction,
     * and TrumpSelection
     *
     * @param action
     */
    @Override
    protected boolean makeMove(GameAction action) {
        Log.i("action", action.getClass().toString());

        // need to check if the action is a game action first make a return false if statement
        if(rookState.trickCount == 0 && rookState.playerId == 0){
            correctPlayerID();
        }

        int playerNum = action.getPlayer().getPlayerNum();

        if (action instanceof BidAction) {
            BidAction ba = (BidAction)action;
            if(rookState.bid(ba)){ // if the action is legal
                rookState.setBidNum(ba.getTotalBid()); // make the rookState bidNum equal to the
                nextBidderID(playerNum);

                if(ba.getTotalBid() == 120) { // if the player bids 120 they win it right away
                    rookState.bidWinner = playerNum;
                    rookState.wonBid[playerNum] = true;
                    rookState.setPhase(RookState.DISCARD_PHASE);
                }
                return true;
            }
        } else if (action instanceof PassingAction) {
            PassingAction pa = (PassingAction)action;

            if(rookState.passTurn(pa)){ // if they can pass then do the following
                rookState.setCanBid(playerNum, false); // player can no longer bid
                if(rookState.isBiddingOver()){
                    rookState.setPhase(RookState.DISCARD_PHASE);
                }
                nextBidderID(playerNum);
                return true; // action was successful
            }
        } else if (action instanceof PlayCardAction) {
            PlayCardAction pca = (PlayCardAction) action;

            if(pca.getCard().getCardSuit() == null) {
                // do nothing, will return false at the end
            } else if (rookState.playCard(pca)) {
                // add the card the player played to the cards played array
                rookState.cardsPlayed[playerNum] = rookState.playerHands[playerNum][pca.getCardIndex()];
                rookState.playerHands[playerNum][pca.getCardIndex()] = null; // delete the card from the players hand
                changePlayerTurn(playerNum);

                try {
                    rookState.leadingSuit = rookState.cardsPlayed[firstPlayerOfTrick()].getCardSuit();
                } catch(NullPointerException npe) {
                    int wtf = 3;
                }

                if(playerNum == lastPlayerOfTrick()){ // if its the player that should go last
                    rookState.trickCount++;
                    rookState.scoreCalc();
                    rookState.playerId = firstPlayerOfTrick(); // make the winner of the bid the player that goes first
                    rookState.setPhase(RookState.ACK_PHASE);
                }
                if(rookState.trickCount == 9){ // if last winner of round, add Nest to their score and reset
                    rookState.addNest();
                    try {
                        Thread.sleep(800);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    rookState.resetRound();
                }
                return true;
            }
        } else if (action instanceof AcknowledgeTrick) { // acknowledges end of subround to reset
            if (rookState.getPhase() == RookState.ACK_PHASE) {
                rookState.ackTrick();
                changePlayerTurn(playerNum);
                return true;
            }
        } else if (action instanceof DiscardingAction) { // trading cards with the nest
            DiscardingAction discardingAction = (DiscardingAction) action;
            if(rookState.discardCard(discardingAction)) {
                int index = ((DiscardingAction) action).getDiscardedIndex();
                if(index == -1) {
                    rookState.setPhase(rookState.TRUMP_PHASE);
                    return true;
                }
                rookState.playerHands[playerNum][index] = rookState.playerHands[4][rookState.discardCount];
                rookState.discardCardCount();
                return true;
            }
        } else if (action instanceof TrumpSelection) { // if they win bid, they select trump suit
            rookState.trumpSuit = rookState.playerHands[playerNum][((TrumpSelection) action).index].getCardSuit();
            rookState.setPhase(RookState.PLAY_PHASE);
            rookState.playerId = 0;
            return true;
        }
        return false;
    }//makeMove

    /**
     * Assigns correct playerID to proxy player. Was not being recognized by program.
     *
     * Met with Nuxoll to resolve
     */
    public void correctPlayerID(){
        for(int i = 0; i < players.length; i++) {
            if (players[i].getPlayerNum() == -9999) { // if they are a proxy players
                players[i].setPlayerNum(i); // set the proxy players number to their index in the array
            }
        } // first for loop
    }


    /**
     * Makes it the next player turn, if its the last player in the subround it resets
     *
     * @param currentPlayer
     */
    public void changePlayerTurn(int currentPlayer){
        if(currentPlayer < 3){
            rookState.playerId++; // make it the next persons turn
        } else {
            rookState.playerId = 0; // Start over with player 0 since it was player 3's turn.
        }
    }

    /**
     * Returns the player id of the next bidder
     *
     * @param currentPlayer
     */
    public void nextBidderID(int currentPlayer){
        if(currentPlayer == 3) {
            for (int i = 0; i < players.length; i++){
                if(rookState.getCanBid(i)){
                    rookState.playerId = i;
                    return;
                }
            }
        }

        for (int i = currentPlayer + 1; i < players.length; i++){
            if(rookState.getCanBid(i)){
                rookState.playerId = i;
                return;
            }
        }
    }

    /**
     * Returns the player id of the last player to play a card in the trick. Used to check
     * when the trick is over in the PlayCardAction
     */
    public int lastPlayerOfTrick(){
        if(rookState.trickCount != 0){ // if its not the zero trick
            // if the zero player wins then do nothing nothing cause that is the default
            if(rookState.trickWinner[rookState.trickCount -1] == 1){ // winner of the last trick
                return 0;
            } else if(rookState.trickWinner[rookState.trickCount -1] == 2){ // winner of the last trick
                return 1;
            }
            else if(rookState.trickWinner[rookState.trickCount -1] == 3){ // winner of the last trick
                return 2;
            }
        }
        return 3; // default is player 3
    }

    /**
     * Uses who went last to decide who goes first for the next trick.
     * Calls lastPlayerOfTrick method
     */
    public int firstPlayerOfTrick(){
        if(lastPlayerOfTrick() == 0){
            return 1;
        } else if (lastPlayerOfTrick() == 1) {
            return 2;
        }else if (lastPlayerOfTrick() == 2) {
            return 3;
        }
        return 0; // if player 3 went last, then player zero goes first
    }


    /**
     * send the updated state to a given player
     *
     * @param p
     */
    @Override
    protected void sendUpdatedStateTo(GamePlayer p) {
        // this is a perfect-information game, so we'll make a
        // complete copy of the state to send to the player
        RookState state = new RookState(this.rookState);
        p.sendInfo(state);
    } // sendUpdatedState

    /**
     * Check if the game is over. It is over, return a string that tells
     * who the winner(s), if any, are. If the game is not over, return null;
     *
     * @return a message that tells who has won the game, or null if the
     * game is not over
     */
    @Override
    protected String checkIfGameOver() {
        if(rookState.trickCount == 9){
            rookState.resetRound();
        }

        if(rookState.team1Score >= 300 && rookState.team1Score > rookState.team2Score) {
            return "Team 1 has won the game with " + rookState.team1Score + " points";
        } else if (rookState.team2Score >= 300 && rookState.team1Score < rookState.team2Score) {
            return "Team 2 has won the game with " + rookState.team2Score + " points";
        }

        return null;
    }
}// class RookLocalGame
