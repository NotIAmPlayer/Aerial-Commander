package com.notiamplayer.aerialcommander;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Player {
    public double posX, posY;
    //private double radius;
    private Bitmap image;

    private GameView view;

    //private Paint paint;

    // GAME STATS
    public int maxHP = 100;
    public int currentHP = maxHP;

    public int attackDamage = 20;
    public int attackSpeed = 30;

    public int attackTimer = -(attackSpeed - 1);

    public int iframes = 0;

    public Player(GameView view, double positionX, double positionY) {
        this.posX = positionX;
        this.posY = positionY;

        this.view = view;

        /*
        this.paint = new Paint();

        int color = Color.rgb(0, 200, 255);

        paint.setColor(color);
         */

        this.image = BitmapFactory.decodeResource(view.getResources(), R.drawable.spr_player_ship);
    }

    public void update() {
        if (currentHP <= 0) { return; }


        attackTimer++;

        if (attackTimer % attackSpeed == 0) {
            view.sprites.add(new PlayerBullet(
                view,
                (float) this.posX,
                (float) this.posY,
                0,
                -2,
                attackDamage
            ));
        }

        if (iframes > 0) {
            iframes--;
        }
    }

    public void draw(Canvas canvas) {
        //canvas.drawCircle((float) this.posX, (float) this.posY, (float) this.radius, paint);

        if (iframes % 4 < 2 && currentHP > 0) {
            canvas.drawBitmap(image, (float) this.posX - 32, (float) this.posY - 32, null);
        }
    }

    public void setPosition(double positionX, double positionY) {
        this.posX = positionX;
        this.posY = positionY;
    }
}
