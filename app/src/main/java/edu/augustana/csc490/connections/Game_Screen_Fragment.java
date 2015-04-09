package edu.augustana.csc490.connections;

import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.Vibrator;


public class Game_Screen_Fragment extends Fragment {

    private PrimaryGameView primeGameView; // custom view
    private Vibrator vibrator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view =
                inflater.inflate(R.layout.fragment_game_screen, container, false);
        Log.v("MainGameFragment", "Right Before the view is found");
        primeGameView = (PrimaryGameView) view.findViewById(R.id.PrimaryGameview);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }

    // when paused, MainGameFragment stops the game
    @Override
    public void onPause()
    {
        super.onPause();
        primeGameView.stopGame();
    }

    // when MainActivity is over, releases game resources
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        primeGameView.releaseResources();
    }




}
