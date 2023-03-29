//Page 99
package com.gamecodeschool.subhunter;

import android.app.Activity;
import android.icu.text.DecimalFormat;
import android.os.Build;
import android.text.TextUtils;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
//import java.util.Arraylist;

public class UbootJaeger extends Activity {

    // These variables can be "seen"
    // throughout the SubHunter class
    int numberHorizontalPixels;
    int numberVerticalPixels;
    int blockSize;
    int a = 2220;
    int b = 1014;
    int gridWidth = 40;
    int gridHeight;

    // original value, works ok
    //float horizontalTouched = -100;
    int horizontalTouched = -100;

    //-100 initial position of touched horizontal (shows ShotsTaken 0);
    int verticalTouched = -100;
    int subHorizontalPosition;
    int subVerticalPosition;
    boolean hit = false;
    int shotsTaken;
    int distanceFromSub;

    boolean sameX, sameY;

    //used for debugging to show array output
    String arrayPastShotsOutput;
    boolean debugging = false;
    boolean showSub = false;
//  this 3D array to replace three 1D arrays
    //int[][][] pastShots = new int[10000][10000][10000];
    int[] pastShotsX = new int[2000];
    int[] pastShotsY = new int[2000];
    // = new int[10000]
    int[] pastShotsDistance = new int[2000];
    //int[][][] pastShotsArray = new int[1][1][1];

    ArrayList<Integer> pastShotsArrayList = new ArrayList<>();

    List<Integer[]> pastShotsList = new ArrayList<>();

    List<List<Integer>> pastShotsListOfLists = new ArrayList<>();
    int debuggingCountClick = 0; //helps to activate debugging screen during gameplay

    float touchX;
    float touchY;

    float numberOfGamesPlayed = 0;

    float totalNumberOfShotsForAllGames = 0;

    float averageNumberOfShotsForAllGames;
    // Here are all the objects(instances)
// of classes that we need to do some drawing
    ImageView gameView;
    Bitmap blankBitmap;
    Canvas canvas;
    Paint paintBlack;
    Paint paintBlackCircle;
    Paint paintRed;
    Paint paintRedTrans; // semi transparent red colour, used for boom screen rectangle, pastshots
    //used for debugging in half size of the blocksize
    Paint paintRedHalfSize;
    Paint paintBlackTrans; // semi transparent black shadow colour
    Paint paintBoomBlackTrans; // semi transparent black shadow colour, used for boom screen rectangle
    Paint paintBlue;
    Paint paintGreen;
    Paint paintGreyTrans;
    Paint paintWhite;
    Paint paintPastShots;

