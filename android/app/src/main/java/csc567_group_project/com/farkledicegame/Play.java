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
    Handler gameToUI;

    // game vars here
    boolean game_won;
    final int pointsToWin = 10000;
    int turnTotal, totalPlayers, currentPlayer;

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


        playerName = (TextView) findViewById(R.id.playerName);
        totalScore = (TextView) findViewById(R.id.totalScore);
        turnScore = (TextView) findViewById(R.id.turnScore);

        rollAgain = (ImageButton) findViewById(R.id.rollAgain);
        endTurn = (ImageButton) findViewById(R.id.endTurn);
        showHeld = (ImageButton) findViewById(R.id.showHeld);

        showHeld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // just a test to make sure the winner screen is accessible
                toWinner();
            }
        });

        rollAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        //updateScreen(players, currentPlayer, );
        currentPlayer = 0; // this is an index that loops through players

        Player p = players.get(currentPlayer);
        updateViews();  // set name and score views to appropriate player data

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
        finish();
        startActivity(toWinner);
    }

    public void setPlayerName(TextView nameField, Player p) {
        nameField.setText(p.get_name());
    }

    public void updateTurnScore(TextView turnScore, int points) {
        // parseInt from the current field, or just pull the current turn score
        int prevScore = Integer.parseInt(turnScore.getText().toString());
        points = points + prevScore;
        String pts = Integer.toString(points);
        turnScore.setText(pts);
    }

    public void updateDice(Player p, ArrayList<ImageButton> diceView) {
        // get the list of dice from p  (p.get_rolled_dice())
        // find the face values of the dice and draw the correct die in the grid
        // so if a player rolls three dice and gets "1 4 3", then grid should display in this order:
        //  1  4
        //  3
        // The rest should be empty (there is a @drawable/blankdie you can use to fill the space)
        // My immediate idea for the choosing of which die to draw is with a switch/case where
        // the default is the blankdie
        ArrayList<Die> dice = p.get_rolled_dice();

        for (int i = 0; i < dice.size(); i++) {
            Die d = dice.get(i);
            ImageButton currentDie = diceView.get(i);

            switch(d.get_value()) {
                case 1:
                    currentDie.setBackgroundResource(R.drawable.d1);
                    // @drawable/d1
                    break;
                case 2:
                    currentDie.setBackgroundResource(R.drawable.d2);
                    // @drawable/d2
                    break;
                case 3:
                    currentDie.setBackgroundResource(R.drawable.d3);
                    // @drawable/d3
                    break;
                case 4:
                    currentDie.setBackgroundResource(R.drawable.d4);
                    // @drawable/d4
                    break;
                case 5:
                    currentDie.setBackgroundResource(R.drawable.d5);
                    // @drawable/d5
                    break;
                case 6:
                    currentDie.setBackgroundResource(R.drawable.d6);
                    // @drawable/d6
                    break;
                default:
                    //@drawable/blankdie
                    currentDie.setBackgroundResource(R.drawable.blankdie);
                    break;
            }

        }
    }

    public ArrayList<Integer> calculate_roll_value(Player p) {
        ArrayList<Integer> results = new ArrayList<Integer>();
        // result will have two values, the point value and the number of scoring dice

        int sum = 0;
        int scoring_dice = 0;
        ArrayList<Die> rolled_dice = p.get_rolled_dice();

        // count occurrences
        Map<Integer, Integer> counted = new HashMap<Integer, Integer>();
        for (int i = 0; i < rolled_dice.size(); i++) {
            int die_face_val = rolled_dice.get(i).get_value();
            if(!counted.containsKey(die_face_val)) {
                // value is not yet in the map, add it
                counted.put(die_face_val, 1);
            }
            else { // value exists, increase number of occurrences
                int old_count = (int) counted.get(die_face_val);
                counted.put(die_face_val, old_count + 1);
            }
        }

        // counted should contain something like:
        // {1=3, 4=2, 6=1}

        // rules vary for scoring if at least 3 of a value has been rolled
        ArrayList<Integer> at_least_three = new ArrayList<Integer>();
        ArrayList<Integer> less_than_three = new ArrayList<Integer>();

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

        Player p = players.get(currentPlayer);

        p.roll_dice();

        ArrayList<Integer> roll_results = calculate_roll_value(p);
        ArrayList<Die> rolled = p.get_rolled_dice();

        if (roll_results.get(1) < 1) {
            Toast.makeText(this, "Bust!",Toast.LENGTH_LONG).show();
            turnScore.setText("0");
            rollAgain.setClickable(false);
        } else {
            String turnPts = Integer.toString(roll_results.get(0));
            turnScore.setText(turnPts);
        }

        updateDice(p, diceView);
        updateTurnScore(turnScore, Integer.parseInt(turnScore.getText().toString()));
    }

    public void endTurn() {
        // when a user is done with their turn
        // turn points are added to total (IF the user is over the 1000 point entry threshold)
        // p.reset_dice()
        // next_player()
        // THOSE FUNCTIONS ARE CALLED IN THE ACTUAL GAME CODE, BUT THAT MAY NEED TO BE CHANGED
        Player p = players.get(currentPlayer);

        p.reset_dice();
        if (p.get_score() > 10000) {
            toWinner();
        }

        advancePlayer();
        updateViews();

        Toast.makeText(this, "Clicked end turn", Toast.LENGTH_LONG).show();
    }

    public void updateViews() {
        Player p = players.get(currentPlayer);
        setPlayerName(playerName, p);
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
        Toast.makeText(this, "Clicked on position" + position, Toast.LENGTH_LONG).show();
    }
}
