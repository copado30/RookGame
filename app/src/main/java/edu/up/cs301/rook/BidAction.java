package edu.up.cs301.rook;

import edu.up.cs301.GameFramework.actionMessage.GameAction;
import edu.up.cs301.GameFramework.players.GamePlayer;

public class BidAction extends GameAction {
    /**
     * constructor for GameAction
     *
     * @param player the player who created the action
     */
    public int totalBid = 0;
    public BidAction(GamePlayer player, int increment) {
        super(player);
       totalBid += increment;
    }
}
