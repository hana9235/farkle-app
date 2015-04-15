package csc567_group_project.com.farkledicegame;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Play extends ActionBarActivity {
    GridLayout grid;
    ImageButton d1, d2, d3, d4, d5, d6, rollAgain, endTurn, showHeld;
    TextView playerName, totalScore, turnScore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play);

        grid = (GridLayout) findViewById(R.id.rolledGrid);
        d1 = (ImageButton) findViewById(R.id.d1);
        d2 = (ImageButton) findViewById(R.id.d2);
        d3 = (ImageButton) findViewById(R.id.d3);
        d4 = (ImageButton) findViewById(R.id.d4);
        d5 = (ImageButton) findViewById(R.id.d5);
        d6 = (ImageButton) findViewById(R.id.d6);

        playerName = (TextView) findViewById(R.id.playerName);
        totalScore = (TextView) findViewById(R.id.totalScore);
        turnScore = (TextView) findViewById(R.id.turnScore);

        rollAgain = (ImageButton) findViewById(R.id.rollAgain);
        endTurn = (ImageButton) findViewById(R.id.endTurn);
        showHeld = (ImageButton) findViewById(R.id.showHeld);

        Intent fromSetup = getIntent();
        int totalPlayers = fromSetup.getIntExtra("TOTAL", 2); // default value is two players (1 human, 1 AI)
        int numHumans = fromSetup.getIntExtra("NUMHUMANS", 1); // default to 1 human out of 2 players

        ArrayList<Player> players = createPlayers(totalPlayers, numHumans);
        //updateScreen(players, currentPlayer, );
        Game farkle = new Game(players);
        Toast.makeText(this, "TOTAL PLAYERS = " + totalPlayers, Toast.LENGTH_LONG).show();

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

        // GRIDLAYOUT NEEDS ONITEMCLICKLISTENER (or whatever it's called)
        // or set the same onclicklistener for every d* button, which needs to figure out which
        // position it's in.  Doing this through the grid would be best, but the design will
        // need to be changed a little bit
        

        //farkle.play();
        // START THREAD FOR farkle.play();
        // GET UPDATES FROM THE GAME CODE AS IT GOES
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
            p.set_name("Player " + i+1 );
            if (i >= numHumans) { // set p as AI
                p.set_to_ai();
                p.set_name(p.get_name() + " (AI)");
            }
            players.add(p);
        }
        return players;
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
        turnScore.setText(points);
    }

    public void updateDice(Player p, GridLayout grid) {
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
            switch(d.get_value()) {
                case 1:
                    // @drawable/d1
                    break;
                case 2:
                    // @drawable/d2
                    break;
                case 3:
                    // @drawable/d3
                    break;
                case 4:
                    // @drawable/d4
                    break;
                case 5:
                    // @drawable/d5
                    break;
                case 6:
                    // @drawable/d6
                    break;
                default:
                    //@drawable/blankdie
                    break;
            }

        }
    }

    public void rollAgain() {
        // to be implemented when the player chooses to roll again
        // how to do this for the AI player?
        Toast.makeText(this, "Clicked roll again", Toast.LENGTH_LONG).show();
    }

    public void endTurn() {
        // when a user is done with their turn
        // turn points are added to total (IF the user is over the 1000 point entry threshold)
        // p.reset_dice()
        // next_player()
        // THOSE FUNCTIONS ARE CALLED IN THE ACTUAL GAME CODE, BUT THAT MAY NEED TO BE CHANGED
        Toast.makeText(this, "Clicked end turn", Toast.LENGTH_LONG).show();
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
