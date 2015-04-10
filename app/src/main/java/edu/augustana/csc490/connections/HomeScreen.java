package edu.augustana.csc490.connections;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import java.util.Map;


public class HomeScreen extends Activity {
    private SharedPreferences topScores;
    public static final String SCORES = "scores";
    private TimeKeeper keeper;
    private static final String[] PLACES = {"first", "second","third"};
    private long[] placedTimes = new long[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        Button buttonStartGame = (Button) findViewById(R.id.buttonStartGame);
        buttonStartGame.setOnClickListener(startButtonListener);
        checkForGameComplete();
    }

    private void checkForGameComplete() {
        Intent intent = getIntent();
        long completedTime = intent.getLongExtra("longTime", -1);

//        String time = intent.getStringExtra("stringTime");
        if(completedTime >=0){
            String time = keeper.formatTime(completedTime);
            topScores = getSharedPreferences(SCORES, MODE_PRIVATE);
            if(!topScores.contains("first")){
                initScores(topScores);
            }
            placeScore(completedTime, topScores);
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(HomeScreen.this);

            // set the AlertDialog's title
            builder.setTitle("Your Time Was: "+time);

            // set list of items to display in dialog
            long[] displayArray = new long[3];
            for(int i = 0; i< displayArray.length;i++){
                if(placedTimes[i]==Long.MAX_VALUE){
                    displayArray[i]=000000;
                }else{
                    displayArray[i]=placedTimes[i];
                }
            }
            builder.setMessage(" 1st: "+keeper.formatTime(displayArray[0])
                    +"\n2nd: "+keeper.formatTime(displayArray[1])+"\n 3rd: "+keeper.formatTime(displayArray[2]));

            // set the AlertDialog's negative Button
            builder.setNegativeButton("OK",
                    new DialogInterface.OnClickListener()
                    {
                        // called when the "Cancel" Button is clicked
                        public void onClick(DialogInterface dialog, int id)
                        {
                            dialog.cancel(); // dismiss the AlertDialog
                        }
                    }
            ); // end call to setNegativeButton
            builder.create().show(); // display the AlertDialog
        }

    }

    private void placeScore(long completedTime, SharedPreferences topScores) {
        Map p = topScores.getAll();
        int placementLocation = -1;
        boolean locationFound = false;
        for(int i = 0; i<=p.size()-1;i++){
            placedTimes[i] = (long) p.get(PLACES[i]);
            if(completedTime<placedTimes[i]&&!locationFound){
                placementLocation = i;
                locationFound=true;
            }
        }
        if(locationFound){
            long[] tempArray = new long[3];
            switch (placementLocation){
                case 0:
                    tempArray[0]=completedTime;
                    tempArray[1]=placedTimes[0];
                    tempArray[2]=placedTimes[1];
                    break;
                case 1:
                    tempArray[0]=placedTimes[0];
                    tempArray[1]=completedTime;
                    tempArray[2]=placedTimes[1];
                    break;
                case 2:
                    tempArray[0]=placedTimes[0];
                    tempArray[1]=placedTimes[1];
                    tempArray[2]=completedTime;
            }
            placedTimes=tempArray;
            for(int iterator = 0; iterator<=placedTimes.length-1;iterator++){
                preferencesPutValue(PLACES[iterator], placedTimes[iterator], topScores);
            }
        }

    }

    private void initScores(SharedPreferences topScores) {
        preferencesPutValue("first", Long.MAX_VALUE, topScores);
        preferencesPutValue("second", Long.MAX_VALUE, topScores);
        preferencesPutValue("third", Long.MAX_VALUE, topScores);


    }
    public void preferencesPutValue(String key, long value, SharedPreferences topScores){
        SharedPreferences.Editor sharedEditor = topScores.edit();
        sharedEditor.putLong(key,value);
        sharedEditor.apply();
    }

    public View.OnClickListener startButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(HomeScreen.this, MainActivity.class);
            startActivity(intent);
           /* Intent intent = new Intent();
            startActivity(Intent.createChooser(intent,getString(R.string.shareSearch)));*/
        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        return true;
    }



}
