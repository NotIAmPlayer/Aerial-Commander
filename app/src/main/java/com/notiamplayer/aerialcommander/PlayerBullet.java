package com.notiamplayer.aerialcommander;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class PlayerBullet extends Sprite {
    public int width = 16, height = 40;
    private int speed = 20;

    private Bitmap image;

    public PlayerBullet(GameView view, float startX, float startY, float startDX, float startDY, int damage) {
        super(view, startX, startY, startDX, startDY);

        this.attackDamage = damage;

        this.image = BitmapFactory.decodeResource(view.getResources(), R.drawable.spr_player_bullet);
    }

    public void update() {
        if ((posY <= -this.height * 2) || (posY + height >= scrHeight)) {
            this.view.sprites.remove(this);
        }

        posX += dx * speed;
        posY += dy * speed;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, (float) (this.posX - (width * 0.5)), (float) (this.posY - (height * 0.5)), null);
    }
}
