package com.notiamplayer.aerialcommander;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class EnemyShipBasic extends Sprite {
    public int width = 64, height = 64;
    private int speed = 10;

    private Bitmap image;

    private float targetX, targetY;
    private float leaveX, leaveY;

    public int state = 0;
    private int stateTimer = 0;

    public EnemyShipBasic(GameView view, float startX, float startY, float targetX, float targetY, float leaveX, float leaveY) {
        super(view, startX, startY, 0, 0);

        this.targetX = targetX;
        this.targetY = targetY;

        this.leaveX = leaveX;
        this.leaveY = leaveY;

        this.isFriendly = false;

        this.maxHP = 60;
        this.currentHP = this.maxHP;

        this.attackSpeed = 60;

        this.image = BitmapFactory.decodeResource(view.getResources(), R.drawable.spr_enemy_ship_basic);
    }

    public void update() {
        if (state == 0) {
            double distX = targetX - posX;
            double distY = targetY - posY;

            dx = distX / Math.abs(distX);
            dy = distY / Math.abs(distY);

            state = 1;
        } else if (state == 1) {
            double distX = targetX - posX;
            double distY = targetY - posY;

            dx = distX / Math.abs(distX);
            dy = distY / Math.abs(distY);

            if (Math.abs(distY) <= 0.1) {
                dy = 0;
                posY = targetY;
            }

            if (Math.abs(distX) <= 0.1) {
                dx = 0;
                posX = targetX;
            }

            if (dx == 0 && dy == 0) {
                state = 2;

                if ((targetX == leaveX) && (targetY == leaveY)) {
                    state = 4;
                }
            }
        } else if (state == 2) {
            stateTimer++;

            if (stateTimer % attackSpeed == 0) {
                view.sprites.add(new EnemyBullet(
                    view,
                    (float) this.posX,
                    (float) this.posY,
                    0,
                    2,
                    attackDamage
                ));
            }

            if (stateTimer >= 300) {
                state = 3;
                stateTimer = 0;
            }
        } else if (state == 3) {
            double distX = leaveX - posX;
            double distY = leaveY - posY;

            dx = distX / Math.abs(distX);
            dy = distY / Math.abs(distY);

            state = 4;
        } else if (state == 4) {
            double distX = leaveX - posX;
            double distY = leaveY - posY;

            if (Math.abs(distY) <= 0.1) {
                dy = 0;
                posY = targetY;
            }

            if (Math.abs(distX) <= 0.1) {
                dx = 0;
                posX = targetX;
            }
        }

        posX += dx * speed;
        posY += dy * speed;

        if (iframes > 0) {
            iframes--;
        }
    }

    public void draw(Canvas canvas) {
        if (iframes % 4 < 2) {
            canvas.drawBitmap(image, (float) (this.posX - width * 0.5), (float) (this.posY - height * 0.5), null);
        }
    }
}
