package edu.augustana.csc490.connections;

import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by jonathonm on 3/30/2015.
 */
public class Circle {
    private int xCenter;
    private int yCenter;
    private int circleRadius;
    private boolean isTarget;
    private Paint currentColor = new Paint();
    private int nonTargetColor = Color.BLACK;
    private int targetColor = Color.RED;

    public Circle(int x, int y, int radius){
        xCenter = x;
        yCenter = y;
        circleRadius=radius;
        isTarget = false;
        currentColor.setColor(nonTargetColor);
    }

    public int getX(){
        return xCenter;
    }
    public int getY(){
        return yCenter;
    }
    public int getRadius(){
        return circleRadius;
    }
    public Paint getColor(){return currentColor;}
    public boolean getTarget(){
        return isTarget;
    }
    public void setTarget(boolean status){
        isTarget = status;
        if(isTarget == true){
            currentColor.setColor(targetColor);
        }else{
            currentColor.setColor(nonTargetColor);
        }
    }

    public Boolean pointWithin(int xVal, int yVal){
        boolean inside;
        int leftBound = xCenter - circleRadius;
        int rightBound = xCenter + circleRadius;
        int upperBound = yCenter - circleRadius;
        int lowerBound = yCenter + circleRadius;
        inside = ((xVal >= leftBound) && (xVal <= rightBound)) && ((yVal >= upperBound) && (yVal <= lowerBound)) && isTarget;
        return inside;
    }
}
