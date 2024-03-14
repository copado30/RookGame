package edu.up.cs301.rook;

import edu.up.cs301.GameFramework.infoMessage.GameState;


/**
 * This contains the state for the Counter game. The state consist of simply
 * the value of the counter.
 *
 * @author Steven R. Vegdahl
 * @version July 2013
 */


public class RookState extends GameState {
    private int team1Score;
    private int team2Score;
    private int player1Score;
    private int player2Score;
    private int player3Score;
    private int player4Score;
    private int bidNum;
    private int playerId;

    public RookState() {

    }

    public RookState(int i) {

    }

    public RookState(RookState gameState) {
        this.team1Score = gameState.team1Score;
        this.team2Score = gameState.team2Score;
        this.player1Score = gameState.player1Score;
        this.player2Score = gameState.player2Score;
        this.player3Score = gameState.player3Score;
        this.player4Score = gameState.player4Score;
        this.bidNum = gameState.bidNum;
        this.playerId = gameState.playerId;

    }

    @Override
    public String toString() {
        if (team1Score > team2Score) {
            return "It is player " + playerId + " turn. Team 1 is in the lead with: " + team1Score;
        } else if (team2Score > team1Score) {
            return "It is player " + playerId + " turn. Team 2 is in the lead with: " + team2Score;
        }
        return "It is player " + playerId + " turn. Teams are tied.";
    }
}
