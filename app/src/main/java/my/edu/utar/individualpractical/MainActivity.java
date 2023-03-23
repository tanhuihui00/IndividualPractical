package my.edu.utar.individualpractical;


import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    // Paint object for coloring and styling
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    // Some colors for the face background, eyes and mouth.
    int eyesColor = Color.BLACK, mouthColor = Color.BLACK, borderColor = Color.BLACK;

    Path mouthPath = new Path();

    ArrayList<ViewCustom> faceList = new ArrayList<>();

    LinearLayout faceArea;

    ViewCustom clickedFace;

    // Face size in pixels
    float size;

    int[] noOfFace = {4,9,16,25,36};
    int level = 1, printedFace = 0, randomIndex, score = 0;
    boolean completedPrint = false;
    boolean clickedAll = false, doubleBackToExitPressedOnce = false, backHomeAction = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        generateFaces();
        counterProgressBar();

        String action = getIntent().getStringExtra("action");
        int nextLevel = getIntent().getIntExtra("level",1);
        int currentScore = getIntent().getIntExtra("score",0);

        if(action != null){
            if(action.equals("nextLvl")){
                level = nextLevel;
                score = currentScore;
                generateFaces();
                counterProgressBar();
            }else if(action == "replay"){
                level = 1;
                score = 0;
                generateFaces();
                counterProgressBar();
            }
        }

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
                Intent intent = new Intent(MainActivity.this, HomePage.class);
                finishAffinity();
                finish();
                backHomeAction = true;
                startActivity(intent);
            }
        });
    }

    private class ViewCustom extends View {

        // Face border width in pixels
        float borderWidth = 2.0f;

        int no = 1;

        public ViewCustom(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            drawFaceBackground(canvas);
            drawEyes(canvas);
            drawMouth(canvas);
        }

        public void drawFaceBackground(Canvas canvas) {
            // 1
            if (no == 0) {
                paint.setColor(Color.YELLOW);
            } else {
                paint.setColor(Color.GRAY);
            }

            paint.setStyle(Paint.Style.FILL);

            // 2
            float radius = size / 2f;

            // 3
            canvas.drawCircle(size / 2f, size / 2f, radius, paint);

            // 4
            paint.setColor(borderColor);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(borderWidth);

            setHighlighted(no);

            // 5
            canvas.drawCircle(size / 2f, size / 2f, radius - borderWidth / 2f, paint);
        }

        public void drawEyes(Canvas canvas) {
            // 1
            paint.setColor(eyesColor);
            paint.setStyle(Paint.Style.FILL);

            setHighlighted(no);

            // 2
            RectF leftEyeRect = new RectF(size * 0.32f, size * 0.23f, size * 0.43f, size * 0.50f);
            canvas.drawOval(leftEyeRect, paint);

            // 3
            RectF rightEyeRect = new RectF(size * 0.57f, size * 0.23f, size * 0.68f, size * 0.50f);
            canvas.drawOval(rightEyeRect, paint);
        }

        public void drawMouth(Canvas canvas) {

            mouthPath.reset();

            mouthPath.moveTo(size * 0.22f, size * 0.7f);

            if (no == 1) {
                mouthPath.quadTo(size * 0.50f, size * 0.5f, size * 0.78f, size * 0.7f);
            } else {
                mouthPath.quadTo(size * 0.50f, size * 0.80f, size * 0.78f, size * 0.70f);
                mouthPath.quadTo(size * 0.50f, size * 0.90f, size * 0.22f, size * 0.70f);
            }

            paint.setColor(mouthColor);
            paint.setStyle(Paint.Style.FILL);

            setHighlighted(no);

            // 5
            canvas.drawPath(mouthPath, paint);
        }

        public void setHighlighted(int no) {
            if (no == 1) {
                paint.setAlpha(30); //50%
            } else {
                paint.setAlpha(255);  //100%
            }
        }
    }

    public void counterProgressBar(){

        if(level < 5){

            ProgressBar progressBar = findViewById(R.id.progressBar);

            CountDownTimer countDownTimer = new CountDownTimer(5000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    int progress = (int) (millisUntilFinished / 1000);
                    progressBar.setProgress(progress);

                    if(clickedAll){
                        cancel();
                        if(!backHomeAction){
                            showActionPanel();
                        }
                    }

                    // Check if there is 1 more second left in the countdown
                    if (progress == 1) {
                        progressBar.setProgressTintList(ColorStateList.valueOf(Color.RED));
                    }else{
                        progressBar.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
                    }
                }

                @Override
                public void onFinish() {
                    progressBar.setProgress(0);
                    if(!backHomeAction){
                        showActionPanel();
                    }
                    faceList.clear();
                }
            };
            countDownTimer.start();
        }
    }

    public void generateFaces(){

        faceList.clear();

        int noRow;
        float deviceWidth = getResources().getDisplayMetrics().widthPixels;

        if(level < 3){
            noRow = 3;
        }else if(level == 5){
            noRow = 6;
        }else{
            noRow = 5;
        }

        size = (deviceWidth / noRow) - 15;

        faceArea = findViewById(R.id.faceArea);
        faceArea.removeAllViews();

        TextView title = new TextView(this);
        title.setText("Level "+level);
        title.setTextSize(36);
        title.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        faceArea.addView(title);
        faceArea.setGravity(Gravity.CENTER_HORIZONTAL);

        LinearLayout.LayoutParams llparams = new LinearLayout.LayoutParams(((int)size),(int)size);
        LinearLayout.LayoutParams llParentParams = new LinearLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,ActionBar.LayoutParams.WRAP_CONTENT);
        llparams.setMargins(5,5,5,5);

        int id = 0;

        for(int i = 0; i < noOfFace[level-1]; i++){
            LinearLayout faceRow = new LinearLayout(this);
            faceRow.setOrientation(LinearLayout.HORIZONTAL);
            faceRow.setLayoutParams(llParentParams);
            faceArea.addView(faceRow);

            for(int j = 0; j < noRow; j++){
                if(printedFace >= noOfFace[level-1]){
                    completedPrint = true;
                    break;
                }
                ViewCustom vc = new ViewCustom(this);
                vc.setLayoutParams(llparams);
                id++;
                vc.setId(id);
                faceList.add(vc);
                faceRow.addView(vc);
                printedFace++;
            }

            if(completedPrint){
                printedFace = 0;
                completedPrint = false;
                break;
            }
        }

        randomFace();
        counterProgressBar();
    }

    public void randomFace(){

        if(faceList.size()>0){
            randomIndex = new Random().nextInt(faceList.size());

            //randomly highlight a face
            ViewCustom highlightedFace = faceList.get(randomIndex);

            highlightedFace.no = 0;
            highlightedFace.invalidate();

            for(ViewCustom vc : faceList){
                vc.setOnClickListener(clickSmileFace);
            }
        }
        else{
            clickedAll = true;
        }
    }

    View.OnClickListener clickSmileFace = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            boolean found = false;

            for (int i = 0; i < faceList.size(); i++) {
                if (Objects.equals(faceList.get(i).getId(), view.getId())) {
                    clickedFace = faceList.get(i);
                    found = true;
                    break;
                }
            }

            if(found == true && clickedFace.no == 0){
                playSound(0);
                score++;
                faceList.remove(clickedFace);
                clickedFace = null;
                randomFace();
            }else{
                playSound(1);
            }
        }
    };

    public void showActionPanel(){
        Intent intent = new Intent(MainActivity.this, ActionPanel.class);
        intent.putExtra("level", level);
        intent.putExtra("score", score);
        intent.putExtra("clickedAll",clickedAll);
        startActivity(intent);
    }

    private void playSound(int id) {

        int audioFile;

        switch (id) {
            case 0:
                // Play sound for correct clicks
                audioFile = R.raw.correct;
                break;
            default:
                audioFile = R.raw.wrong;
                break;
        }

        MediaPlayer mp = MediaPlayer.create(this, Uri.parse("android.resource://" +getPackageName()+ "/"+audioFile));
        mp.start();

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                mp.release();
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
}