package edu.up.cs301.rook;

import android.app.Activity;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import edu.up.cs301.GameFramework.Card;
import edu.up.cs301.GameFramework.GameMainActivity;
import edu.up.cs301.GameFramework.infoMessage.GameInfo;
import edu.up.cs301.GameFramework.players.GameHumanPlayer;

public class RookHumanPlayer extends GameHumanPlayer implements View.OnClickListener {

    /* instance variables */

    // The TextView the displays the current counter value
    private TextView testResultsTextView;
    private TextView bidText;

    // the most recent game state, as given to us by the CounterLocalGame
    private RookState rookState;
    // the android activity that we are running
    private GameMainActivity myActivity;
    private Button passButton;
    private Button bidButton;
    private Button plusButton;
    private Button minusButton;

    MediaPlayer mp;

    private ImageButton[] cardButtons = new ImageButton[9];
    public ImageView[] playedCards = new ImageView[4];
    private TextView team1Score;
    private TextView team2Score;
    private TextView bidWinner;
    private TextView leadingSuit;
    private TextView trumpSuit;
    private TextView currPhase;
    private TextView playerTurn;

    /**
     * constructor
     *
     * @param name the player's name
     */
    public RookHumanPlayer(String name) {
        super(name);
    }


    /**
     * Returns the GUI's top view object
     *
     * @return the top object in the GUI's view heirarchy
     */
    public View getTopView() {
        return myActivity.findViewById(R.id.main_activity_GUI);
    }//was game_state_test_layout

    /**
     * sets the counter value in the text view
     */
    protected void updateDisplay() {
        // set the text in the appropriate widget

        Card[] myHand = rookState.playerHands[this.playerNum];
        for (int i = 0; i < myHand.length; i++) { //fix deck length
            int resId = getResourceIdForCard(myHand[i]);
            cardButtons[i].setImageResource(resId);
        }

        // display the cards in the trick
        for(int i = 0; i < rookState.cardsPlayed.length; i++) {
            int resId = getResourceIdForCard(rookState.cardsPlayed[i]);
            playedCards[i].setImageResource(resId);
        }

        team1Score.setText(rookState.team1Score + "");
        team2Score.setText(rookState.team2Score + "");


        if (rookState.getPhase() == rookState.ACK_PHASE) {
            currPhase.setText("  Phase: Acknowledge");
            bidWinner.setText("Press any button to continue.");
        } else if (rookState.getPhase() == rookState.TRUMP_PHASE) {
            bidWinner.setText("Select the trump suit by selecting one of the cards in your hand.");
            currPhase.setText("  Phase: Trump");
        } else {
            if(rookState.bidWinner  == 4){
                bidWinner.setText("No player has won bid is: " + rookState.getBidNum() + "  ");
            }else {
                bidWinner.setText("  Player " + (rookState.bidWinner + 1) + ": " + rookState.getBidNum() + "  ");
            }
        }
         if (rookState.getPhase() == rookState.BID_PHASE){
             currPhase.setText("  Phase: Bid");
        } else if (rookState.getPhase() == rookState.PLAY_PHASE){
             currPhase.setText("  Phase: Play");
        } else if (rookState.getPhase() == rookState.DISCARD_PHASE){
             if(rookState.bidWinner == playerNum) {
                 bidWinner.setText("You won the bid! Select up to 5 cards to trade with the nest.\n" +
                         "Press pass when you are done trading cards.");
             } else {
                 bidWinner.setText("Please wait for the bid winner to complete collecting their prize.");
             }
             currPhase.setText("  Phase: Discard");
        }


        leadingSuit.setText("  Leading: " + rookState.leadingSuit);
        trumpSuit.setText("  Trump: " + rookState.trumpSuit);


        playerTurn.setText("  Player:" + (rookState.playerId + 1) + " turn");




        getTopView().invalidate();
    }

