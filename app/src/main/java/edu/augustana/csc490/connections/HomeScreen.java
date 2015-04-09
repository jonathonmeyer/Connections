package edu.augustana.csc490.connections;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class HomeScreen extends Activity {

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
        String time = intent.getStringExtra("stringTime");
        if(time != null){
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(HomeScreen.this);

            // set the AlertDialog's title
            builder.setTitle("Your Time Was: "+time);

            // set list of items to display in dialog


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

    public View.OnClickListener startButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(HomeScreen.this, MainActivity.class);
            HomeScreen.this.finish();
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
