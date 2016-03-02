package com.example.patrickgross.snake;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

public class GameActivity extends Activity {

    Canvas canvs;
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
        public void run() {
            while(playingSnake){
                updateGame();
                drawGame();
                controlFPS();
            }
        }
        public void drawGame(){

        }

        public void updateGame(){
            if(snakeX[0] == appleX && snakeY[0] == appleY){
                snakeLength ++;
                getApple();
                score = score = snakeLength;
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