    // color set to cover up with single blocks the "shots:" text for boom screen
    Paint paintBackgroundColor;

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
        int a = 12, b = 24;
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
        paintRedTrans = new Paint();
        paintRedHalfSize = new Paint();
        paintBlackTrans = new Paint();
        paintBlackCircle = new Paint();
        paintBlue = new Paint();
        paintGreen = new Paint();
        paintGreyTrans = new Paint();
        paintWhite = new Paint();
        paintPastShots = new Paint();
        paintBackgroundColor = new Paint();
        paintBoomBlackTrans = new Paint();
        // Tell Android to set our drawing as the view for this app
        setContentView(gameView);

//        Log.d("Debugging", "In onCreate");

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
// reinitialize Arrays pastShotsX, pastShotsY and pastShotsDistance
        Arrays.fill(pastShotsX, 0);
        Arrays.fill(pastShotsY, 0);
        Arrays.fill(pastShotsDistance, 0);
//        Log.d("Debugging", "In newGame");

    }
 /*
 Here we will do all the drawing.
 The grid lines, the HUD and
 the touch indicator
 */
    void draw() {

        gameView.setImageBitmap(blankBitmap);
        // Wipe the screen with a white color
        canvas.drawColor(Color.argb(255, 235, 235, 235));

        // Set paint colors
        paintBlack.setColor(Color.argb(255, 0, 0, 0));
        paintRed.setColor(Color.argb(255, 255, 0, 0));
        paintRedTrans.setColor(Color.argb(115, 255, 0, 0));
        paintRedHalfSize.setColor(Color.argb(255, 255, 0, 0));
        paintBlackTrans.setColor(Color.argb(95, 0, 0, 0));
        paintBoomBlackTrans.setColor(Color.argb(95, 0, 0, 0));

        paintBlue.setColor(Color.argb(255, 0, 0, 255));
        paintGreen.setColor(Color.argb(255, 0, 255, 0));
        paintWhite.setColor(Color.argb(215, 255, 255, 255));
        paintGreyTrans.setColor(Color.argb(175, 80, 80, 80));
        paintPastShots.setColor(Color.argb(255, 0, 0, 0));
        paintBlackCircle.setColor(Color.argb(255, 0, 0, 0));
        paintBlackCircle.setStyle(Paint.Style.STROKE);
        //this background color needs to match the canvas color
        paintBackgroundColor.setColor(Color.argb(255, 235, 235, 235));
        // Set paint font sizes
        paintRedHalfSize.setTextSize(blockSize * 0.7F);


        if (debugging) {
            printDebuggingText();
        }
        if (showSub) {
            showSub();
        }
// Draw the vertical lines of the grid
        for(int i = 0; i <= gridWidth; i++){
            canvas.drawLine(blockSize * i, 0,
                    blockSize * i, numberVerticalPixels - (numberVerticalPixels - (blockSize * gridHeight)),
                    paintBlack);
        }
// Draw the horizontal lines of the grid
        for(int i = 0; i <= gridHeight; i++){
            canvas.drawLine(0, blockSize * i,
                    numberHorizontalPixels - (numberHorizontalPixels - (blockSize * gridWidth)), blockSize * i,
                    paintBlack);
        }
        // shows the number of shots taken.
        paintBlack.setTextSize(blockSize * 0.9F);
        paintBlack.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(
                "Shots:" + shotsTaken,
                blockSize * 1.00f, blockSize * 0.9f,
                paintBlack);

        paintRed.setColor(Color.argb(255, 255, 0, 0));
        paintGreen.setTextSize(blockSize * 1);
        paintGreen.setTextAlign(Paint.Align.CENTER);
        paintBlack.setTextSize(blockSize * 0.9F);
        paintPastShots.setTextSize(blockSize * 0.9f);
        paintPastShots.setTextAlign(Paint.Align.CENTER);
        paintBlack.setTextAlign(Paint.Align.LEFT);
        paintBlack.setTextAlign(Paint.Align.LEFT);
        paintBlack.setTextSize(blockSize * 1);
        //Log.d("Debugging", "In draw");
        if (shotsTaken == 0) {

// shows welcome screen with a white color, transparent, obsolete
            canvas.drawColor(Color.argb(0, 255, 0, 0));
            gameView.setImageBitmap(blankBitmap);
//new game screen background
            canvas.drawRect(
                    blockSize + 0.3F * blockSize,
                    blockSize + 0.3F * blockSize,
                    numberHorizontalPixels - 0.7F * blockSize,
                    numberVerticalPixels - 0.7F * blockSize,
                    paintBlackTrans);
//new game screen background shadow
            canvas.drawRect(
                    1 * blockSize,
                    1 * blockSize,
                    numberHorizontalPixels - blockSize,
                    numberVerticalPixels - blockSize,
                    paintGreyTrans);
// Draw the name of the game
            paintWhite.setTextSize(blockSize * 4);
            paintWhite.setTextAlign(Paint.Align.CENTER);

            canvas.drawText("Submarine Hunter", (numberHorizontalPixels / 2),
                    (blockSize / 2) +blockSize - paintWhite.ascent(), paintWhite);
// Draw text "PavelD(2023) v1.61" at the start top left of grey screen
            paintWhite.setTextAlign(Paint.Align.LEFT);
            paintWhite.setTextSize(blockSize * 0.7f);
            canvas.drawText(
                    "PavelD(2023) v1.71",
                    1.4F * blockSize ,
                    blockSize * 2f,
                    paintWhite);
            paintWhite.setTextAlign(Paint.Align.CENTER);
// Draw text "thanks to gamecodeschool" at the start top right of grey screen
            paintWhite.setTextAlign(Paint.Align.RIGHT);
            paintWhite.setTextSize(blockSize * 0.7f);
            canvas.drawText(
                    "thanks to gamecodeschool",
                    (1 * numberHorizontalPixels) - (numberHorizontalPixels - (gridWidth * blockSize - 1.1F * blockSize)) ,
                    blockSize * 2f,
                    paintWhite);
            paintWhite.setTextAlign(Paint.Align.CENTER);
// Draw the description of the game
            paintWhite.setTextSize(blockSize * 1.5F);
            canvas.drawText("a very simple yet interesting game",
                    (numberHorizontalPixels / 2),
                    (numberVerticalPixels / 2) - 2.5F *blockSize - ((paintWhite.descent() + paintWhite.ascent()) / 2), paintWhite);
            paintWhite.setTextSize(blockSize * 2);

// Draw some text to prompt to start the game
            paintBlue.setTextSize(blockSize * 1.75F);
            paintBlue.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("Click on the screen to start",
                    (numberHorizontalPixels / 2),
                    (numberVerticalPixels / 2) + 2* blockSize - ((paintWhite.descent() + paintWhite.ascent()) / 2), paintBlue);
            paintWhite.setTextSize(blockSize * 2);

// Draw howto text
            paintWhite.setTextSize(blockSize * 1);
            canvas.drawText("The number on each shot will tell you how far the submarine is hiding from you.", numberHorizontalPixels / 2,
                    numberVerticalPixels - (blockSize * 2), paintWhite);
            paintBlack.setTextSize(blockSize * 1);

        }
// Draw the player's past shots
        if (shotsTaken > 0) {
            for (int i = 0; i < shotsTaken; i++) {
//changed transparency of paintRedtrans color to 85, for past shots blocks
                paintRedTrans.setColor(Color.argb(85, 255, 0, 0));

                canvas.drawRect(
                        pastShotsX[i] * blockSize,
                        pastShotsY[i] * blockSize,
                        (pastShotsX[i] * blockSize) + blockSize,
                        (pastShotsY[i] * blockSize) + blockSize,
                        paintRedTrans);
////changed transparency of paintRedtrans back to default
                paintRedTrans.setColor(Color.argb(115, 255, 0, 0));
// draws a black cross and distances on past shots
//                canvas.drawLine(pastShotsX[i] * blockSize - 0.3F * blockSize, pastShotsY[i] * blockSize - 0.3F * blockSize,
//                        (pastShotsX[i] * blockSize) + 1.3F * blockSize, (pastShotsY[i] * blockSize) + 1.3F * blockSize,
//                        paintBlack);
//                canvas.drawLine(pastShotsX[i] * blockSize, (pastShotsY[i] * blockSize) + blockSize,
//                        (pastShotsX[i] * blockSize) + blockSize, pastShotsY[i] * blockSize,
//                        paintBlack);
            }
//draw distance-to-sub numbers on past shots squares exclusive the current shot
            for (int i = 0; i < shotsTaken; i++) {
            //for (int i = 0; i < shotsTaken; i++) {
                paintBlack.setTextAlign(Paint.Align.CENTER);
                paintBlackTrans.setTextAlign(Paint.Align.CENTER);
                paintBlackTrans.setTextSize(blockSize * 0.9F);
                    // invisible number on the current shot, distance = 0
                    if (pastShotsDistance[i+1] == 0 ) {
                        paintBlackTrans.setColor(Color.argb(0, 0, 0, 0));
// draws current shot by the player, only the block
                        canvas.drawRect(
                                pastShotsX[i] * blockSize,
                                pastShotsY[i] * blockSize,
                                (pastShotsX[i] * blockSize) + blockSize,
                                (pastShotsY[i] * blockSize) + blockSize,
                                paintRed);
                    }
                    canvas.drawText("" + pastShotsDistance[i+1],
                            pastShotsX[i] * blockSize  + (blockSize / 2),
                            (pastShotsY[i] * blockSize) + (blockSize / 2) - ((paintPastShots.descent() + paintPastShots.ascent())/ 2),
                            paintBlackTrans);
                }
            }
// Draw the player shot' distance number on the block
        paintBlack.setTextSize(blockSize * 0.9F);
        paintBlack.setColor(Color.argb(255, 0, 0, 0));
        canvas.drawText(
                "" + distanceFromSub,
                (blockSize * horizontalTouched) + (blockSize / 2),
                blockSize * (verticalTouched) + (blockSize / 2) - ((paintPastShots.descent() + paintPastShots.ascent())/ 2),
                paintBlack);

        // Draw a circle showing are where submarine is
        /*canvas.drawArc(blockSize/2 + blockSize * (horizontalTouched - distanceFromSub),
                blockSize/2 + blockSize * (verticalTouched + distanceFromSub),
                blockSize/2 + blockSize * (horizontalTouched + (distanceFromSub)),
                blockSize/2 + blockSize * (verticalTouched - (distanceFromSub)),
                0, 360, false, paintBlackCircle);*/
        /*canvas.drawArc( blockSize/2 + (touchX - (distanceFromSub*blockSize)),
                blockSize/2 + touchY - (distanceFromSub*blockSize),
                blockSize/2 + touchX + (distanceFromSub*blockSize),
                blockSize/2 + touchY + (distanceFromSub*blockSize),
                0, 360, false, paintBlackCircle);
        canvas.drawRect(horizontalTouched * blockSize,
                verticalTouched * blockSize,
                (horizontalTouched * blockSize) + blockSize,
                (verticalTouched * blockSize)+ blockSize,
                paintRed);*/
                // Re-size the text appropriate for the amount of shots

    }

 /*
 This part of the code will
 handle detecting that the player
 has tapped the screen
 */
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        //Log.d("Debugging", "In onTouchEvent");
        // Has the player removed their finger from the screen?
        if((motionEvent.getAction() &
                MotionEvent.ACTION_MASK)
                == MotionEvent.ACTION_UP) {
            // Process the player's shot by passing the
            // coordinates of the player's finger to takeShot
            takeShot(motionEvent.getX(), motionEvent.getY());

            touchX = motionEvent.getX();
            touchY = motionEvent.getY();
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
//      Log.d("Debugging", "In takeShot");

// Convert the float screen coordinates
// into int grid coordinates
        horizontalTouched = (int)touchX/ blockSize;
        verticalTouched = (int)touchY/ blockSize;
// check whether block on 1:1 coordinates was clicked, if yes, debuggingCountClick ++
        if (horizontalTouched != 0 && verticalTouched != 0) {
            debuggingCountClick = 0;
        }
        if (horizontalTouched == 0 && verticalTouched == 0) {
            debuggingCountClick++;
        }
// shows submarine position if top left block is clicked 12 times
        if (debuggingCountClick == 12) {
            showSub = true;
        }
// shows debugging information if top left block is clicked 18 times
        if (debuggingCountClick == 18) {
            debugging = true;
        }
// hides debugging information and submarine position if top left block is clicked 24 times
        if (debuggingCountClick == 24) {
            debugging = false;
            showSub = false;
        }
// position of the current shot will be saved into pastShotsX and pastShotsY 1D arrays
        pastShotsX[shotsTaken] = horizontalTouched;
        pastShotsY[shotsTaken] = verticalTouched;
// if the distance to sub is other than zero, distance from sub is saved to pastShotsdistance 1D array
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

// Add one to the shotsTaken variable if the block hasn't been clicked already
        shotsTaken ++;
        paintBlack.setTextSize(blockSize * 0.9F);
        paintBlue.setTextSize(blockSize * 1);
        paintBlack.setColor(Color.argb(255, 0, 0, 0));
        paintBlue.setColor(Color.argb(255, 0, 0, 255));

// log output of current shot and pastShotsArrayList
        if (shotsTaken >0) {
            pastShotsArrayList.add(horizontalTouched);
            pastShotsArrayList.add(verticalTouched);
            pastShotsArrayList.add(distanceFromSub);
//PastShotsArray to string
            String joined = TextUtils.join("-", pastShotsArrayList);
            String rawData = " X:" + horizontalTouched + " Y:" + verticalTouched + " D:" + distanceFromSub;
            Log.d("Debugging", "In onCreate");
            Log.d("current shot", "Values: " + rawData);  // arraylist values
            Log.d("pastShotsArrayList", "Values: " + joined);  // arraylist values

        pastShotsList.add(new Integer[] {horizontalTouched, verticalTouched, distanceFromSub});
        for (final Integer[] coordinates : pastShotsList) {
            System.out.println(Arrays.toString(coordinates));
        //    final int x = coordinates[0];
        //    final int y = coordinates[1];
        //    final int d = coordinates[2];
        }
        String joinedList = TextUtils.join(",", pastShotsList.get(shotsTaken-1));
        Log.d("pastShotsArrayListBaum", "Values: " + joinedList);  // arraylist values
        //final int x3 = pastShotsList.get(3)[0];
        //final int y5 = pastShotsList.get(5)[1];
        //final int d7 = pastShotsList.get(7)[2];



            pastShotsListOfLists.add(Arrays.asList(horizontalTouched, verticalTouched, distanceFromSub));
            for (final List<Integer> coordinates : pastShotsListOfLists) {
                System.out.println(coordinates);
             //   final int x = coordinates.get(0);
             //   final int y = coordinates.get(1);
             //   final int d = coordinates.get(2);
            }
            String joinedListOfLists = TextUtils.join(".", pastShotsListOfLists);
            Log.d("pastShotsListOfLists", "Values: " + joinedListOfLists);  // arraylist values
            //final int x3 = pastShotsListOfLists.get(3).get(0);
            //final int y5 = pastShotsListOfLists.get(5).get(1);
            //final int d7 = pastShotsListOfLists.get(7).get(2);
        }
// If there is a hit call boom
        if(hit) {
            boom();
        }
// Otherwise call draw as usual
        else {
            draw();
        }
    }
    // This code says "EXPLOSION!"
    void boom(){
    numberOfGamesPlayed++;
    totalNumberOfShotsForAllGames = totalNumberOfShotsForAllGames + shotsTaken;
    averageNumberOfShotsForAllGames = totalNumberOfShotsForAllGames / numberOfGamesPlayed;
        DecimalFormat df = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            df = new DecimalFormat();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            df.setMaximumFractionDigits(2);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // System.out.println(df.format(averageNumberOfShotsForAllGames));
        }
