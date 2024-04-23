package edu.up.cs301.rook;

import edu.up.cs301.GameFramework.infoMessage.GameState;
import edu.up.cs301.GameFramework.players.GamePlayer;
import edu.up.cs301.GameFramework.LocalGame;
import edu.up.cs301.GameFramework.actionMessage.GameAction;

import android.util.Log;

/**
 * A class that represents the state of a game. In our counter game, the only
 * relevant piece of information is the value of the game's counter. The
 * CounterState object is therefore very simple.
 *
 * @author Steven R. Vegdahl
 * @author Andrew M. Nuxoll
 * @version July 2013
 */
public class RookLocalGame extends LocalGame {

    // When a counter game is played, any number of players. The first player
    // is trying to get the counter value to TARGET_MAGNITUDE; the second player,
    // if present, is trying to get the counter to -TARGET_MAGNITUDE. The
    // remaining players are neither winners nor losers, but can interfere by
    // modifying the counter.
    public static final int TARGET_MAGNITUDE = 10;

    // the game's state
    private RookState rookState;

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
     * Types of GameAction is BidAction, PassingAction, PlayCardAction, and DiscardingAction
     */
    @Override
    protected boolean makeMove(GameAction action) {


        Log.i("action", action.getClass().toString());
        //need to check if the action is a game action  first make a return false if statement

        int playerNum = action.getPlayer().getPlayerNum();

        if (action instanceof BidAction) {
            BidAction ba = (BidAction)action;
            if(rookState.bid(ba)){//if the action is legal
                rookState.setBidNum(ba.getTotalBid());//make the rookState bidNum equal to the
                changePlayerTurn(playerNum);
                if(ba.getTotalBid() == 120) {//if the player bids 120 they win it right away
                    rookState.bidWinner = playerNum;
                    rookState.wonBid[playerNum] = true;
                    rookState.setPhase(RookState.PLAY_PHASE);//since a player won then it is no longer the bidPhase
                    rookState.playerId = 0;
                }
                return true;
            }
        } else if (action instanceof PassingAction) {
            PassingAction pa = (PassingAction)action;
            if(rookState.passTurn(pa)){//if they can  pass then do the following
                rookState.setCanBid(playerNum, false);//player can no longer bid
                if(rookState.isBiddingOver()){
                    rookState.setPhase(RookState.PLAY_PHASE);
                }
                changePlayerTurn(playerNum);
                return true;//action was successful
            }
        } else if (action instanceof PlayCardAction) {
            PlayCardAction pca = (PlayCardAction) action;

            if(pca.getCard().getCardSuit() == null){
                //do nothing, will return false at the end
            }
            else if (rookState.playCard(pca)) {
                if (playerNum == rookState.firstPlayerOfTrick()){
                    rookState.leadingSuit = pca.getCard().getCardSuit();
                }
                rookState.cardsPlayed[playerNum] = rookState.playerHands[playerNum][pca.getCardIndex()];//add the card the player played to the cards played array
                rookState.playerHands[playerNum][pca.getCardIndex()] = null;//delete the card from the players hand
                changePlayerTurn(playerNum);

                //TODO REMOVE DEBUG
                int fpot = rookState.firstPlayerOfTrick();
                if (rookState.cardsPlayed[fpot] == null
                        || rookState.cardsPlayed[fpot].getCardSuit() == null) {
                    Log.i("wtf", "CardSuit is NUll");
                }

                rookState.leadingSuit = rookState.cardsPlayed[rookState.firstPlayerOfTrick()].getCardSuit();




                if(playerNum == rookState.lastPlayerOfTrick()){//if its the player that should go last
                    rookState.trickCount++;
                    rookState.scoreCalc();
                    rookState.playerId = rookState.firstPlayerOfTrick();//make the winner of the bid the player that goes first
                    rookState.setPhase(RookState.ACK_PHASE);//each player does
                }
                return true;
            }
        } else if (action instanceof AcknowledgeTrick) {
            if (rookState.getPhase() == RookState.ACK_PHASE) {
                rookState.ackTrick();
                changePlayerTurn(playerNum);
                return true;
            }
        }
        return false;
    }//makeMove

    //isPlayCard is a boolean that lets us know if it is being called by PlayCardAction
    public void changePlayerTurn(int currentPlayer){
        if(currentPlayer < 3){
            rookState.playerId++;//make it the next persons turn
        } else {
            rookState.playerId = 0; // Start over with player 0 since it was player 3's turn.
        }
    }

    //The lastPlayerOfTrick method returns the player id of the last player to play a card in the trick
    //used to check when the trick is over in the PCA


    /**
     * send the updated state to a given player
     */
    @Override
    protected void sendUpdatedStateTo(GamePlayer p) {
        // this is a perfect-information game, so we'll make a
        // complete copy of the state to send to the player
        p.sendInfo(new RookState(this.rookState));

    }//sendUpdatedSate

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

}// class CounterLocalGame
