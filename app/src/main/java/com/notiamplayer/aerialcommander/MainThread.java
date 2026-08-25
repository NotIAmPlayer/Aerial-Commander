package com.notiamplayer.aerialcommander;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class MainThread extends Thread {
    private SurfaceHolder surfaceHolder;
    private GameView gameView;

    private boolean running;
    public static Canvas canvas;

    private final int TARGET_FPS = 60;

    private double averageFPS;

    public MainThread(SurfaceHolder surfaceHolder, GameView gameView) {
        super();

        this.surfaceHolder = surfaceHolder;
        this.gameView = gameView;
    }

    public void setRunning(boolean isRunning) {
        running = isRunning;
    }

    @Override
    public void run() {
        long startTime;
        long timeMillis = 0;
        long waitTime = 0;
        long totalTime = 0;
        int frameCount = 0;
        int updateCount = 0;
        long targetTime = 1000 / TARGET_FPS;

        while (running) {
            startTime = System.nanoTime();
            canvas = null;

            try {
                canvas = this.surfaceHolder.lockCanvas();

                synchronized (surfaceHolder) {

                    this.gameView.update();
                    updateCount++;
                    this.gameView.draw(canvas);
                }
            } catch (Exception e) {} finally {
                if (canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            timeMillis = (System.nanoTime() - startTime) / 1000000;
            waitTime = targetTime - timeMillis;

            if (waitTime > 0) {
                try {
                    sleep(waitTime);
                } catch (Exception e) {}
            }

            if ((waitTime < 0) && (updateCount < 30 - 1)) {
                while (updateCount < 30 - 1) {
                    this.gameView.update();
                    updateCount++;

                    timeMillis = (System.nanoTime() - startTime) / 1000000;
                }
            }

            totalTime += System.nanoTime() - startTime;
            frameCount++;

            //System.out.println(timeMillis + " " + waitTime + " " + totalTime + " " + frameCount);

            if (frameCount == TARGET_FPS) {
                averageFPS = 1000 / ((totalTime / frameCount) / 1000000);
                frameCount = 0;
                totalTime = 0;
                System.out.println(averageFPS);
            }
        }
    }
}