// Wipe the screen with a red color, transparent with shadow
        canvas.drawColor(Color.argb(0, 255, 0, 0));
        gameView.setImageBitmap(blankBitmap);
// draws red boom area's shadow
        canvas.drawRect(
                1 * blockSize + 0.3F * blockSize,
                1 * blockSize + 0.3F * blockSize,
                numberHorizontalPixels - 0.7F * blockSize,
                numberVerticalPixels - 0.7F * blockSize,
                paintBoomBlackTrans);
// draws red boom area
        canvas.drawRect( blockSize, blockSize,
                numberHorizontalPixels - blockSize,
                numberVerticalPixels - blockSize,
                paintRedTrans);
// paint 4 white blocks over text "shots:" to cover up for shots-1 error
        paintBlack.setColor(Color.argb(255, 0, 0, 0));
        canvas.drawRect(1 * blockSize,0,5 * blockSize,1* blockSize,paintBackgroundColor);

// Draw the vertical replacement lines of the grid in top left area
        for(int i = 0; i <= 5; i++){
            canvas.drawLine(blockSize * i, 0,
                    blockSize * i, 1 * blockSize,
                    paintBlack);
        }
// Draw one horizontal replacement line segment of the grid in top left area
        canvas.drawLine(1 * blockSize, 1 * blockSize,
                    5 * blockSize, 1 * blockSize,
                    paintBlack);

