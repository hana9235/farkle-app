package csc567_group_project.com.farkledicegame;


import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;

public class Winner extends ActionBarActivity{
    /** This class will receive the list of players once the game has done.
     * It will then sort the list of players in order of highest to lowest score.
     * They will then populate the listView and display the winners in order.
     * Maybe pop up a congratulations alert dialog or something. */


     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play);

         //getIntent() and extract playerList
         // sort on ArrayList attribute?
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

 }
