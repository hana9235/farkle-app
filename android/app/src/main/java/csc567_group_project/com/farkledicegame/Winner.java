package csc567_group_project.com.farkledicegame;


import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
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

        //getIntent() and extract playerList
        // sort on ArrayList attribute?
        winners = (ListView) findViewById(R.id.winners);
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    public void backToStart() {
        Intent backToStart = new Intent(this, MainActivity.class);
        startActivity(backToStart);
    }
 }
