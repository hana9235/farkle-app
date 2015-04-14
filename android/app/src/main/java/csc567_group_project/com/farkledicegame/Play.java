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
        startActivity(toWinner);
    }
}