    /**
     * this method gets called when the user clicks the '+' or '-' button. It
     * creates a new CounterMoveAction to return to the parent activity.
     *
     * @param button the button that was clicked
     */
    public void onClick(View button) {
        // if we are not yet connected to a game, ignore
        if (game == null) return;

        //In Ack phase any button acknowledges
        if (rookState.getPhase() == RookState.ACK_PHASE) {
            game.sendAction(new AcknowledgeTrick(this));
            return;
        }

        if(rookState.isBidPhase() && playerNum == rookState.playerId) {//if its bid phase and their turn
            if (button.getId() == R.id.passButton) {
                PassingAction passingAction = new PassingAction(this);
                game.sendAction(passingAction);
            } else if (button.getId() == R.id.bidButton) {
                int newBidValue = Integer.parseInt(bidText.getText().toString());
                BidAction bidAction = new BidAction(this, newBidValue);
                game.sendAction(bidAction);
            } else if (button.getId() == R.id.plusButton) {
                int newBidValue = Integer.parseInt(bidText.getText().toString()) + 5;
                if (newBidValue > 120) {
                    /*do nothing*/
                } else {
                    bidText.setText(newBidValue + "");
                }
            } else if (button.getId() == R.id.minusButton) {
                int newBidValue = Integer.parseInt(bidText.getText().toString()) - 5;
                if (newBidValue < rookState.getBidNum()) {
                    /*do nothing*/
                } else {
                    bidText.setText(newBidValue + "");
                }
            }
        }



        if (rookState.getPhase() == rookState.DISCARD_PHASE && rookState.bidWinner == playerNum) {
            if (button.getId() == R.id.passButton) {
                game.sendAction(new DiscardingAction(this, -1));
            }

            int index = -1;

            if (button.getId() == R.id.cardButton0) {
                index = 0;
            } else if (button.getId() == R.id.cardButton1) {
                index = 1;
            } else if (button.getId() == R.id.cardButton2) {
                index = 2;
            } else if (button.getId() == R.id.cardButton3) {
                index = 3;
            } else if (button.getId() == R.id.cardButton4) {
                index = 4;
            } else if (button.getId() == R.id.cardButton5) {
                index = 5;
            } else if (button.getId() == R.id.cardButton6) {
                index = 6;
            } else if (button.getId() == R.id.cardButton7) {
                index = 7;
            } else if (button.getId() == R.id.cardButton8) {
                index = 8;
            }
            game.sendAction(new DiscardingAction(this, index));

        }

        else if(rookState.getPhase() == rookState.TRUMP_PHASE && (rookState.bidWinner == playerNum)) {
            if (button.getId() == R.id.cardButton0) {
                rookState.trumpSuitIndex = 0;
            } else if (button.getId() == R.id.cardButton1) {
                rookState.trumpSuitIndex = 1;
            } else if (button.getId() == R.id.cardButton2) {
                rookState.trumpSuitIndex = 2;
            } else if (button.getId() == R.id.cardButton3) {
                rookState.trumpSuitIndex = 3;
            } else if (button.getId() == R.id.cardButton4) {
                rookState.trumpSuitIndex = 4;
            } else if (button.getId() == R.id.cardButton5) {
                rookState.trumpSuitIndex = 5;
            } else if (button.getId() == R.id.cardButton6) {
                rookState.trumpSuitIndex = 6;
            } else if (button.getId() == R.id.cardButton7) {
                rookState.trumpSuitIndex = 7;
            } else if (button.getId() == R.id.cardButton8) {
                rookState.trumpSuitIndex = 8;
            }
            game.sendAction(new TrumpSelection(this, rookState.trumpSuitIndex));
        }
        else {//assume its the play phase
            if (button.getId() == R.id.cardButton0) {
                PlayCardAction playCardAction = new PlayCardAction(this, rookState.playerHands[playerNum][0], 0);
                game.sendAction(playCardAction);
            } else if (button.getId() == R.id.cardButton1) {
                PlayCardAction playCardAction = new PlayCardAction(this, rookState.playerHands[playerNum][1], 1);
                game.sendAction(playCardAction);
            } else if (button.getId() == R.id.cardButton2) {
                PlayCardAction playCardAction = new PlayCardAction(this, rookState.playerHands[playerNum][2], 2);
                game.sendAction(playCardAction);
            } else if (button.getId() == R.id.cardButton3) {
                PlayCardAction playCardAction = new PlayCardAction(this, rookState.playerHands[playerNum][3], 3);
                game.sendAction(playCardAction);
            } else if (button.getId() == R.id.cardButton4) {
                PlayCardAction playCardAction = new PlayCardAction(this, rookState.playerHands[playerNum][4], 4);
                game.sendAction(playCardAction);
            } else if (button.getId() == R.id.cardButton5) {
                PlayCardAction playCardAction = new PlayCardAction(this, rookState.playerHands[playerNum][5], 5);
                game.sendAction(playCardAction);
            } else if (button.getId() == R.id.cardButton6) {
                PlayCardAction playCardAction = new PlayCardAction(this, rookState.playerHands[playerNum][6], 6);
                game.sendAction(playCardAction);
            } else if (button.getId() == R.id.cardButton7) {
                PlayCardAction playCardAction = new PlayCardAction(this, rookState.playerHands[playerNum][7], 7);
                game.sendAction(playCardAction);
            } else if (button.getId() == R.id.cardButton8) {
                PlayCardAction playCardAction = new PlayCardAction(this, rookState.playerHands[playerNum][8], 8);
                game.sendAction(playCardAction);
            }
        }
    } //onClick

