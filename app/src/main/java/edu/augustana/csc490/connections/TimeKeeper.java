package edu.augustana.csc490.connections;

/**
 * Created by jonathonm on 4/8/2015.
 */
public class TimeKeeper {

    private long startTIme;
    private long endTime;

    public void start(){
        startTIme=System.nanoTime();
    }
    public void stopTime(){
        endTime = System.nanoTime();
    }
    public long longElapsedTime(){
        return (endTime-startTIme)/1000000;
    }
    public String stringElapsedTime(){
        Long elapsedTime = longElapsedTime() ;
        long ms =elapsedTime%1000;
        long seconds = (elapsedTime/1000)%60;
        long minutes = (elapsedTime/1000)/60;
        String formattedTime = String.format("%d"+":"+"%02d"+"."+"%03d",minutes,seconds,ms);
        return formattedTime;
        //simple test
    }
}
