//Page 99
package com.gamecodeschool.subhunter;

import android.app.Activity;
import android.view.MotionEvent;
import android.os.Bundle;
import android.view.Window;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.Display;
import android.util.Log;
import android.widget.ImageView;
import java.util.Random;
//import java.util.Arraylist;

public class SubHunter extends Activity {

    // These variables can be "seen"
    // throughout the SubHunter class
    int numberHorizontalPixels;
    int numberVerticalPixels;
    int blockSize;
    int gridWidth = 33;
    int gridHeight;
    float horizontalTouched = -100;
    float verticalTouched = -100;
    int subHorizontalPosition;
    int subVerticalPosition;
    boolean hit = false;
    boolean gameOver = false;
    int shotsTaken;
    int distanceFromSub;
    boolean debugging = false;
    float[] pastShotsX = new float[100];
    float[] pastShotsY = new float[100];
    int[] pastShotsDistance = new int[100];


    // Here are all the objects(instances)
// of classes that we need to do some drawing
    ImageView gameView;
    Bitmap blankBitmap;
    Canvas canvas;
    Paint paintBlack;
    Paint paintRed;
    Paint paintBlue;
    Paint paintWhite;
    Paint paintPastShots;

    /*
        Android runs this code just before
        the player sees the app.
        This makes it a good place to add
        the code for the one-time setup phase.
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Get the current device's screen resolution
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        // Initialize our size based variables
        // based on the screen resolution
        numberHorizontalPixels = size.x;
        numberVerticalPixels = size.y;
        blockSize = numberHorizontalPixels / gridWidth;
        gridHeight = numberVerticalPixels / blockSize;

        // Initialize all the objects ready for drawing
        blankBitmap =
                Bitmap.createBitmap(numberHorizontalPixels,
                        numberVerticalPixels,
                        Bitmap.Config.ARGB_8888);
        canvas = new Canvas(blankBitmap);
        gameView = new ImageView(this);
        paintBlack = new Paint();
        paintRed = new Paint();
        paintBlue = new Paint();
        paintWhite = new Paint();
        paintPastShots = new Paint();
        // Tell Android to set our drawing
        // as the view for this app
        setContentView(gameView);

        Log.d("Debugging", "In onCreate");

        newGame();
        draw();
    }
    /*
 This code will execute when a new
 game needs to be started. It will
 happen when the app is first started
 and after the player wins a game.
 */
    public void newGame(){

        Random random = new Random();
        subHorizontalPosition = random.nextInt(gridWidth);
        subVerticalPosition = random.nextInt(gridHeight);
        shotsTaken = 0;

        Log.d("Debugging", "In newGame");}
 /*
 Here we will do all the drawing.
 The grid lines, the HUD and
 the touch indicator
 */
    void draw() {
        gameView.setImageBitmap(blankBitmap);
        // Wipe the screen with a white color
        canvas.drawColor(Color.argb(255, 255, 255, 255));

        // Set paint colors
        paintBlack.setColor(Color.argb(255, 0, 0, 0));
        paintRed.setColor(Color.argb(255, 255, 0, 0));
        paintBlue.setColor(Color.argb(255, 0, 0, 255));
        paintWhite.setColor(Color.argb(255, 255, 255, 255));
        paintPastShots.setColor(Color.argb(255, 0, 0, 0));
        if (debugging) {
            printDebuggingText();
        }
// Draw the vertical lines of the grid
        for(int i = 0; i < gridWidth; i++){
            canvas.drawLine(blockSize * i, 0,
                    blockSize * i, numberVerticalPixels,
                    paintBlack);
        }
// Draw the horizontal lines of the grid
        for(int i = 0; i < gridHeight; i++){
            canvas.drawLine(0, blockSize * i,
                    numberHorizontalPixels, blockSize * i,
                    paintBlack);
        }
// Draw the player's past shots
        if (shotsTaken > 0) {
            for (int i = 0; i < shotsTaken; i++) {
                canvas.drawRect(pastShotsX[i] * blockSize,
                        pastShotsY[i] * blockSize,
                        (pastShotsX[i] * blockSize) + blockSize,
                        (pastShotsY[i] * blockSize) + blockSize,
                        paintRed);
// draws a black cross on past shots
                canvas.drawLine(pastShotsX[i] * blockSize, pastShotsY[i] * blockSize,
                        (pastShotsX[i] * blockSize) + blockSize, (pastShotsY[i] * blockSize) + blockSize,
                        paintBlack);
                canvas.drawLine(pastShotsX[i] * blockSize, (pastShotsY[i] * blockSize) + blockSize,
                        (pastShotsX[i] * blockSize) + blockSize, pastShotsY[i] * blockSize,
                        paintBlack);
                if (shotsTaken == 1) {
                    paintBlue.setColor(Color.argb(0, 0, 0, 255));
                    canvas.drawText("______" + pastShotsDistance[i + 1],
                            pastShotsX[i] * blockSize, (pastShotsY[i] * blockSize) + blockSize,
                            paintBlue);
                    paintBlue.setColor(Color.argb(255, 0, 0, 255));
                     }
                else {
                    paintBlue.setColor(Color.argb(0, 0, 0, 255));
                    canvas.drawText("______" + pastShotsDistance[i + 1],
                            pastShotsX[i] * blockSize, (pastShotsY[i] * blockSize) + blockSize,
                            paintBlue);
                    paintBlue.setColor(Color.argb(255, 0, 0, 255));
                    }
            }
        }

        // Draw the player's shot
        paintRed.setColor(Color.argb(200, 255, 0, 0));
        canvas.drawRect(horizontalTouched * blockSize,
                verticalTouched * blockSize,
                (horizontalTouched * blockSize) + blockSize,
                (verticalTouched * blockSize)+ blockSize,
                paintRed);
        paintBlack.setTextSize(blockSize * 1);
        paintPastShots.setTextSize(blockSize * 0.9f);
        paintPastShots.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(
                   "" + distanceFromSub,
                (blockSize * horizontalTouched) + (blockSize / 2),
                blockSize * (verticalTouched) + (blockSize / 2) - ((paintPastShots.descent() + paintPastShots.ascent())/ 2),
                paintPastShots);

        // Re-size the text appropriate for the
        // score and distance text
        paintBlack.setTextSize(blockSize * 1);
        paintBlue.setTextSize(blockSize * 1);
        canvas.drawText(
                "Shots Taken: " + shotsTaken +
                        " Distance to submarine: " + distanceFromSub,
                blockSize * 1.00f, blockSize * 0.9f,
                paintBlue);
// Copyright text
        paintBlack.setTextAlign(Paint.Align.RIGHT);
        paintBlack.setTextSize(blockSize * 0.7f);
        canvas.drawText(
                "Copyright: PavelD (2023)",
                numberHorizontalPixels, blockSize * 0.9f,
                paintBlack);
        paintBlack.setTextAlign(Paint.Align.LEFT);
        paintBlack.setTextSize(blockSize * 1);
        Log.d("Debugging", "In draw");

    }

