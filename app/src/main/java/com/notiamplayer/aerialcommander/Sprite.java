package com.notiamplayer.aerialcommander;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Sprite {
    public double posX, posY;
    public double dx, dy;
    public int width = 128, height = 128;
    private int speed = 5;

    public GameView view;
    public int scrWidth, scrHeight;

    // GAME STATS
    public int maxHP = 100;
    public int currentHP = maxHP;

    public int attackDamage = 10;
    public int attackSpeed = 40;

    public int defense = 0;

    public int attackTimer = 0;

    public boolean isFriendly = true;

    public int iframes = 0;

    public boolean initialized = false;

    public Sprite(GameView view, float startX, float startY, float startDX, float startDY) {
        posX = startX;
        posY = startY;
        dx = startDX;
        dy = startDY;

        this.view = view;
        scrWidth = view.getWidth();
        scrHeight = view.getHeight();

        initialized = true;
    }

    public void update() {

    }

    public void draw(Canvas canvas) {

    }
}
