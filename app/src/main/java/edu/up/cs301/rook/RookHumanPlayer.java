package edu.up.cs301.rook;

import edu.up.cs301.GameFramework.Card;
import edu.up.cs301.GameFramework.infoMessage.GameState;
import edu.up.cs301.GameFramework.players.GameHumanPlayer;
import edu.up.cs301.GameFramework.GameMainActivity;
import edu.up.cs301.GameFramework.actionMessage.GameAction;
import edu.up.cs301.GameFramework.infoMessage.GameInfo;
import edu.up.cs301.GameFramework.players.GamePlayer;
import edu.up.cs301.rook.R;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View.OnClickListener;

import org.w3c.dom.Text;

/**
 * A GUI of a counter-player. The GUI displays the current value of the counter,
 * and allows the human player to press the '+' and '-' buttons in order to
 * send moves to the game.
 * 
 * Just for fun, the GUI is implemented so that if the player presses either button
 * when the counter-value is zero, the screen flashes briefly, with the flash-color
 * being dependent on whether the player is player 0 or player 1.
 * 
 * @author Steven R. Vegdahl
 * @author Andrew M. Nuxoll
 * @version July 2013
 */
public class RookHumanPlayer extends GameHumanPlayer implements OnClickListener {

	/* instance variables */

	// The TextView the displays the current counter value
	private TextView testResultsTextView;
	private TextView bidText;

	// the most recent game state, as given to us by the CounterLocalGame
	private RookState rookState;

	// the android activity that we are running
	private GameMainActivity myActivity;
	private EditText editText;
	private Button runTestButton;
	private Button passButton;
	private Button bidButton;
	private Button plusButton;
	private Button minusButton;
	boolean firstRun = true;// for first press of RunTest button
	/**
	 * constructor
	 * @param name
	 * 		the player's name
	 */
	public RookHumanPlayer(String name) {super(name);}



	/**
	 * Returns the GUI's top view object
	 *
	 * @return
	 * 		the top object in the GUI's view heirarchy
	 */
	public View getTopView() {
		return myActivity.findViewById(R.id.main_activity_GUI);}//was game_state_test_layout

	/**
	 * sets the counter value in the text view
	 */
	protected void updateDisplay() {
		// set the text in the appropriate widget
	//	counterValueTextView.setText("" + state.getCounter());
	}

