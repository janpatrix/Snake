package com.example.patrickgross.snake;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

public class GameActivity extends Activity {

    Canvas canvas;
    SnakeView snakeView;

    Bitmap headBitmap;
    Bitmap bodyBitmap;
    Bitmap tailBitmap;
    Bitmap appleBitmap;

    int directionOfTravel = 0;

    int screenWidth;
    int screenHeight;
    int topGap;

    long lastFrameTime;
    int fps;
    int score;
    int hi;

    int [] snakeX;
    int [] snakeY;
    int snakeLength;

    int appleX;
    int appleY;

    int blockSize;
    int numBlocksWide;
    int numBlocksHigh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureDisplay();
        snakeView = new SnakeView(this);
        setContentView(snakeView);
    }

    public void configureDisplay(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        topGap = screenHeight / 14;
        blockSize = screenWidth/40;
        numBlocksWide = 40;
        numBlocksHigh = (screenHeight - topGap)/blockSize;

        headBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.head);
        tailBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tail);
        bodyBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.body);
        appleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.apple);

        headBitmap = Bitmap.createScaledBitmap(headBitmap, blockSize, blockSize, false);
        tailBitmap = Bitmap.createScaledBitmap(tailBitmap, blockSize, blockSize, false);
        bodyBitmap = Bitmap.createScaledBitmap(bodyBitmap, blockSize, blockSize, false);
        appleBitmap = Bitmap.createScaledBitmap(appleBitmap, blockSize, blockSize, false);



    }

    @Override
    protected void onStop() {
        super.onStop();
        while (true) {
            snakeView.pause();
            break;
        }
        finish();
    }


    @Override
    protected void onResume(){
        super.onResume();
        snakeView.resume();
    }

    @Override
    protected void onPause(){
        super.onPause();
        snakeView.pause();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            snakeView.pause();

            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
            return true;
        }
        return false;
    }



    class SnakeView extends SurfaceView implements Runnable{

        Thread ourThread = null;
        SurfaceHolder ourHolder;
        volatile boolean playingSnake;
        Paint paint;

        public SnakeView(Context context) {
            super(context);
            ourHolder = getHolder();
            paint = new Paint();

            snakeX = new int[200];
            snakeY = new int[200];
            getSnake();
            getApple();
        }
        public void getSnake(){
            snakeLength = 3;
            snakeX[0] = numBlocksWide / 2;
            snakeY[0] = numBlocksHigh / 2;

            snakeX[1] = snakeX[0] - 1;
            snakeY[1] = snakeY[0];

            snakeX[2] = snakeX[1] - 1;
            snakeY[2] = snakeY[0];
        }

        public void getApple(){
            Random random = new Random();
            appleX = random.nextInt(numBlocksWide - 1) + 1;
            appleY = random.nextInt(numBlocksHigh - 1) + 1;
        }

        @Override
        public void run(){
            while(playingSnake) {
                updateGame();
                drawGame();
                controlFPS();
            }
        }


        public void pause() {
            playingSnake = false;
            try {
                ourThread.join();
            } catch (InterruptedException e){}
        }

        public void resume() {
            playingSnake = true;
            ourThread = new Thread(this);
            ourThread.start();
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            switch(motionEvent.getAction() & MotionEvent.ACTION_MASK){
                case MotionEvent.ACTION_UP :
                    if(motionEvent.getX() >= screenWidth / 2){
                        directionOfTravel++;
                        if(directionOfTravel == 4){
                            directionOfTravel = 0;
                        }
                    } else {
                        directionOfTravel--;
                        if (directionOfTravel == -1){
                            directionOfTravel = 3;
                        }
                    }
            }
            return true;
        }

        public void controlFPS() {
            long timeThisFrame = (System.currentTimeMillis() - lastFrameTime);
            long timeToSleep = 100 - timeThisFrame;
            if (timeThisFrame > 0) {
                fps = (int)(1000/timeThisFrame);
            }
            if (timeToSleep > 0){
                try {
                    ourThread.sleep(timeToSleep);
                } catch (InterruptedException e) {}
            }
            lastFrameTime = System.currentTimeMillis();
            }

        public void drawGame(){
            if (ourHolder.getSurface().isValid()){
                canvas = ourHolder.lockCanvas();
                canvas.drawColor(Color.BLACK);
                paint.setColor(Color.argb(255, 255, 255, 255));
                paint.setTextSize(topGap/2);
                canvas.drawText("Score = " + score + "Hi: " + hi, 10, topGap - 6, paint);
                paint.setStrokeWidth(3);
                canvas.drawLine(1, topGap, screenWidth - 1, topGap, paint );
                canvas.drawLine(screenWidth - 1, topGap, screenWidth - 1, topGap + (numBlocksHigh*blockSize), paint);
                canvas.drawLine(screenWidth - 1, topGap + (numBlocksHigh*blockSize), 1, topGap + (numBlocksHigh*blockSize), paint);
                canvas.drawLine(1, topGap, 1, topGap + (numBlocksHigh*blockSize), paint);
                canvas.drawBitmap(headBitmap, snakeX[0]*blockSize, (snakeY[0]*blockSize) + topGap, paint);
                for (int i = 1; i < snakeLength - 1; i++){
                    canvas.drawBitmap(bodyBitmap, snakeX[i]*blockSize, (snakeY[i]*blockSize) + topGap, paint);
                }
                canvas.drawBitmap(tailBitmap, snakeX[snakeLength - 1]*blockSize, (snakeY[snakeLength - 1]*blockSize) + topGap, paint);
                canvas.drawBitmap(appleBitmap, appleX * blockSize, (appleY*blockSize) + topGap, paint);
                ourHolder.unlockCanvasAndPost(canvas);
            }
        }

        public void updateGame(){
            if(snakeX[0] == appleX && snakeY[0] == appleY){
                snakeLength ++;
                getApple();
                score += snakeLength;
            }

            for (int i = snakeLength; i > 0; i--){
                snakeX[i] = snakeX[i-1];
                snakeY[i] = snakeY[i-1];
            }

            switch (directionOfTravel){
                case 0:
                    snakeY[0] --;
                    break;
                case 1:
                    snakeX[0] ++;
                    break;
                case 2:
                    snakeY[0] ++;
                    break;
                case 3:
                    snakeX[0] --;
                    break;
            }

            boolean dead = false;
            if(snakeX[0] == -1) dead = true;
            if(snakeX[0] >= numBlocksWide) dead = true;
            if(snakeY[0] == -1) dead = true;
            if(snakeY[0] >= numBlocksHigh) dead = true;

            for (int i = snakeLength - 1; i > 0; i-- ){
                if((i > 4) && (snakeX[0] == snakeX[i]) && (snakeY[0] == snakeY[i])){
                    dead = true;
                }
            }

            if(dead){
                score = 0;
                getSnake();
            }
        }
    }
}
