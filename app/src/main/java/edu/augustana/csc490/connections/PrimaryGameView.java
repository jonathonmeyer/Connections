package edu.augustana.csc490.connections;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.ArrayList;
/*
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
*/
import java.util.Random;

/**
 * Created by jonathonm on 3/29/2015.
 */
public class PrimaryGameView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "Connections"; // for Log.w(TAG, ...)



    private GameThread gameThread; // runs the main game loop
    private Activity mainActivity; // keep a reference to the main Activity

    private boolean isGameOver = true;
    private boolean  dotsSet;
    Random r = new Random();

    private TimeKeeper keeper;
    private int x;
    private int y;
    private long startTime;
    private long totalTime;
    private int screenWidth;
    private int screenHeight;

    private int changeDelay = 30;
    private int previousSize;
    private int previousIndex;

    private ArrayList<Circle> gameDots;

    private Paint myPaint;
    private Paint backgroundPaint;
    private Paint boxPaint;

    public PrimaryGameView(Context context, AttributeSet atts)
    {

        super(context, atts);
        mainActivity = (Activity) context;
        //Log.v("MainGameView", "Right after the view is created");

        getHolder().addCallback(this);
        gameDots =  new ArrayList<>(1);
       /* gameDots.add(new Circle(250,250,50));
        gameDots.add(new Circle(600,600,50));
        gameDots.get(0).setTarget(true);*/
        myPaint = new Paint();
        myPaint.setColor(Color.BLUE);
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.GRAY);

        keeper = new TimeKeeper();
    }

    // called when the size changes (and first time, when view is created)
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.v("ONSizeChNGED", "w: " + w + "h: " + h);
        dotsSet = false;
        screenWidth = w;
        screenHeight = h;

        startNewGame();
        keeper.start();
        startTime = System.nanoTime();
    }

    public void startNewGame()
    {
        //this.x = 250;
        //this.y = 250;

        if (isGameOver)
        {
            isGameOver = false;
            gameThread = new GameThread(getHolder());
            gameThread.start(); // start the main game loop going
        }
    }

    public void updateView(Canvas canvas, ArrayList<Circle> dots) {
        synchronized (gameDots) {
            if (canvas != null) {
                if (!dotsSet) {
                    initDots(canvas);
                }
                canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), backgroundPaint);
                changeDelay--;
                if (changeDelay == 0) {
                    if(gameDots.size()>1) {
                        int nextCircle = r.nextInt(gameDots.size());
                        gameDots.get(nextCircle).setTarget(true);
                        if (gameDots.size() == previousSize) {
                            if (nextCircle != previousIndex) {
                                // Log.v(TAG, "previousIndex: "+previousIndex);
                                gameDots.get(previousIndex).setTarget(false);
                            }
                        }
                        previousIndex = nextCircle;
                        previousSize = gameDots.size();
                        changeDelay = 30;
                    }else if(gameDots.size()<=1){
                        gameDots.get(0).setTarget(true);
                    }
                }
                for (Circle d : dots) {
                    canvas.drawCircle(d.getX(), d.getY(), (float) d.getRadius(), d.getColor());
                    //canvas.drawRect(d.getX()-d.getRadius(), d.getY()-d.getRadius(), 600, 750, boxPaint);
                }

            }
        }
    }

    // stop the game; may be called by the MainGameFragment onPause
    public void stopGame()
    {
        if (gameThread != null)
            gameThread.setRunning(false);
    }

    // release resources; may be called by MainGameFragment onDestroy
    public void releaseResources()
    {
        // release any resources (e.g. SoundPool stuff)
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    // called when the surface is destroyed
    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        // ensure that thread terminates properly
        boolean retry = true;
        gameThread.setRunning(false); // terminate gameThread

        while (retry)
        {
            try
            {
                gameThread.join(); // wait for gameThread to finish
                retry = false;
            }
            catch (InterruptedException e)
            {
                Log.e(TAG, "Thread interrupted", e);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e)
    {
        if (e.getAction() == MotionEvent.ACTION_DOWN)
        {
            synchronized (gameDots) {
                int remove = -1;

                for (int i = 0; i < gameDots.size(); i++) {
                    if (gameDots.get(i).pointWithin((int) e.getX(), (int) e.getY())) {
                        remove = i;
                    }
                }

                if (remove >= 0) {
                    gameDots.remove(remove);
                    if(gameDots.size()<=0){
                        keeper.stopTime();
                        Intent intent = new Intent(mainActivity,HomeScreen.class );
                        intent.putExtra("longTime", keeper.longElapsedTime());
                        intent.putExtra("stringTime", keeper.stringElapsedTime());
//                        startActivity(intent);
                        mainActivity.finish();
                        mainActivity.startActivity(intent);
                    }
                }

            }
        }


        return true;
    }

    // Thread subclass to run the main game loop
    private class GameThread extends Thread
    {
        private SurfaceHolder surfaceHolder; // for manipulating canvas
        private boolean threadIsRunning = true; // running by default

        // initializes the surface holder
        public GameThread(SurfaceHolder holder)
        {
            surfaceHolder = holder;
            setName("GameThread");
        }

        // changes running state
        public void setRunning(boolean running)
        {
            threadIsRunning = running;
        }

        @Override
        public void run()
        {
            Canvas canvas = null;

            while (threadIsRunning)
            {
                try
                {
                    // get Canvas for exclusive drawing from this thread
                    canvas = surfaceHolder.lockCanvas(null);

                    // lock the surfaceHolder for drawing
                    synchronized(surfaceHolder)
                    {
                       // gameStep();         // update game state
                        updateView(canvas, gameDots); // draw using the canvas
                    }
                    Thread.sleep(1); // if you want to slow down the action...
                } catch (InterruptedException ex) {
                    Log.e(TAG,ex.toString());
                }
                finally  // regardless if any errors happen...
                {
                    // make sure we unlock canvas so other threads can use it
                    if (canvas != null)
                        surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    private void initDots(Canvas canvas){
        int tenthWidth = canvas.getWidth()/10;
        int tenthHeight = canvas.getHeight()/10;
        int calculatedRadius = tenthWidth/2;
        for(int i = 0; i <=4; i++){
            int verticalPoint = (i*(2*tenthHeight))+tenthHeight;
            for(int t = 0; t <= 4; t++){
                gameDots.add(new Circle(t*(2*tenthWidth)+tenthWidth,verticalPoint,calculatedRadius));
            }
        }
        int targetDot = r.nextInt(gameDots.size()-1);
        gameDots.get(targetDot).setTarget(true);
        previousIndex = targetDot;
        previousSize = gameDots.size();
        dotsSet=true;
    }
}
