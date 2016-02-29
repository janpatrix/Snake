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
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainActivity extends Activity {

    Canvas canvas;
    SnakeAnimView snakeAnimView;
    Bitmap headAnimBitmap;
    Rect rectToBeDrawn;

    //Frames
    int frameHeight = 64;
    int frameWidth = 64;
    int frameNumberWidth = 0;
    int frameNumberHeight = 0;

    int screenHeight;
    int screenWidth;

    long lastFrameTime;
    int fps;
    int hi;

    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenHeight = size.y;
        screenWidth = size.x;

        headAnimBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.head_sprite_sheet);
        snakeAnimView = new SnakeAnimView(this);
        setContentView(snakeAnimView);

        i = new Intent(this, GameActivity.class);
    }

    @Override
    protected void onStop(){
        super.onStop();
        while(true){
            snakeAnimView.pause();
            break;
        }
        finish();
    }

    @Override
    protected void onResume(){
        super.onResume();
        snakeAnimView.resume();
    }

    @Override
    protected void onPause(){
        super.onPause();
        snakeAnimView.pause();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            snakeAnimView.pause();
            finish();
            return true;
        }
        return false;
    }

    class SnakeAnimView extends SurfaceView implements Runnable {

        Thread ourThread = null;
        SurfaceHolder ourHolder;
        volatile boolean playingSnake;
        Paint paint;

        public SnakeAnimView(Context context){
            super(context);
            ourHolder = getHolder();
            paint = new Paint();
            frameWidth = headAnimBitmap.getWidth()/5;
            frameHeight = headAnimBitmap.getHeight()/4;
        }

        @Override
        public void run() {
            while (playingSnake){
                update();
                draw();
                controlFPS();
            }
        }

        public void update(){
            rectToBeDrawn = new Rect(frameWidth*(3+frameNumberWidth) , frameHeight*frameNumberHeight, frameWidth*(4+frameNumberWidth), frameHeight*(frameNumberHeight+1));
            frameNumberWidth++;
            if(frameNumberWidth == 2){
                frameNumberHeight += 1;
                frameNumberWidth = 0;
            }
            if(frameNumberHeight == 2){
                frameNumberHeight = 0;
            }
        }

        public void draw(){
            if(ourHolder.getSurface().isValid()){
                canvas = ourHolder.lockCanvas();
                canvas.drawColor(Color.BLACK);
                paint.setColor(Color.argb(255, 255, 255, 255));
                paint.setTextSize(150);
                canvas.drawText("Snake", 10, 150, paint);
                paint.setTextSize(50);
                canvas.drawText("    Hi Score: " + hi, 10, screenHeight - 150, paint);

                Rect destRect = new Rect(screenWidth/2 - 100, screenHeight/2 - 100,
                        screenWidth/2 + 100, screenHeight/2 + 100);
                canvas.drawBitmap(headAnimBitmap, rectToBeDrawn, destRect, paint);
                ourHolder.unlockCanvasAndPost(canvas);
            }
        }

        public void controlFPS(){
            long timeThisFrame = (System.currentTimeMillis() - lastFrameTime);
            long timeToSleep = 500 - timeThisFrame;
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
            startActivity(i);
            return true;
        }

    }
}
