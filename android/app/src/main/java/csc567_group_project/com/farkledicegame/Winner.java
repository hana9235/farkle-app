package csc567_group_project.com.farkledicegame;


import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.List;

public class Winner extends ActionBarActivity{
    /** This class will receive the list of players once the game has done.
     * It will then sort the list of players in order of highest to lowest score.
     * They will then populate the listView and display the winners in order.
     * Maybe pop up a congratulations alert dialog or something. */

   ListView winners;
   ImageButton backToStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.winner);

        //getIntent() and get the player data
        // sort by score
        // put them in the listview
        Intent fromPlay = getIntent();
        int numberOfPlayers = fromPlay.getIntExtra("numberOfPlayers", 0);
        String [] playerNames= new String[numberOfPlayers];
        int [] playerScores= new int[numberOfPlayers];

        for(int i = 0; i < numberOfPlayers; i++) {
            String name = fromPlay.getStringExtra("PlayerName" + i);
            int score = fromPlay.getIntExtra("PlayerScore"+i, 0);
            playerNames[i] = name;
            playerScores[i] = score;
        }

        String [] playerStrings = new String[numberOfPlayers];
        for(int i = 0; i < numberOfPlayers; i++) {
            String line = playerNames[i] + "\t\t " + playerScores[i];
            playerStrings[i] = line;
        }

        winners = (ListView) findViewById(R.id.winners);

        ArrayAdapter<String> arrayAdapterNew = new ArrayAdapter<String>(this,
                android.R.layout.simple_expandable_list_item_1 , playerStrings);

        winners.setAdapter(arrayAdapterNew);

        backToStart = (ImageButton) findViewById(R.id.return_to_start);

        backToStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToStart();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    public void backToStart() {
        Intent backToStart = new Intent(this, MainActivity.class);
        //finish();
        backToStart.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(backToStart);

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

}