//shows the last stand of the shots-taken count after successful hit, top left
        paintBlack.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(
                "Shots:" + shotsTaken,
                blockSize * 1, blockSize * 0.9f,
                paintBlack);
// paint last shot for the boom screen
        canvas.drawRect(horizontalTouched * blockSize,
                verticalTouched * blockSize,
                (horizontalTouched * blockSize) + blockSize,
                (verticalTouched * blockSize)+ blockSize,
                paintBlack);
// Draw some huge white text
        paintWhite.setTextSize(blockSize * 4);
        paintWhite.setTextAlign(Paint.Align.CENTER);

        canvas.drawText("EXPLOSION!", (numberHorizontalPixels / 2),
                (blockSize / 2) +blockSize - paintWhite.ascent(), paintWhite);
// Draw how many shots player needed on the boom screen
        paintWhite.setTextSize(blockSize * 2);
        canvas.drawText("Shots: " + shotsTaken,
                (numberHorizontalPixels / 2),
                (numberVerticalPixels / 2) +blockSize - ((paintWhite.descent() + paintWhite.ascent()) / 2), paintWhite);

        paintWhite.setTextSize(blockSize * 1.5F);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // System.out.println(df.format(averageNumberOfShotsForAllGames));
            canvas.drawText("Average Number of Shots per all games: " + df.format(averageNumberOfShotsForAllGames),
                    (numberHorizontalPixels / 2),
                    (numberVerticalPixels / 2) + (3 * blockSize) - ((paintWhite.descent() + paintWhite.ascent()) / 2),
                    paintWhite);
        }

        paintWhite.setTextSize(blockSize * 2);

