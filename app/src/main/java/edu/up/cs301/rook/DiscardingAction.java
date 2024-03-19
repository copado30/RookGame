package edu.up.cs301.rook;

import edu.up.cs301.GameFramework.actionMessage.GameAction;
import edu.up.cs301.GameFramework.players.GamePlayer;

public class DiscardingAction extends GameAction {
    /**
     * constructor for GameAction
     *
     * @param player the player who created the action
     */
    public DiscardingAction(GamePlayer player) {
        super(player);
    }
}