    /**
     * assisted in writing by Prof Nuxoll
     */
    public int getResourceIdForCard(Card c) {
        if(c.getCardSuit() == null) { return R.drawable.null_card; }

        if (c.getCardSuit().equals("Black")) {
            if (c.getNum() == 5) {
                return R.drawable.five_black;
            } else if (c.getNum() == 6) {
                return R.drawable.six_black;
            } else if (c.getNum() == 7) {
                return R.drawable.seven_black;
            } else if (c.getNum() == 8) {
                return R.drawable.eight_black;
            } else if (c.getNum() == 9) {
                return R.drawable.nine_black;
            } else if (c.getNum() == 10) {
                return R.drawable.ten_black;
            } else if (c.getNum() == 11) {
                return R.drawable.eleven_black;
            } else if (c.getNum() == 12) {
                return R.drawable.twelve_black;
            } else if (c.getNum() == 13) {
                return R.drawable.thirteen_black;
            } else if (c.getNum() == 14) {
                return R.drawable.fourteen_black;
            }
        } else if (c.getCardSuit().equals("Red")) {
            if (c.getNum() == 5) {
                return R.drawable.five_red;
            } else if (c.getNum() == 6) {
                return R.drawable.six_red;
            } else if (c.getNum() == 7) {
                return R.drawable.seven_red;
            } else if (c.getNum() == 8) {
                return R.drawable.eight_red;
            } else if (c.getNum() == 9) {
                return R.drawable.nine_red;
            } else if (c.getNum() == 10) {
                return R.drawable.ten_red;
            } else if (c.getNum() == 11) {
                return R.drawable.eleven_red;
            } else if (c.getNum() == 12) {
                return R.drawable.twelve_red;
            } else if (c.getNum() == 13) {
                return R.drawable.thirteen_red;
            } else if (c.getNum() == 14) {
                return R.drawable.fourteen_red;
            }
        } else if (c.getCardSuit().equals("Yellow")) {
            if (c.getNum() == 5) {
                return R.drawable.five_yellow;
            } else if (c.getNum() == 6) {
                return R.drawable.six_yellow;
            } else if (c.getNum() == 7) {
                return R.drawable.seven_yellow;
            } else if (c.getNum() == 8) {
                return R.drawable.eight_yellow;
            } else if (c.getNum() == 9) {
                return R.drawable.nine_yellow;
            } else if (c.getNum() == 10) {
                return R.drawable.ten_yellow;
            } else if (c.getNum() == 11) {
                return R.drawable.eleven_yellow;
            } else if (c.getNum() == 12) {
                return R.drawable.twelve_yellow;
            } else if (c.getNum() == 13) {
                return R.drawable.thirteen_yellow;
            } else if (c.getNum() == 14) {
                return R.drawable.fourteen_yellow;
            }
        } else if (c.getCardSuit().equals("Green")) {
            if (c.getNum() == 5) {
                return R.drawable.five_green;
            } else if (c.getNum() == 6) {
                return R.drawable.six_green;
            } else if (c.getNum() == 7) {
                return R.drawable.seven_green;
            } else if (c.getNum() == 8) {
                return R.drawable.eight_green;
            } else if (c.getNum() == 9) {
                return R.drawable.nine_green;
            } else if (c.getNum() == 10) {
                return R.drawable.ten_green;
            } else if (c.getNum() == 11) {
                return R.drawable.eleven_green;
            } else if (c.getNum() == 12) {
                return R.drawable.twelve_green;
            } else if (c.getNum() == 13) {
                return R.drawable.thirteen_green;
            } else if (c.getNum() == 14) {
                return R.drawable.fourteen_green;
            }
        } else if (c.getCardSuit().equals("Rook")) {
            return R.drawable.rook;
        }
        return -1; //should not happen
    }




