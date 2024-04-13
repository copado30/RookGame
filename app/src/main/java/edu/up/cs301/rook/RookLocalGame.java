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
     * This ctor should be called when a new counter game is started
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
     * The only type of GameAction that should be sent is CounterMoveAction
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
                return true;
            }
        } else if (action instanceof PassingAction) {
            PassingAction pa = (PassingAction)action;
            if(rookState.passTurn(pa)){//if they can  pass then do the following
                rookState.setCanBid(action.getPlayer().getPlayerNum(), false);//player can no longer bid
                changePlayerTurn(playerNum);
                return true;//action was successful
            }
        } else if (action instanceof PlayCardAction) {
            PlayCardAction pca = (PlayCardAction) action;
            if (rookState.playCard(pca)) {
                rookState.cardsPlayed[playerNum] = rookState.playerHands[playerNum][pca.getCardIndex()];
                //put card into cards played array(line above) and then remove from the players hand(line below)
                rookState.playerHands[playerNum][pca.getCardIndex()] = null;
                changePlayerTurn(playerNum);
                return true;
            }
        }
        return false;
    }//makeMove

    public void changePlayerTurn(int currentPlayer){
        if(currentPlayer < 3){
            rookState.playerId++;//make it the next persons turn
        } else {
            rookState.playerId = 0; // Start over with player 0 since it was player 3's turn.
            scoreCalc(); // Calculate scores at the end of the round.
            //clearPlayedCards();
        }
    }

    public void clearPlayedCards() {
        for (int i = 0; i < rookState.cardsPlayed.length; i++) {
            rookState.cardsPlayed[i] = null; // Clear each card.
        }
    }

    public void scoreCalc(){
        //team 1 player 0, player 2
        //team 2 player 1, 3
        int scoreForRound = 0;

        for(int i = 0; i < rookState.cardsPlayed.length; i++){
            scoreForRound += rookState.cardsPlayed[i].getCardVal();
        }
        if(winner() == 0 || winner() == 2){//player 0 or 2 won then add to team 1
            rookState.team1Score += scoreForRound;
        }
        else if(winner() == 1 || winner() == 3){//player 1 or 3 then add to team 2
            rookState.team2Score += scoreForRound;
        }
        else{
            //don't add cause both teams suck
        }
    }

    public int winner(){
        int winningSuitPlayer = 0, winningTrumpPlayer = 0, winningSuitNum = 0, winningTrumpNum = 0;


        for(int i = 0; i < rookState.cardsPlayed.length; i++){
            if(rookState.cardsPlayed[i].getCardSuit() == "Rook"){
                return i;
            } else if (rookState.cardsPlayed[i].getCardSuit().equals(rookState.trumpSuit)) {
                if(rookState.cardsPlayed[i].getNum() > winningTrumpNum) {
                    winningTrumpNum = rookState.cardsPlayed[i].getNum();
                    winningTrumpPlayer = i;
                }
            } else if(rookState.cardsPlayed[i].getCardSuit().equals(rookState.leadingSuit)){
                if(rookState.cardsPlayed[i].getNum() > winningSuitNum) {
                    winningSuitNum = rookState.cardsPlayed[i].getNum();
                    winningSuitPlayer = i;
                }
            }
        }
        if(winningTrumpNum != 0){
            return winningTrumpPlayer;
        }
        else if(winningSuitNum != 0){
            return winningSuitPlayer;
        }

        return -7;//no one won
    }

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

        // get the value of the counter
//		int counterVal = this.rookState.getCounter();
//
//		if (counterVal >= TARGET_MAGNITUDE) {
//			// counter has reached target magnitude, so return message that
//			// player 0 has won.
//			return playerNames[0]+" has won.";
//		}
//		else if (counterVal <= -TARGET_MAGNITUDE) {
//			// counter has reached negative of target magnitude; if there
//			// is a second player, return message that this player has won,
//			// otherwise that the first player has lost
//			if (playerNames.length >= 2) {
//				return playerNames[1]+" has won.";
//			}
//			else {
//				return playerNames[0]+" has lost.";
//			}
//		}else {
//			// game is still between the two limit: return null, as the game
//			// is not yet over
        return null;
//		}

    }

}// class CounterLocalGame
