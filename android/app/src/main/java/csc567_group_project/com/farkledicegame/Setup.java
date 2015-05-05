package csc567_group_project.com.farkledicegame;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;


public class Setup extends ActionBarActivity {
    TextView humanPrompt, AIprompt;
    EditText numHumans, numAI;
    int totalPlayers, humans, AIs;
    ImageButton startGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup);

        numHumans = (EditText) findViewById(R.id.numHumans);
        numAI = (EditText) findViewById(R.id.numAI);

        startGame = (ImageButton) findViewById(R.id.start_button);
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int humans, AIs;
                if(numHumans.getText().toString().equals("")) {
                    humans = 0;
                } else {
                    humans = Integer.parseInt(numHumans.getText().toString());
                }
                if (numAI.getText().toString().equals("")) {
                    AIs = 0;
                } else {
                    AIs = Integer.parseInt(numAI.getText().toString());
                }
            int totalPlayers = humans + AIs;
            beginGame(totalPlayers, humans);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void beginGame(int totalPlayers, int humans) {

        if (totalPlayers > 6) {
            Toast.makeText(this, "There is a maximum of 6 players.", Toast.LENGTH_LONG).show();
        }
        else {
//        Game farkle = new Game(p_list);
            Intent playgame = new Intent(this, Play.class);
            playgame.putExtra("TOTAL", totalPlayers);
            playgame.putExtra("NUMHUMANS", humans);
            //finish();
            startActivity(playgame);
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }


}
