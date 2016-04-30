package com.google.engedu.ghost;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import com.google.common.base.Strings;

public class GhostActivity extends ActionBarActivity {
    private static final String COMPUTER_TURN = "Computer's turn";
    private static final String USER_TURN = "Your turn";
    private GhostDictionary dictionary;
    private boolean userTurn = false;
    private Random random = new Random();
    private SimpleDictionary simpleDictionary;
    private FastDictionary fastDictionary;
    private String wordFragment = "";

    // State saves
    static final String STATE_COMPUTER_TURN = "";
    static final String STATE_USER_TURN = "";
    // static final SimpleDictionary STATE_SIMPLE_DICTIONARY = simpleDictionary;
    static final String STATE_USERTURN = "";
    static final String STATE_WORD_FRAGMENT = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);
        onStart(null);

        /*
        √ Start by updating GhostActivity.onCreate to instantiate a FastDictionary instead of a SimpleDictionary and let's explore FastDictionary.
        √ You will notice that it implements the same interface as SimpleDictionary but instead of storing each word in an ArrayList, it adds them to a TrieNode.
        Your next challenge is to implement the add and isWord methods of the TrieNode class.
         */

        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open("words.txt");
            fastDictionary = new FastDictionary(inputStream);
        } catch (IOException e) {
            Toast toast = Toast.makeText(this, "Could not load dictionary", Toast.LENGTH_LONG);
            toast.show();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        // http://developer.android.com/training/basics/activity-lifecycle/recreating.html

        // Save the user's current game state
        savedInstanceState.putString(STATE_COMPUTER_TURN, COMPUTER_TURN);
        savedInstanceState.putString(STATE_USER_TURN, USER_TURN);
        savedInstanceState.putBoolean(STATE_USERTURN, userTurn);
        savedInstanceState.putString(STATE_WORD_FRAGMENT, wordFragment);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ghost, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    /*
    √ Check if the fragment is a word with at least 4 characters. If so declare victory by updating the game status
    √ Use the dictionary's getAnyWordStartingWith method to get a possible longer word
    √ If such a word doesn't exist (method returns null), challenge the user's fragment and declare victory (you can't bluff this computer!)
    If such a word does exist, add the next letter of it to the fragment (remember the substring method in the Java string library)

     */
    private void computerTurn() {

        if (wordFragment.length() == 0) {
            char letter = (char) (random.nextInt(26) + 'a');
            wordFragment = String.valueOf(letter);
            updateGhostView();
            updateStatus(USER_TURN);

        } else {

            if (wordFragment.length() >= 4 && fastDictionary.isWord(wordFragment)) {
                updateStatus(wordFragment + " is a word, the computer wins!");
                disableChallengeButton();
            } else if (fastDictionary.getAnyWordStartingWith(wordFragment) == null) {
                updateStatus(wordFragment + " cannot form a word, the computer wins!");
                disableChallengeButton();
//            } else if (!fastDictionary.isWord(fastDictionary.getAnyWordStartingWith(wordFragment))) {
//                updateStatus(wordFragment + " cannot form a word, the computer wins!");
//                disableChallengeButton();
            } else {

                if (!Strings.isNullOrEmpty(fastDictionary.getAnyWordStartingWith(wordFragment))) {
                    wordFragment = fastDictionary.getGoodWordStartingWith(wordFragment).substring(0, wordFragment.length() + 1); // gets only the first letter
                    updateGhostView();
                } else {
                    updateStatus("There aren't any words starting with " + wordFragment + ", the computer wins!");
                    disableChallengeButton();
                }

                TextView label = (TextView) findViewById(R.id.gameStatus);
                // Do computer turn stuff then make it the user's turn again
                userTurn = true;
                label.setText(USER_TURN);

            }
        }

    }

    /*
    √ Handler for the "Reset" button.
    √ Randomly determines whether the game starts with a user turn or a computer turn.
    */
    public boolean onStart(View view) {

        userTurn = random.nextBoolean();

        updateGhostView();

        if (userTurn) {
            updateStatus(USER_TURN);
        } else {
            updateStatus(COMPUTER_TURN);
            computerTurn();
        }

        return true;

    }

    /*
    √ Since we are not using a standard EditText field, we will need to do some keyboard handling.
    √ Proceed to override the GhostActivity.onKeyUp method.
    √ If the key that the user pressed is not a letter, default to returning the value of super.onKeyUp().
    √ Otherwise, add the letter to the word fragment.
    √ Also check whether the current word fragment is a complete word and, if it is, update the game status label to indicate so (this is not the right behavior for the game but will allow you to verify that your code is working for now).
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent e) {

        char keyPressed = (char) e.getUnicodeChar();

        if (Character.isLetter(keyPressed)) {
            wordFragment += keyPressed;

            updateGhostView();
        }

        userTurn = false;

        computerTurn();

        return super.onKeyUp(keyCode, e);

    }

    public void reset(View view) {

        wordFragment = "";
        updateGhostView();

        userTurn = random.nextBoolean();
        // Log.d("test", "userTurn: " + userTurn);

        if (userTurn) {
            updateStatus(USER_TURN);
        } else {
            updateStatus(COMPUTER_TURN);
            computerTurn();
        }

        Button challengeBtn = (Button) findViewById(R.id.challenge_btn);
        challengeBtn.setEnabled(true);

    }

    /*
    √ Get the current word fragment
    √ If it has at least 4 characters and is a valid word, declare victory for the user
    √ Otherwise if a word can be formed with the fragment as prefix, declare victory for the computer and display a possible word
    √ If a word cannot be formed with the fragment, declare victory for the user
    */
    public void challenge(View view) {

        if (wordFragment.length() < 4) {
            updateStatus("There must be at least 4 characters to challenge.");
        } else if (wordFragment.length() >= 4 && fastDictionary.isWord(wordFragment)) {
            // Log.d("test", "Word is >= 4 && is a word!");
            updateStatus(wordFragment + " is a word, you win!");
            disableChallengeButton();
        } else {
            String tempWord = fastDictionary.getAnyWordStartingWith(wordFragment);
            // Log.d("test", "word plus 1: " + tempWord);
            if (tempWord == null) {
                updateStatus(wordFragment + " is not a word, you win!");
                Log.d("jon", "tempWord: " + tempWord);
            } else if (tempWord.length() > wordFragment.length()) {
                updateStatus(tempWord + " can be created from " + wordFragment + ", you lose!");
            } else {
                updateStatus(wordFragment + " cannot be extended, you win!");
            }
            disableChallengeButton();
        }

    }

    public void disableChallengeButton() {
        Button challengeBtn = (Button) findViewById(R.id.challenge_btn);
        challengeBtn.setEnabled(false);
    }

    public void updateStatus(String status) {
        TextView gameStatus = (TextView) findViewById(R.id.gameStatus);
        gameStatus.setText(status);
    }

    public void updateGhostView() {
        TextView ghostTextView = (TextView) findViewById(R.id.ghostText);
        ghostTextView.setText(wordFragment);
    }

}