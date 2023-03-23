package my.edu.utar.individualpractical;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Handler;
import android.os.Looper;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ActionPanel extends AppCompatActivity{

    Button replayBtn, nextLvlBtn, rankBtn;
    TextView titleMsg, msg;

    static String winnerName;

    int currentLevel, currentScore;
    boolean clickedAll, doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_panel);

        titleMsg = findViewById(R.id.overlay_textview);
        msg = findViewById(R.id.msg);
        replayBtn = findViewById(R.id.replayBtn);
        rankBtn = findViewById(R.id.rankBtn);
        nextLvlBtn = findViewById(R.id.overlay_button);

        currentLevel = getIntent().getIntExtra("level",1);
        currentScore = getIntent().getIntExtra("score",0);
        clickedAll = getIntent().getBooleanExtra("clickedAll",false);

        msg.setTextColor(Color.BLACK);
        titleMsg.setTextColor(Color.BLACK);

        if(currentLevel < 5){
            if(clickedAll){
                titleMsg.setText("Congratulation! You have successfully touched all smiley faces!");
            }else{
                titleMsg.setText("Oh no! Time is up.");
            }
            msg.setText("Are you ready for the next level?\nYour current score: "+currentScore);
            nextLvlBtn.setText("Level "+(currentLevel+1));
            rankBtn.setVisibility(View.INVISIBLE);
            replayBtn.setVisibility(View.INVISIBLE);
        }else{
            titleMsg.setText("Hooray!");
            msg.setText("You have completed all the levels!\nYour final score: "+currentScore);
            nextLvlBtn.setVisibility(View.INVISIBLE);
            rankBtn.setVisibility(View.VISIBLE);
            replayBtn.setVisibility(View.VISIBLE);

            if(checkIfEnterTop25(currentScore)){
                insertDialog();
            }
        }


        nextLvlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currentLevel < 5) {
                    currentLevel++;
                }

                Intent intent = new Intent(ActionPanel.this, MainActivity.class);
                intent.putExtra("action", "nextLvl");
                intent.putExtra("level", currentLevel);
                intent.putExtra("score", currentScore);
                startActivity(intent);

            }
        });

        replayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActionPanel.this, MainActivity.class);
                intent.putExtra("action", "replay");
                startActivity(intent);
            }
        });

        rankBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActionPanel.this, RankingTable.class);
                startActivity(intent);
            }
        });

        ImageButton closeApp = findViewById(R.id.closeAppBtn);

        closeApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
                finish();
                System.exit(0);
            }
        });

        ImageButton backHomeBtn = findViewById(R.id.backHomeBtn);

        backHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActionPanel.this, HomePage.class);
                finishAffinity();
                finish();
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finishAffinity();
            finish();
            System.exit(0);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    public boolean checkIfEnterTop25(int score){

        boolean result;

        SQLiteAdapter mySQLiteAdapter = new SQLiteAdapter(this);
        mySQLiteAdapter.openToRead();

        int scoreOf25th = mySQLiteAdapter.queueByLowest();

        if(score > scoreOf25th){
            result = true;
        }else{
            result = false;
        }

        return result;
    }

    public void insertDialog(){
        Top25PlayerDialogForm builder = new Top25PlayerDialogForm();
        builder.show(getSupportFragmentManager(), "RegisterWinnerDialog");

        //if press submit, then store name and score to DB
        builder.setMyCustomListener(new Top25PlayerDialogForm.CustomListener() {
            @Override
            public void onMyCustomAction(CustomObject o){
                saveToDB(winnerName, currentScore);
            }
        });
    }

    public void saveToDB(String name, int score){
        if(name != null){
            SQLiteAdapter mySQLiteAdapter;

            mySQLiteAdapter = new SQLiteAdapter(this);
            mySQLiteAdapter.openToWrite();

            mySQLiteAdapter.insert(name,score);

            int newWinnerID = mySQLiteAdapter.retrieveLastWinnerID();

            Intent intent = new Intent(ActionPanel.this, RankingTable.class);
            intent.putExtra("newWinnerID",newWinnerID);

            startActivity(intent);

            mySQLiteAdapter.close();

            finishAffinity();
            finish();

        }else{
            insertDialog();
        }

    }
}