// Draw some text to prompt restarting on the boom screen
        canvas.drawText("Click on the screen to start again", numberHorizontalPixels / 2,
                numberVerticalPixels - (blockSize * 2), paintWhite);
        paintBlack.setTextSize(blockSize * 1);
// variable to output pastshosts for onscreen debugging
        String arrayPastShotsOutput;
// Start a new game
        newGame();
    }

    // this code shows the location of the Submarine, shows count clicks
    void showSub(){
        paintBlack.setColor(Color.argb(175, 0, 0, 0));
        paintRed.setColor(Color.argb(175, 255, 0, 0));
        paintBlack.setTextSize(0.7F * blockSize);
        paintRed.setTextSize(blockSize);
        paintBlack.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Top Left Block Click Count: " + debuggingCountClick,
                gridWidth * blockSize / 2, 1.5F * blockSize, paintBlack);
        paintBlack.setTextSize(0.6F * blockSize);
        canvas.drawText("24x click to hide all hacks",
                gridWidth * blockSize / 2, 2 * blockSize, paintBlack);
        paintBlack.setTextAlign(Paint.Align.LEFT);
        paintBlack.setTextSize(blockSize);
        canvas.drawRect(subHorizontalPosition * blockSize,
                subVerticalPosition * blockSize,
                (subHorizontalPosition * blockSize) + blockSize,
                (subVerticalPosition * blockSize)+ blockSize,
                paintBlack);
        paintBlack.setColor(Color.argb(255, 0, 0, 0));
        paintRed.setColor(Color.argb(255, 255, 0, 0));
    }
    // This code prints the debugging text
    void printDebuggingText() {
        paintBlack.setColor(Color.argb(175, 0, 0, 0));
        paintRed.setColor(Color.argb(175, 255, 0, 0));
        paintBlack.setTextAlign(Paint.Align.LEFT);
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
        canvas.drawText("verticalTouched = " +
                        verticalTouched, 50,
                blockSize * 9, paintBlack);
        canvas.drawText("subHorizontalPosition = " +
                        subHorizontalPosition + "(" + subHorizontalPosition * blockSize + "px)", 50,
                blockSize * 10, paintRed);
        canvas.drawText("subVerticalPosition = " +
                        subVerticalPosition + "(" + subVerticalPosition * blockSize + "px)", 50,
                blockSize * 11, paintRed);
        canvas.drawText("touchX = " +
                        touchX, 50,
                blockSize * 12, paintRed);
        canvas.drawText("touchY = " +
                        touchY, 50,
                blockSize * 13, paintRed);
/*        canvas.drawText("sameX = " +
                        sameX, 50,
                blockSize * 14, paintRed);
        canvas.drawText("sameY = " +
                        sameY, 50,
                blockSize * 15, paintRed);
*/
//        arrayPastShotsOutput = "";
//        for (int i = 0; i < shotsTaken; i++) {
//            arrayPastShotsOutput += ("[" + pastShotsX[i] + " " + pastShotsY[i] + " " + pastShotsDistance[i] + "]");
//            canvas.drawText("pastShotsArray[X Y D] = " + arrayPastShotsOutput,
//                    50, blockSize * 16, paintRedHalfSize);
//        }

//                50, blockSize * 13, paint);
        canvas.drawText("Total amount of Shots : " + totalNumberOfShotsForAllGames,
                blockSize * 18, blockSize * 3, paintBlack);
        canvas.drawText("number of games played: " + numberOfGamesPlayed,
                blockSize * 18, blockSize * 4, paintBlack);
        canvas.drawText("Average: " + averageNumberOfShotsForAllGames,
                blockSize * 18, blockSize * 5, paintBlack);
        canvas.drawText("DebuggingCountClick: " + debuggingCountClick,
                blockSize * 18, blockSize * 6, paintBlack);
//draws sub block for debugging
        canvas.drawRect(subHorizontalPosition * blockSize,
                subVerticalPosition * blockSize,
                (subHorizontalPosition * blockSize) + blockSize,
                (subVerticalPosition * blockSize) + blockSize,
                paintBlack);
//debugging testing for duplicate location shots
/*        paintBlack.setTextSize(0.6F * blockSize);
        canvas.drawText("pastShotsX: X " + "touchedX: " + horizontalTouched + " sameX: " + sameX, numberHorizontalPixels / 2 + 8 * blockSize,
                (blockSize / 2) * (9), paintBlack);
        if (shotsTaken == 0 | shotsTaken == 1) {
            sameX = false;
        }
        else {
            for (int i = 0; i < shotsTaken; i++) {
                if (pastShotsX[i] == (int) horizontalTouched) {
                    sameX = true;
                } else {
                    sameX = false;

                }
                canvas.drawText("pastShotsX = " +
                                pastShotsX[i] + " touchedX: " + horizontalTouched + " sameX: " + sameX, numberHorizontalPixels / 2 + 8 * blockSize,
                        (blockSize / 2) * (10 + i), paintBlack);
            }
            paintBlack.setTextSize(blockSize);

        }*/
        paintBlack.setColor(Color.argb(255, 0, 0, 0));
        paintRed.setColor(Color.argb(255, 255, 0, 0));
    }
}