package edu.up.cs301.rook;

import java.io.Serializable;

import edu.up.cs301.GameFramework.actionMessage.GameAction;
import edu.up.cs301.GameFramework.players.GamePlayer;

public class DiscardingAction extends GameAction implements Serializable {
    /**
     * constructor for GameAction
     *
     * @param player the player who created the action
     */

    public int index;

    public DiscardingAction(GamePlayer player, int index) {
        super(player);
        this.index = index;
    }

    public int getDiscardedIndex() {
        return index;
    }

}
