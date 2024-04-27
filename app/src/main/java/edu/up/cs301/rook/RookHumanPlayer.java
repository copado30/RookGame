package edu.up.cs301.rook;

import android.annotation.SuppressLint;
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
    public static final int DISCARD = 1;
    public static final int TRUMP = 2;
    public static final int PLAY = 3;

    private RookState rookState; // the most recent game state, as given to us by the RookLocalGame
    private GameMainActivity myActivity; // the android activity that we are running

    private Button passButton;
    private Button bidButton;
    private Button plusButton;
    private Button minusButton;

    MediaPlayer mp;

    private ImageButton[] cardButtons = new ImageButton[9];
    public ImageView[] playedCards = new ImageView[4];
    private TextView bidText; //displays center top text view, most often the bid amount and winner
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
    } //was game_state_test_layout

    /**
     * sets the card drawable to card image buttons
     * sets text displayed to user: team scores, phase, player turn, and round instructions
     */
    @SuppressLint("SetTextI18n")
    protected void updateDisplay() {
        // assign images to card buttons
        Card[] myHand = rookState.playerHands[this.playerNum];
        for (int i = 0; i < myHand.length; i++) { //fix deck length
            int resId = getResourceIdForCard(myHand[i]);
            cardButtons[i].setImageResource(resId);
        }

        // display the center cards in the trick
        for(int i = 0; i < rookState.cardsPlayed.length; i++) {
            int resId = getResourceIdForCard(rookState.cardsPlayed[i]);
            playedCards[i].setImageResource(resId);
        }

        // changes instructions/stats in GUI based on phases of game
        team1Score.setText(rookState.team1Score + "");
        team2Score.setText(rookState.team2Score + "");

        if (rookState.getPhase() == rookState.ACK_PHASE) {
            currPhase.setText("  Phase: Acknowledge");
            bidWinner.setText("Press any button to continue.");
        } else if (rookState.getPhase() == rookState.TRUMP_PHASE) {
            bidWinner.setText("Select the trump suit by selecting one of the\n" +
                              "cards in your hand.");
            currPhase.setText("  Phase: Trump");
        } else {
            if(rookState.bidWinner == 4){
                bidWinner.setText("No player has won, bid is: " + rookState.getBidNum() + "  ");
            }else {
                bidWinner.setText("  Player " + (rookState.bidWinner + 1) + ": " + rookState.getBidNum() + "  ");
            }
        }

        // changes instructions/stats in GUI based on phases of game
        if (rookState.getPhase() == rookState.BID_PHASE){
             currPhase.setText("  Phase: Bid");
        } else if (rookState.getPhase() == rookState.PLAY_PHASE){
             currPhase.setText("  Phase: Play");
        } else if (rookState.getPhase() == rookState.DISCARD_PHASE){
             if(rookState.bidWinner == playerNum) {
                 bidWinner.setText("You won the bid! Select up to 5 cards to trade\n" +
                                   "with the nest. Press pass when you are\n" +
                                   "done trading cards.");
             } else {
                 bidWinner.setText("Please wait for the bid winner to complete" +
                                   "collecting their prize.");
             }
             currPhase.setText("  Phase: Discard");
        }

        leadingSuit.setText("  Leading: " + rookState.leadingSuit);
        trumpSuit.setText("  Trump: " + rookState.trumpSuit);

        playerTurn.setText("  Player:" + (rookState.playerId + 1) + " turn");

        getTopView().invalidate();
    }

    /**
     * this method gets called when the user clicks the '+','-','pass','bid', or any of the
     * card image buttons. It creates a new RookMoveAction to return to the parent activity.
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

        // if its bid phase and their turn, they can interact with lower level buttons
        if(rookState.isBidPhase() && playerNum == rookState.playerId) {
            if (button.getId() == R.id.passButton) {
                PassingAction passingAction = new PassingAction(this);
                game.sendAction(passingAction);
            } else if (button.getId() == R.id.bidButton) {
                int newBidValue = Integer.parseInt(bidText.getText().toString());
                BidAction bidAction = new BidAction(this, newBidValue);
                game.sendAction(bidAction);
            } else if (button.getId() == R.id.plusButton) {
                int newBidValue = Integer.parseInt(bidText.getText().toString()) + 5;
                if (newBidValue > 120) { // cannot bid over 120
                    /*do nothing*/
                } else {
                    bidText.setText(newBidValue + "");
                }
            } else if (button.getId() == R.id.minusButton) {
                int newBidValue = Integer.parseInt(bidText.getText().toString()) - 5;
                if (newBidValue < rookState.getBidNum()) { // cannot bid less than previous bid
                    /*do nothing*/
                } else {
                    bidText.setText(newBidValue + "");
                }
            }
        }


        // if discard phase and human player is bidWinner, allow them to trade with nest
        if (rookState.getPhase() == rookState.DISCARD_PHASE && rookState.bidWinner == playerNum) {
            if (button.getId() == R.id.passButton) { // done trading
                game.sendAction(new DiscardingAction(this, -1));
            }else{
                sendAction(DISCARD, button.getId());
            }
        } else if(rookState.getPhase() == rookState.TRUMP_PHASE && (rookState.bidWinner == playerNum)) {
            sendAction(TRUMP, button.getId());
        } else {//assume its the play phase
            sendAction(PLAY,button.getId());
        }
    } //onClick

    /**
     * Intended to get resourceID of card image to be assigned to card buttons
     * in updateDisplay method
     *
     * assisted in writing by Prof Nuxoll
     */
    public int getResourceIdForCard(Card c) {
        //Nuxoll said this is fine, no other way to do it
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
     * sends actions based on button pressed
     *
     * @param actionType the action it's associated with
     * @param buttonId the button that was clicked
     */
    public void sendAction(int actionType, int buttonId){
        int indexOfButtonClicked = -1;//should get changed if a button was clicked
        int index = 0;

        for(int i = 0; i < cardButtons.length; i++){
            int cardId = cardButtons[i].getId();
            if(cardId == buttonId){
                indexOfButtonClicked = i;
                index = i;
                break;
            }
        }

        if(actionType == DISCARD){
            game.sendAction(new DiscardingAction(this, indexOfButtonClicked));
        }

        if(actionType == TRUMP){
            game.sendAction(new TrumpSelection(this, indexOfButtonClicked));
        }

        if(actionType == PLAY){
            game.sendAction(new PlayCardAction(this,rookState.playerHands[playerNum][index], indexOfButtonClicked));
        }
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

        // sound effects
        mp = MediaPlayer.create(this.myActivity,R.raw.jazz);
        mp.start();
        mp.setLooping(true);

        // Load the layout resource for our GUI
        activity.setContentView(R.layout.activity_main);
        bidText = activity.findViewById(R.id.betValueTextView);

        // initialize widget reference member variables
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

        // listen for button presses
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
}// class RookHumanPlayer

