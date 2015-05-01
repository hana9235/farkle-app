package csc567_group_project.com.farkledicegame;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Play extends ActionBarActivity {
    GridLayout grid;
    ArrayList<ImageButton> diceView;
    ArrayList<Player> players;
    ImageButton d1, d2, d3, d4, d5, d6, rollAgain, endTurn, showHeld;
    TextView playerName, totalScore, turnScore;
    boolean viewingHeld, firstRollOfTurn;

    // game vars here
    boolean game_won;
    final int pointsToWin = 10000;
    int turnTotal, heldScore, totalPlayers, currentPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play);

        // initialize UI
        grid = (GridLayout) findViewById(R.id.rolledGrid);
        diceView = new ArrayList<ImageButton>();

        d1 = (ImageButton) findViewById(R.id.d1);
        d2 = (ImageButton) findViewById(R.id.d2);
        d3 = (ImageButton) findViewById(R.id.d3);
        d4 = (ImageButton) findViewById(R.id.d4);
        d5 = (ImageButton) findViewById(R.id.d5);
        d6 = (ImageButton) findViewById(R.id.d6);
        diceView.add(d1);
        diceView.add(d2);
        diceView.add(d3);
        diceView.add(d4);
        diceView.add(d5);
        diceView.add(d6);


        for(int i = 0; i < diceView.size(); i++) {
            final int diePosition = i;
            ImageButton d = diceView.get(i);
            d.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        holdDie(diePosition);
                }
            });
            diceView.set(i, d);
        }


        playerName = (TextView) findViewById(R.id.playerName);
        totalScore = (TextView) findViewById(R.id.totalScore);
        turnScore = (TextView) findViewById(R.id.turnScore);

        rollAgain = (ImageButton) findViewById(R.id.rollAgain);
        endTurn = (ImageButton) findViewById(R.id.endTurn);
        showHeld = (ImageButton) findViewById(R.id.showHeld);

        viewingHeld = false;

        showHeld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recalculateHeld();

                if (viewingHeld) {
                    viewingHeld = false;
                    showHeld.setBackgroundResource(R.drawable.showheld);
                    // disable roll again just to keep things from being buggy
                    rollAgain.setBackgroundResource(R.drawable.rollagain);
                    rollAgain.setClickable(true);
                    updateDice(players.get(currentPlayer).get_rolled_dice(), diceView, viewingHeld);

                } else {
                    viewingHeld = true;
                    showHeld.setBackgroundResource(R.drawable.showrolled);
                    rollAgain.setBackgroundResource(R.drawable.rollagaindisabled);
                    rollAgain.setClickable(false);

                    // TODO:  get the score of the held dice

                    updateDice(players.get(currentPlayer).get_held_dice(), diceView, viewingHeld);
                }

            }
        });

        rollAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Player p = players.get(currentPlayer);

                // set flag bits in the held dice to -1 so they're locked
                ArrayList<Die> dice = p.get_held_dice();
                for(int i = 0; i < dice.size(); i++) {
                    Die d = dice.get(i);
                    d.setHoldLock(-1);
                }

                rollAgain();
            }
        });
        endTurn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endTurn();
            }
        });


        // initialize game variables
        Intent fromSetup = getIntent();
        totalPlayers = fromSetup.getIntExtra("TOTAL", 2); // default value is two players (1 human, 1 AI)
        int numHumans = fromSetup.getIntExtra("NUMHUMANS", 1); // default to 1 human out of 2 players

        players = createPlayers(totalPlayers, numHumans);
        currentPlayer = 0; // this is an index that loops through players

        Player p = players.get(currentPlayer);
        updateViews();  // set name and score views to appropriate player data
        heldScore = 0;
        turnTotal = 0;

        firstRollOfTurn = true;

        rollAgain();  // start the game

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    public ArrayList<Player> createPlayers(int totalPlayers, int numHumans) {
        // generate all the players necessary, set AI state and name them accordingly
        ArrayList<Player> players = new ArrayList<>();
        for (int i = 0; i < totalPlayers; i++) {
            Player p = new Player();
            p.set_name("Player " + (i+1) );
            if (i >= numHumans) { // set p as AI
                p.set_to_ai();
                p.set_name(p.get_name() + " (AI)");
            }
            players.add(p);
        }
        return players;
    }


    public void advancePlayer() {
        currentPlayer++;
        if (currentPlayer >= totalPlayers) {
            currentPlayer = 0;
        }
    }

    public void toWinner() {
        Intent toWinner = new Intent(this, Winner.class);
        // players are in a arraylist "players"
        // look at the labs
        finish();
        startActivity(toWinner);
    }


    public void updateTurnScore(int pts) {
        // parseInt from the current field, or just pull the current turn score
        Player p = players.get(currentPlayer);

        /**
        System.out.println("old roll score = " + rollScore);
        ArrayList<Integer> updated_results = calculate_roll_value(p.get_rolled_dice());

        // pull the roll score out so we can change it and prevent weirdness
        turnTotal = turnTotal - rollScore;
        // get rid of the points for not-held dice
        if (rollScore == 0) {
            rollScore = updated_results.get(0);
        } else {
            rollScore = rollScore - updated_results.get(0);
        }
        // add the revised score for this roll to the turn total
        turnTotal += rollScore;

        turnScore.setText(Integer.toString(turnTotal));
        /*int prevScore = Integer.parseInt(turnScore.getText().toString());
        int rolledScore = Integer.parseInt(pts);
        int points = rolledScore + prevScore;
        pts = Integer.toString(points);
        turnScore.setText(pts);  */
    }

    public void updateDice(ArrayList<Die> dice, ArrayList<ImageButton> diceView, boolean heldDice) {
        // get the list of dice from p  (p.get_rolled_dice())
        // find the face values of the dice and draw the correct die in the grid
        // so if a player rolls three dice and gets "1 4 3", then grid should display in this order:
        //  1  4
        //  3

        for (int i = 0; i < dice.size(); i++) {
            Die d = dice.get(i);
            ImageButton currentDie = diceView.get(i);
            //System.out.println("HoldLock of this die (value = " + d.get_value() + ") is " + d.getHoldLock());

            switch(d.get_value()) {
                case 1:
                    if(heldDice) {
                        currentDie.setBackgroundResource(R.drawable.heldd1);
                    } else {
                        currentDie.setBackgroundResource(R.drawable.d1);
                    }
                    if(d.getHoldLock() == -1) {
                        currentDie.setClickable(false);
                    } else {
                        currentDie.setClickable(true);
                    }

                    // @drawable/d1
                    break;
                case 2:
                    if(heldDice) {
                        currentDie.setBackgroundResource(R.drawable.heldd2);
                    } else {
                        currentDie.setBackgroundResource(R.drawable.d2);
                    }
                    if(d.getHoldLock() == -1) {
                        currentDie.setClickable(false);
                    } else {
                        currentDie.setClickable(true);
                    }
                    // @drawable/d2
                    break;
                case 3:
                    if(heldDice) {
                        currentDie.setBackgroundResource(R.drawable.heldd3);
                    } else {
                        currentDie.setBackgroundResource(R.drawable.d3);
                    }
                    if(d.getHoldLock() == -1) {
                        currentDie.setClickable(false);
                    } else {
                        currentDie.setClickable(true);
                    }
                    // @drawable/d3
                    break;
                case 4:
                    if(heldDice) {
                        currentDie.setBackgroundResource(R.drawable.heldd4);
                    } else {
                        currentDie.setBackgroundResource(R.drawable.d4);
                    }
                    if(d.getHoldLock() == -1) {
                        currentDie.setClickable(false);
                    } else {
                        currentDie.setClickable(true);
                    }
                    // @drawable/d4
                    break;
                case 5:
                    if(heldDice) {
                        currentDie.setBackgroundResource(R.drawable.heldd5);
                    } else {
                        currentDie.setBackgroundResource(R.drawable.d5);
                    }
                    if(d.getHoldLock() == -1) {
                        currentDie.setClickable(false);
                    } else {
                        currentDie.setClickable(true);
                    }
                    // @drawable/d5
                    break;
                case 6:
                    if(heldDice) {
                        currentDie.setBackgroundResource(R.drawable.heldd6);
                    } else {
                        currentDie.setBackgroundResource(R.drawable.d6);
                    }
                    if(d.getHoldLock() == -1) {
                        currentDie.setClickable(false);
                    } else {
                        currentDie.setClickable(true);
                    }
                    // @drawable/d6
                    break;
                default:
                    //@drawable/blankdie
                    currentDie.setBackgroundResource(R.drawable.blankdie);
                    currentDie.setClickable(false);
                    break;
            }
        }
        // fill the rest with blanks
        for(int j = dice.size(); j < diceView.size(); j++) {  // TODO: check that this is looping right -- probably, but clickable is not set right
            ImageButton d = diceView.get(j);
            d.setBackgroundResource(R.drawable.blankdie);
            d.setClickable(false);

            // reset/update the imagebutton
            diceView.set(j, d);
        }
    }

    public ArrayList<Integer> calculate_roll_value(ArrayList<Die> dList) {
        ArrayList<Integer> results = new ArrayList<>();
        // result will have two values, the point value and the number of scoring dice

        int sum = 0;
        int scoring_dice = 0;
        ArrayList<Die> rolled_dice = dList;

        // count occurrences
        Map<Integer, Integer> counted = new HashMap<Integer, Integer>();
        for (int i = 0; i < rolled_dice.size(); i++) {
            int die_face_val = rolled_dice.get(i).get_value();
            if(!counted.containsKey(die_face_val)) {
                // value is not yet in the map, add it
                counted.put(die_face_val, 1);
            }
            else { // value exists, increase number of occurrences
                int old_count = counted.get(die_face_val);
                if (old_count + 1 == 6) {
                    if (firstRollOfTurn) {
                        // if you roll all 6 dice the same on your first roll, you win
                        results.add(10000);
                        results.add(6);
                    }
                }
                counted.put(die_face_val, old_count + 1);
            }
        }

        // counted should contain something like:
        // {1=3, 4=2, 6=1}

        // rules vary for scoring if at least 3 of a value has been rolled
        ArrayList<Integer> at_least_three = new ArrayList<>();
        ArrayList<Integer> less_than_three = new ArrayList<>();

        for( Map.Entry<Integer, Integer> entry : counted.entrySet()) {
            int face_val = entry.getKey();
            int occurrences = entry.getValue();

            if (occurrences < 3) {
                less_than_three.add(face_val);
            }
            else {
                at_least_three.add(face_val);
            }
        }
        // the roll values are properly sorted between the lists

        // first take care of special cases
        if (less_than_three.size() == 6) {
            // rolled a straight
            sum = 1500;
            scoring_dice = 6;
            results.add(sum);
            results.add(scoring_dice);
            System.out.println("Straight");
            return results;
        }

        if (less_than_three.size() == 3 && at_least_three.size() == 0
                && rolled_dice.size() == 6) {
            // possibly rolled three pairs
            boolean is_three_pair = true;
            for(int i = 0; i < 3; i++) {
                int key = less_than_three.get(i);
                int occurrences = counted.get(key);
                if (occurrences != 2) {
                    // if one isn't a pair, three pair fails
                    is_three_pair = false;
                }
            }
            if(is_three_pair) {
                // if it got to here, then it must be three pair
                sum = 600;
                scoring_dice = 6;
                System.out.println("Three pairs");
                results.add(sum);
                results.add(scoring_dice);
                return results;
            }
        }

        // special cases didn't happen, calculate point values of roll
        // three or more of a number
        for(int val : at_least_three) {
            int occurrences = counted.get(val);
            if(occurrences > 3) {
                if (val == 1) {
                    // special case, 3 1's == 1000
                    sum += 1000 * (2 * (occurrences - 3));
                }
                else { // not 1's, no special case
                    sum += (val * 100) * (2 * (occurrences - 3));
                }
                scoring_dice += occurrences;
            }
            else { // exactly 3 of the same value rolled
                if (val == 1) {
                    sum += 1000;
                }
                else {
                    sum += (val * 100);
                }
                scoring_dice += occurrences;
            }
        }

        // for 1 or 2 of a number, only 1's and 5's count for points
        for (int val : less_than_three) {
            int occurrences = counted.get(val);
            if (val == 1) {
                sum += 100 * occurrences;
                scoring_dice += occurrences;
            }
            else if (val == 5) {
                sum += 50 * occurrences;
                scoring_dice += occurrences;
            }
        }

        System.out.println("score: " + sum);

        results.add(sum);
        results.add(scoring_dice);
        return results;
    }

    public void rollAgain() {
        // to be implemented when the player chooses to roll again
        // how to do this for the AI player?

        // enable clicking dice in case all scored previously and were disabled
        recalculateHeld();
        int heldScore = Integer.parseInt(turnScore.getText().toString());
        turnTotal += heldScore;

        for(int i = 0; i < diceView.size(); i++) {
            ImageButton ib = diceView.get(i);
            ib.setClickable(true);
            diceView.set(i, ib);
        }

        Player p = players.get(currentPlayer);
        boolean allScored = false;

        p.roll_dice();

        ArrayList<Integer> roll_results = calculate_roll_value(p.get_rolled_dice());

        // use roll score as temporary value to prevent scoring issues with turnTotal
        int rollScore = roll_results.get(0);

        // player must hold dice to keep rolling, or end turn
        rollAgain.setClickable(false);
        rollAgain.setBackgroundResource(R.drawable.rollagaindisabled);

        if (roll_results.get(1) == 0) {
            // no scoring dice
            Toast.makeText(this, "Bust!",Toast.LENGTH_LONG).show();
            turnScore.setText("0");

        }
        else {
            if (roll_results.get(1) == p.get_rolled_dice().size()) {
                Toast.makeText(this, "All 6 dice have scored, you may roll them all again.", Toast.LENGTH_LONG).show();
                // disable the dice until the roll again button is clicked again  -- prevents weird reset issue
                for(int i = 0; i < diceView.size(); i++) {
                    ImageButton ib = diceView.get(i);
                    ib.setClickable(false);
                    diceView.set(i, ib);
                }

                rollAgain.setBackgroundResource(R.drawable.rollagain);
                rollAgain.setClickable(true);
                allScored = true;
            } else {
                //String turnPts = Integer.toString(rollScore);
                updateTurnScore(heldScore);
            }
        }

        updateDice(p.get_rolled_dice(), diceView, false);
        if (allScored) {
            turnTotal += roll_results.get(0);
            p.reset_dice();
        }
        firstRollOfTurn = false;
    }

    public void endTurn() {
        // when a user is done with their turn
        // turn points are added to total (IF the user is over the 1000 point entry threshold)
        // p.reset_dice()
        // next_player()
        Player p = players.get(currentPlayer);

        p.reset_dice();
        int thisTurnTotal = Integer.parseInt(turnScore.getText().toString());
        updateTotalScore(thisTurnTotal);

        turnTotal = 0;


        if (p.get_score() >= pointsToWin) {
            toWinner();
            return;
        }

        advancePlayer();
        updateViews();

        //rollScore = 0;
        rollAgain.setClickable(true); // in case the user busted and rollAgain was disabled
        rollAgain.setBackgroundResource(R.drawable.rollagain);
        viewingHeld = false;

        // reset to check for 6 of same value
        firstRollOfTurn = true;
        rollAgain();
    }

    public void updateTotalScore(int points) {
        Player p = players.get(currentPlayer);
        if(p.get_score() >= 1000) { // already on board
            p.add_to_score(points);
        } else {
            // not on board, did they make the threshold?
            if (points >= 1000) {
                p.add_to_score(points);
            }
            else {
                Toast.makeText(this, "Not enough to get on the board, sorry!", Toast.LENGTH_LONG).show();
            }
        }

    }


    public void updateViews() {
        Player p = players.get(currentPlayer);
        playerName.setText(p.get_name());
        String playerTotalScore = Integer.toString(p.get_score());
        totalScore.setText(playerTotalScore);
        turnScore.setText("0");
    }

    // hold each die individually
    public void holdDie(int position) {
        // dice are accessed by their position in the grid
        //  0  1
        //  2  3
        //  4  5
        // so pass that index back to game, which will move that die to the player's hold list

        ImageButton clicked = diceView.get(position);
        clicked.setBackgroundResource(R.drawable.blankdie);
        clicked.setClickable(false);
        Player p = players.get(currentPlayer);
        if(viewingHeld) {
            // ?????
            p.unHold(position);
            rollAgain.setClickable(false);
            rollAgain.setBackgroundResource(R.drawable.rollagaindisabled);
            updateDice(p.get_held_dice(), diceView, true);
        } else {
            p.holdOne(position);
            rollAgain.setClickable(true);
            rollAgain.setBackgroundResource(R.drawable.rollagain);
            updateDice(p.get_rolled_dice(), diceView, false);

        }
        recalculateHeld();

        // don't click on the button if there's nothing to roll; you'll bust
        // OR just reset the dice to avoid the whole problem
        // (you shouldn't be able to be here and trying to hold all six - something in your held dice
        // doesn't count for points)
        if(p.get_rolled_dice().size() == 0) {
            rollAgain.setClickable(false);
            rollAgain.setBackgroundResource(R.drawable.rollagaindisabled);
        }


    }


    public void recalculateHeld() {
        ArrayList<Die> unlockedDice = players.get(currentPlayer).getUnlockedDice();
        ArrayList<Integer> holdScoreResults = calculate_roll_value(unlockedDice);
        int currentTurnScore = Integer.parseInt(turnScore.getText().toString());

        System.out.println("Hold score = " + holdScoreResults.get(0));
        turnScore.setText(Integer.toString(holdScoreResults.get(0) + currentTurnScore));
    }
}
