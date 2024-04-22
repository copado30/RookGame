package edu.up.cs301.rook;

import edu.up.cs301.GameFramework.Card;
import edu.up.cs301.GameFramework.actionMessage.GameAction;
import edu.up.cs301.GameFramework.players.GamePlayer;

public class PlayCardAction extends GameAction {
    private final Card card; //card being played
    private int cardIndex;

    /**
     * constructor for GameAction
     *
     * @param player the player who created the action
     * @param card the card being played
     */

    public PlayCardAction(GamePlayer player, Card card, int cardIndex) {
        super(player);
        this.card = card;
        this.cardIndex = cardIndex;
    }

    public Card getCard() { //returns card being played
        return card;
    }
    public int getCardIndex() {return this.cardIndex;}

}
