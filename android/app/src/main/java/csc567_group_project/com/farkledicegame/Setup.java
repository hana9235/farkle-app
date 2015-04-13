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
              int humans = Integer.parseInt(numHumans.getText().toString());
              int AIs = Integer.parseInt(numAI.getText().toString());
              int totalPlayers = humans + AIs;
              ArrayList<Player> p_list = createPlayers(totalPlayers, AIs);
              beginGame(p_list);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    public ArrayList<Player> createPlayers(int totalPlayers, int numAIs) {
        return new ArrayList<Player>();
    }

    public void beginGame(ArrayList<Player> p_list) {

//        Game farkle = new Game(p_list);
        Intent playgame = new Intent(this, Play.class);
        playgame.putExtra("PLAYERLIST", p_list);
        startActivity(playgame);
    }

}
