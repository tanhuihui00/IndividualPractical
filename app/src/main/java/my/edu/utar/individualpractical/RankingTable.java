package my.edu.utar.individualpractical;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.renderscript.Sampler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Text;

public class RankingTable extends AppCompatActivity {

    TextView data;
    int newWinnerID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking_table);

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
                Intent intent = new Intent(RankingTable.this, HomePage.class);
                finish();
                finishAffinity();
                startActivity(intent);
            }
        });

        newWinnerID = getIntent().getIntExtra("newWinnerID",-1);

        retrieveAllWinner(newWinnerID);
    }

    public void retrieveAllWinner(int id){

        SQLiteAdapter mySQLiteAdapter = new SQLiteAdapter(this);

        mySQLiteAdapter.openToRead();

        String result = mySQLiteAdapter.queueAll();

        String[] arrOfResult = result.split("\n", 0);

        TableLayout rankTable = findViewById(R.id.rankingTable);
        TableRow rowRecord = new TableRow(this);
        rankTable.addView(rowRecord);

        int no = 0;

        for (int j=0; j<arrOfResult.length; j+=3){

            data = new TextView(this);
            data.setText(String.valueOf(++no));
            data.setTextSize(16);
            data.setTypeface(Typeface.MONOSPACE,Typeface.NORMAL);
            if(id != -1){
                if(Integer.valueOf(arrOfResult[j]) == id){
                    data.setTypeface(Typeface.MONOSPACE,Typeface.BOLD);
                    data.setTextSize(24);
                }
            }
            rowRecord.addView(data);

            data = new TextView(this);
            data.setText(arrOfResult[j+1]);
            data.setTextSize(16);
            data.setTypeface(Typeface.MONOSPACE,Typeface.NORMAL);
            if(id != -1){
                if(Integer.valueOf(arrOfResult[j]) == id){
                    data.setTypeface(Typeface.MONOSPACE,Typeface.BOLD);
                    data.setTextSize(24);
                }
            }
            rowRecord.addView(data);

            data = new TextView(this);
            data.setText(arrOfResult[j+2]);
            data.setTextSize(16);
            data.setTypeface(Typeface.MONOSPACE,Typeface.NORMAL);
            if(id != -1){
                if(Integer.valueOf(arrOfResult[j]) == id){
                    data.setTypeface(Typeface.MONOSPACE,Typeface.BOLD);
                    data.setTextSize(24 );
                }
            }
            rowRecord.addView(data);

            rowRecord = new TableRow(this);
            rankTable.addView(rowRecord);
        }
        mySQLiteAdapter.close();
    }
}