    /**
     * callback method when we get a message (e.g., from the game)
     *
     * @param info the message
     */
    @Override
    public void receiveInfo(GameInfo info) {
        // ignore the message if it's not a CounterState message
        if (!(info instanceof RookState)) return;

        // update our state; then update the display
        this.rookState = (RookState) info;


        updateDisplay();
    }

    /**
     * callback method--our game has been chosen/rechosen to be the GUI,
     * called from the GUI thread
     *
     * @param activity the activity under which we are running
     */
    public void setAsGui(GameMainActivity activity) {

        // remember the activity
        this.myActivity = activity;

        mp = MediaPlayer.create(this.myActivity,R.raw.jazz);
        mp.start();
        mp.setLooping(true);

        // Load the layout resource for our GUI
        activity.setContentView(R.layout.activity_main);
        bidText = activity.findViewById(R.id.betValueTextView);

        //initialize widget reference member variables
        this.bidButton = (Button) activity.findViewById(R.id.bidButton);
        this.plusButton = (Button) activity.findViewById(R.id.plusButton);
        this.minusButton = (Button) activity.findViewById(R.id.minusButton);
        this.passButton = (Button) activity.findViewById(R.id.passButton);

        this.cardButtons[0] = (ImageButton) activity.findViewById(R.id.cardButton0);
        this.cardButtons[1] = (ImageButton) activity.findViewById(R.id.cardButton1);
        this.cardButtons[2] = (ImageButton) activity.findViewById(R.id.cardButton2);
        this.cardButtons[3] = (ImageButton) activity.findViewById(R.id.cardButton3);
        this.cardButtons[4] = (ImageButton) activity.findViewById(R.id.cardButton4);
        this.cardButtons[5] = (ImageButton) activity.findViewById(R.id.cardButton5);
        this.cardButtons[6] = (ImageButton) activity.findViewById(R.id.cardButton6);
        this.cardButtons[7] = (ImageButton) activity.findViewById(R.id.cardButton7);
        this.cardButtons[8] = (ImageButton) activity.findViewById(R.id.cardButton8);

        this.playedCards[0] = (ImageView) activity.findViewById(R.id.player0_played_card);
        this.playedCards[1] = (ImageView) activity.findViewById(R.id.player1_played_card);
        this.playedCards[2] = (ImageView) activity.findViewById(R.id.player2_played_card);
        this.playedCards[3] = (ImageView) activity.findViewById(R.id.player3_played_card);

        this.team1Score = (TextView) activity.findViewById(R.id.team1ScoreTextView);
        this.team2Score = (TextView) activity.findViewById(R.id.team2ScoreTextView);
        this.bidWinner = (TextView) activity.findViewById(R.id.bidWinner_textView);
        this.leadingSuit = (TextView) activity.findViewById(R.id.leadingSuitTextView);
        this.trumpSuit = (TextView) activity.findViewById(R.id.trumpSuitTextView);
        this.currPhase = (TextView) activity.findViewById(R.id.phase_textView);
        this.playerTurn = (TextView) activity.findViewById(R.id.playerTurn_textView);
        //listen for button presses
        bidButton.setOnClickListener(this);
        plusButton.setOnClickListener(this);
        minusButton.setOnClickListener(this);
        passButton.setOnClickListener(this);

        for(int i = 0; i < cardButtons.length; i++) {
            cardButtons[i].setOnClickListener(this);
        }


    }//setAsGui

    @Override
    public void setPlayerNum(int playerNum) {
        //ignore
    }

}// class CounterHumanPlayer

