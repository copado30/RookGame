package edu.up.cs301.rook;

import edu.up.cs301.GameFramework.actionMessage.GameAction;
import edu.up.cs301.GameFramework.players.GamePlayer;

public class TrumpSelection extends GameAction {
    public int index;
    public TrumpSelection(GamePlayer player, int index) {
        super(player);
        this.index = index;
    }
}