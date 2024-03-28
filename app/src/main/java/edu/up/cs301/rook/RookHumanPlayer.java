package edu.up.cs301.rook;

import edu.up.cs301.GameFramework.infoMessage.GameState;
import edu.up.cs301.GameFramework.players.GameHumanPlayer;
import edu.up.cs301.GameFramework.GameMainActivity;
import edu.up.cs301.GameFramework.actionMessage.GameAction;
import edu.up.cs301.GameFramework.infoMessage.GameInfo;
import edu.up.cs301.rook.R;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View.OnClickListener;

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
	
	// the most recent game state, as given to us by the CounterLocalGame
	private RookState state;
	
	// the android activity that we are running
	private GameMainActivity myActivity;
	private EditText editText;
	private Button runTestButton;
	boolean firstRun = true; // for first press of RunTest button

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
		return myActivity.findViewById(R.id.game_state_test_layout);
	}
	
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

		if(firstRun){
			testResultsTextView.setText("");
			firstRun = false;
		}

		RookState firstInstance = new RookState();
		RookState secondInstance = new RookState();


		firstInstance.createDeck();
		firstInstance.shuffle();

		//firstInstance.discardCard(new DiscardingAction(this));
		testResultsTextView.setText(testResultsTextView.getText() + firstInstance.toString() + " ");
		//firstInstance.bid(action);
		//firstInstance.passTurn(action);
		//firstInstance.playCard(action);

		//testResultsTextView.setText(firstInstance.toString());
		RookState firstCopy = new RookState(firstInstance); // perspective of player 1
		RookState secondCopy = new RookState(secondInstance);

		if(firstCopy.toString().equals(secondCopy.toString())) {
			testResultsTextView.setText(testResultsTextView.getText() + ". firstCopy and secondCopy are equal to each other. ");
			testResultsTextView.setText(testResultsTextView.getText() + " " + firstCopy.toString() + " ");
			testResultsTextView.setText(testResultsTextView.getText() + " " + secondCopy.toString() + " ");
		}
	}// onClick
	
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
		this.state = (RookState)info;
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
		activity.setContentView(R.layout.unit_test);
		testResultsTextView = activity.findViewById(R.id.edit_text_results);

		runTestButton = activity.findViewById(R.id.run_test_button);
		runTestButton.setOnClickListener(this);

	}//setAsGui

}// class CounterHumanPlayer

