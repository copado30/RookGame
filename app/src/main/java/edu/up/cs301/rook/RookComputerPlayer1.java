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
public class RookComputerPlayer1 extends GameComputerPlayer {

    /**
     * Constructor for objects of class RookComputerPlayer1
     * 
     * @param name
     * 		the player's name
     */
    public RookComputerPlayer1(String name) {
        // invoke superclass constructor
        super(name);
        
        // start the timer, ticking 20 times per second
        getTimer().setInterval(50);
        getTimer().start();
    }
    
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

        if(rookState.playerId == playerNum){ //if it's the bots turn

            if(rookState.isBidPhase()){//if its the bidPhase then skip
                PassingAction passingAction = new PassingAction(this);
                game.sendAction(passingAction);
            }
            else if (rookState.getPhase() == RookState.ACK_PHASE) {
                game.sendAction(new AcknowledgeTrick(this));
            }
            else if(rookState.getPhase() == RookState.PLAY_PHASE){//if its not the bid phase
                //what cards can they play
                ArrayList<Integer> leadingSuitCards =  new ArrayList<Integer>();//contains indexes of the leadingSuitCards in the players hand
                ArrayList<Integer> trumpSuitCards =  new ArrayList<Integer>();//

                for(int i = 0; i < 9; i++) {//see where the leading suits and the rook card is if they have one
                    if(rookState.playerHands[playerNum][i].getCardSuit() == null){
                        //do nothing
                    }else if(rookState.playerHands[playerNum][i].getCardSuit() == rookState.leadingSuit
                            || rookState.playerHands[playerNum][i].getCardSuit() == "Rook"){
                        leadingSuitCards.add(i);
                    }else if(rookState.playerHands[playerNum][i].getCardSuit() == rookState.trumpSuit){
                        trumpSuitCards.add(i);
                    }

                }//card indexes for loop

                Random random = new Random();
                int cardIndex;

                if(!leadingSuitCards.isEmpty()) {// if they have a leading suit card then play one
                    cardIndex = leadingSuitCards.get(random.nextInt(leadingSuitCards.size()));//get a random number from the array and store it
                    PlayCardAction pca = new PlayCardAction(this, rookState.playerHands[playerNum][cardIndex], cardIndex);
                    game.sendAction(pca);
                    leadingSuitCards.remove((Integer)cardIndex);
                }else if(!trumpSuitCards.isEmpty()){
                    cardIndex = trumpSuitCards.get(random.nextInt(trumpSuitCards.size()));
                    PlayCardAction pca = new PlayCardAction(this, rookState.playerHands[playerNum][cardIndex], cardIndex);
                    game.sendAction(pca);
                    trumpSuitCards.remove((Integer)cardIndex);
                } else {
                    //boolean empty = true;
                    for(int i = 0; i < 9; i++) {
                        if(rookState.playerHands[playerNum][i].getCardSuit() != null) {//if the slot is not null
                            PlayCardAction pca = new PlayCardAction(this, rookState.playerHands[playerNum][i], i);
                            game.sendAction(pca);
                            //empty = false;
                            break;
                        }
                    }
                }
            }
        }//if its the players turn
        //sleep(0.5);
    }

}