	/**
	 * this method gets called when the user clicks the '+' or '-' button. It
	 * creates a new CounterMoveAction to return to the parent activity.
	 *
	 * @param button
	 * 		the button that was clicked
	 */
	public void onClick(View button) {
		// if we are not yet connected to a game, ignore
		if (game == null) return;

		if(button.getId() == R.id.passButton){
			PassingAction passingAction = new PassingAction(this);
			game.sendAction(passingAction);
		} else if(button.getId() == R.id.bidButton){
			int newBidValue = Integer.parseInt(bidText.getText().toString());
			BidAction bidAction = new BidAction(this, newBidValue);
			game.sendAction(bidAction);
		} else if(button.getId() == R.id.plusButton) {
			int newBidValue = Integer.parseInt(bidText.getText().toString()) + 5;
			if(newBidValue > 120){
				/*do nothing*/
			} else {
				bidText.setText(newBidValue + "");}
		} else if(button.getId() == R.id.minusButton) {
			int newBidValue = Integer.parseInt(bidText.getText().toString()) - 5;
			if(newBidValue < rookState.getBidNum()) {
				/*do nothing*/
			} else {
				bidText.setText(newBidValue + "");}
		} else if(button.getId() == R.id.passButton){
			PassingAction passingAction = new PassingAction(this);
			game.sendAction(passingAction);
		}

		/*
		if(firstRun){
			testResultsTextView.setText("");
			firstRun = false;
		}

		RookState firstInstance = new RookState();
		RookState secondInstance = new RookState();

		firstInstance.createDeck();
		firstInstance.shuffle();

		secondInstance.createDeck();
		secondInstance.shuffle();

		// 1 round of rook play

		if(firstInstance.bid(new BidAction(this))) {
			testResultsTextView.setText(testResultsTextView.getText() + "Player 1 just added 5 points to their bid! + ");
		} if(firstInstance.bid(new BidAction(this))) {
			testResultsTextView.setText(testResultsTextView.getText() + "Player 2 just added 15 points to their bid! + ");
		} if(firstInstance.bid(new BidAction(this))) {
			testResultsTextView.setText(testResultsTextView.getText() + "Player 3 just added 25 points to their bid! + ");
		} if(firstInstance.passTurn(new PassingAction(this))) {
			testResultsTextView.setText(testResultsTextView.getText() + "Player 4 passed on their bid! + ");
		} if(firstInstance.discardCard(new DiscardingAction(this))) {
			testResultsTextView.setText(testResultsTextView.getText() + "Player 3 won the bid. They chose 5 cards from their deck to discard and exchange with the nest and selected the trump suit as black. + ");
		} if(firstInstance.playCard(new PlayCardAction(this, new Card(10, 10, "Red")))) {
			testResultsTextView.setText(testResultsTextView.getText() + "Player 1 played a red 14! + ");
		} if(firstInstance.playCard(new PlayCardAction(this, new Card(0, 8, "Red")))) {
			testResultsTextView.setText(testResultsTextView.getText() + "Player 2 played a red 8 + ");
		} if(firstInstance.playCard(new PlayCardAction(this, new Card(0, 9, "Black")))) {
			testResultsTextView.setText(testResultsTextView.getText() + "Player 3 played a black 9! + ");
		} if(firstInstance.playCard(new PlayCardAction(this, new Card(0, 12, "Red")))) {
			testResultsTextView.setText(testResultsTextView.getText() + "Player 4 played a red 12! + ");
		}
		testResultsTextView.setText(testResultsTextView.getText() + "Player 3 won the game with a black 9! + ");

		RookState firstCopy = new RookState(firstInstance); // perspective of player 1
		RookState secondCopy = new RookState(secondInstance);

		if(firstCopy.toString().equals(secondCopy.toString())) { // comparing firstCopy and secondCopy toString
			testResultsTextView.setText(testResultsTextView.getText() + "firstCopy and secondCopy are equal to each other. ");
			testResultsTextView.setText(testResultsTextView.getText() + ". The firstCopy string is: " + firstCopy.toString() + " ");
			testResultsTextView.setText(testResultsTextView.getText() + "The secondCopy string is: " + secondCopy.toString() + " ");
		}


		testResultsTextView.setText(firstCopy.toString());
		testResultsTextView.setText(testResultsTextView.getText() + secondCopy.toString()); */

	} //onClick
	
	/**
	 * callback method when we get a message (e.g., from the game)
	 * 
	 * @param info
	 * 		the message
	 */
	@Override
	public void receiveInfo(GameInfo info) {
		// ignore the message if it's not a CounterState message
		if (!(info instanceof RookState)) return;
		
		// update our state; then update the display
		this.rookState = (RookState)info;
		updateDisplay();
	}
	
	/**
	 * callback method--our game has been chosen/rechosen to be the GUI,
	 * called from the GUI thread
	 * 
	 * @param activity
	 * 		the activity under which we are running
	 */
	public void setAsGui(GameMainActivity activity) {
		
		// remember the activity
		this.myActivity = activity;
		
	    // Load the layout resource for our GUI
		activity.setContentView(R.layout.activity_main);
		bidText = activity.findViewById(R.id.betValueTextView);

		//initialize widget reference member variables
		this.bidButton = (Button)activity.findViewById(R.id.bidButton);
		this.plusButton = (Button)activity.findViewById(R.id.plusButton);
		this.minusButton = (Button)activity.findViewById(R.id.minusButton);
		this.passButton = (Button)activity.findViewById(R.id.passButton);

		//listen for button presses
		bidButton.setOnClickListener(this);
		plusButton.setOnClickListener(this);
		minusButton.setOnClickListener(this);
		passButton.setOnClickListener(this);

		//TextView bidText = (TextView)activity.findViewById(R.id.betValueTextView);
		//bidText.setText(String.valueOf(total));

		//testResultsTextView = activity.findViewById(R.id.edit_text_results);

		//runTestButton = activity.findViewById(R.id.run_test_button);
		//runTestButton.setOnClickListener(this);

	}//setAsGui

}// class CounterHumanPlayer

