package edu.augustana.csc490.connections;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.ArrayList;

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
    //If using an emulator change this delay to something smaller like 10 or so
    private static final int CHANGE_DELAY = 50;
    private int currentDelay;
    private int previousSize;
    private int previousIndex;

    private ArrayList<Circle> gameDots;

    private Paint myPaint;
    private Paint backgroundPaint;

    public PrimaryGameView(Context context, AttributeSet atts)
    {

        super(context, atts);
        mainActivity = (Activity) context;


        getHolder().addCallback(this);
        gameDots =  new ArrayList<>(1);
        myPaint = new Paint();
        myPaint.setColor(Color.BLUE);
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.WHITE);
        currentDelay=CHANGE_DELAY;
        keeper = new TimeKeeper();
    }

    // called when the size changes (and first time, when view is created)
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        dotsSet = false;


        startNewGame();
        keeper.start();
    }

    public void startNewGame()
    {


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
                currentDelay--;
                if (currentDelay == 0) {
                    if(gameDots.size()>1) {
                        int nextCircle = r.nextInt(gameDots.size());
                        gameDots.get(nextCircle).setTarget(true);
                        if (gameDots.size() == previousSize) {
                            if (nextCircle != previousIndex) {

                                gameDots.get(previousIndex).setTarget(false);
                            }
                        }
                        previousIndex = nextCircle;
                        previousSize = gameDots.size();
                        currentDelay=CHANGE_DELAY;
                    }else if(gameDots.size()<=1){
                        gameDots.get(0).setTarget(true);
                    }
                }
                for (Circle d : dots) {
                    canvas.drawCircle(d.getX(), d.getY(), (float) d.getRadius(), d.getColor());
                }

            }
        }
    }

    public void stopGame()
    {
        if (gameThread != null)
            gameThread.setRunning(false);
    }

    public void releaseResources()
    {
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
//                        intent.putExtra("stringTime", keeper.stringElapsedTime());
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
/*In a nutshell this method takes a canvas of a given size and calculates where
    to place 25 identical dots equal distance apart.
*/
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