 /*
 This part of the code will
 handle detecting that the player
 has tapped the screen
 */
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        Log.d("Debugging", "In onTouchEvent");
        // Has the player removed their finger from the screen?
        if((motionEvent.getAction() &
                MotionEvent.ACTION_MASK)
                == MotionEvent.ACTION_UP) {
            // Process the player's shot by passing the
            // coordinates of the player's finger to takeShot
            takeShot(motionEvent.getX(), motionEvent.getY());
        }
        return true;
    }
/*
 The code here will execute when
 the player taps the screen. It will
 calculate the distance from the sub'
 and decide a hit or miss
 */
    void takeShot(float touchX, float touchY){
        Log.d("Debugging", "In takeShot");

// Convert the float screen coordinates
// into int grid coordinates
        horizontalTouched = (int)touchX/ blockSize;
        verticalTouched = (int)touchY/ blockSize;
        pastShotsX[shotsTaken] = horizontalTouched;
        pastShotsY[shotsTaken] = verticalTouched;
        if (distanceFromSub != 0) {
            pastShotsDistance[shotsTaken] = distanceFromSub;
        }
        // Did the shot hit the sub?
        hit = horizontalTouched == subHorizontalPosition
                && verticalTouched == subVerticalPosition;
// How far away horizontally and vertically
// was the shot from the sub
        int horizontalGap = (int)horizontalTouched -
                subHorizontalPosition;
        int verticalGap = (int)verticalTouched -
                subVerticalPosition;
// Use Pythagoras's theorem to get the
// distance travelled in a straight line
        distanceFromSub = (int)Math.sqrt(
                ((horizontalGap * horizontalGap) +
                        (verticalGap * verticalGap)));

        if (shotsTaken == 0) {
            System.out.println("START OF THE GAME\n\n\n");
            canvas.drawText(
                    "Start of the game" + distanceFromSub,
                    blockSize * 1, blockSize * 1,
                    paintBlack);
        }

// Add one to the shotsTaken variable
        shotsTaken ++;
// If there is a hit call boom
        if(hit) {
            gameOver = true;
            boom();
        }
// Otherwise call draw as usual
        else {
            gameOver = false;
            draw();
        }
    }
    // This code says "BOOM!"
    void boom(){
        gameView.setImageBitmap(blankBitmap);
// Wipe the screen with a red color
        canvas.drawColor(Color.argb(255, 255, 0, 0));
// Draw some huge white text
        paintWhite.setColor(Color.argb(255, 255, 255, 255));
        paintWhite.setTextSize(blockSize * 4);
        paintWhite.setTextAlign(Paint.Align.CENTER);

        canvas.drawText("BOOM!", (numberHorizontalPixels / 2),
                (blockSize / 2) - paintWhite.ascent(), paintWhite);
// Draw how many shots player needed
        paintWhite.setTextSize(blockSize * 2);
        canvas.drawText("Shots: " + shotsTaken,
                (numberHorizontalPixels / 2),
                (numberVerticalPixels / 2) - ((paintWhite.descent() + paintWhite.ascent()) / 2), paintWhite);
        paintWhite.setTextSize(blockSize * 2);

// Draw some text to prompt restarting
        canvas.drawText("Take a shot to start again", numberHorizontalPixels / 2,
                numberVerticalPixels - blockSize, paintWhite);
        paintBlack.setTextSize(blockSize * 1);
// Start a new game
        newGame();
    }
    // This code prints the debugging text
    void printDebuggingText(){
        paintBlack.setTextSize(blockSize);
        paintRed.setTextSize(blockSize);
        canvas.drawText("numberHorizontalPixels = "
                        + numberHorizontalPixels,
                50, blockSize * 3, paintBlack);
        canvas.drawText("numberVerticalPixels = "
                        + numberVerticalPixels,
                50, blockSize * 4, paintBlack);
        canvas.drawText("blockSize = " + blockSize,
                50, blockSize * 5, paintBlack);
        canvas.drawText("gridWidth = " + gridWidth,
                50, blockSize * 6, paintBlack);
        canvas.drawText("gridHeight = " + gridHeight,
                50, blockSize * 7, paintBlack);
        canvas.drawText("horizontalTouched = " +
                        horizontalTouched, 50,
                blockSize * 8, paintBlack);
        canvas.drawText("Distance = " +
                            distanceFromSub, 50,
                    blockSize * 9, paintBlack);
        canvas.drawText("verticalTouched = " +
                        verticalTouched, 50,
                blockSize * 10, paintBlack);
        canvas.drawText("subHorizontalPosition = " +
                        subHorizontalPosition, 50,
                blockSize * 11, paintRed);
        canvas.drawText("subVerticalPosition = " +
                        subVerticalPosition, 50,
                blockSize * 12, paintRed);
        //canvas.drawText("game over = " + gameOver,
//                50, blockSize * 13, paint);
        canvas.drawText("shotsTaken = " +
                        shotsTaken,
                50, blockSize * 14, paintBlue);
        canvas.drawText("(gridWidth / 2) = " + (gridWidth / 2),
                50, blockSize * 15, paintBlue);
        canvas.drawRect(subHorizontalPosition * blockSize,
                subVerticalPosition * blockSize,
                (subHorizontalPosition * blockSize) + blockSize,
                (subVerticalPosition * blockSize)+ blockSize,
                paintBlack);
    }



}

