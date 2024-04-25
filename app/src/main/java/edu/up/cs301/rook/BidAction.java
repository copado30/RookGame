package edu.up.cs301.rook;

import java.io.Serializable;

import edu.up.cs301.GameFramework.actionMessage.GameAction;
import edu.up.cs301.GameFramework.players.GamePlayer;

public class BidAction extends GameAction implements Serializable {
    /**
     * constructor for GameAction
     *
     * @param player the player who created the action
     */
    private int totalBid = 0;

    public BidAction(GamePlayer player, int newValue) {
        super(player);
        this.totalBid = newValue;
    }

    public int getTotalBid() {return totalBid;}
    public void setTotalBid(int total) {this.totalBid = total;}
}


