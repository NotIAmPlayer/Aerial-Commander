package com.notiamplayer.aerialcommander;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private MainThread thread;
    private Paint hpBackground, hpBar;

    private GameActivity game;

    public boolean isPaused = false;

    public int gameStatus = 0;

    final int STATUS_GAME_OVER = 1;
    final int STATUS_LEVEL_COMPLETE = 2;

    public Player player = new Player(this, 250, 2 * 500 /*, 64 */);
    public int endGameTimer = 0;

    public List<Sprite> sprites = new ArrayList<Sprite>();

    public Bitmap bgSky = BitmapFactory.decodeResource(getResources(), R.drawable.spr_bg_sky);
    public float skyPosY = 0;

    private int NUM_WAVES = 15;
    private int currentWave = 0;

    private int enemyCount = 0;

    private int enemyDefeated = 0;
    private boolean dialogShown = false;

    public GameView(Context context, GameActivity g) {
        super(context);

        game = g;

        getHolder().addCallback(this);
        thread = new MainThread(getHolder(), this);
        setFocusable(true);

        hpBackground = new Paint();
        hpBackground.setColor(Color.rgb(10, 10, 10));

        hpBar = new Paint();
        hpBar.setColor(Color.rgb(0, 255, 0));

        SharedPreferences gameData = context.getSharedPreferences("game_data", Context.MODE_PRIVATE);

        int[] armorHP = {100, 200, 450};

        int equippedArmor = gameData.getInt("armor_equipped", 0);

        player.maxHP = armorHP[equippedArmor];
        player.currentHP = player.maxHP;

        spawnNewWave();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gameStatus != 0) { return super.onTouchEvent(event); }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                player.setPosition((double) event.getX(), (double) event.getY());
                //System.out.println(event.getX() + " " + event.getY());
                return true;
            case MotionEvent.ACTION_MOVE:
                player.setPosition((double) event.getX(), (double) event.getY());
                //System.out.println(event.getX() + " " + event.getY());
                return true;
        }

        return super.onTouchEvent(event);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;

        while (retry) {
            try {
                thread.setRunning(false);
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            retry = false;
        }
    }

    public void update() {
        if (!isPaused) {
            skyPosY = (skyPosY + 1) % getHeight();

            for (Sprite npc : sprites) {
                if (!npc.initialized) {
                    continue;
                }

                npc.update();

                if (npc.getClass() == EnemyShipBasic.class) {
                    if (((EnemyShipBasic) npc).state == 4) {
                        if ((npc.dx == 0 && npc.dy == 0)
                                || (npc.posX < -getWidth() || npc.posX > getWidth()
                                || npc.posY < -getHeight() || npc.posY > getHeight())) {
                            sprites.remove(npc);
                            enemyCount--;
                            System.out.println("wave:" + currentWave + ":" + enemyCount);
                        }
                    }
                }

                if (!npc.isFriendly) {
                    if (player.iframes <= 0) {
                        if ((player.posX - 32 < npc.posX + npc.width * 0.5) &&
                            (player.posX + 32 > npc.posX - npc.width * 0.5) &&
                            (player.posY - 32 < npc.posY + npc.height * 0.5) &&
                            (player.posY) + 32 > npc.posY - npc.height * 0.5)
                        {
                            player.currentHP -= npc.attackDamage;
                            player.iframes = 60;

                            if (npc.getClass() == EnemyBullet.class) {
                                sprites.remove(npc);
                            }
                        }
                    }
                }

                if (npc.getClass() == PlayerBullet.class) {
                    for (Sprite target : sprites) {
                        if (target.getClass() != EnemyShipBasic.class) {
                            continue;
                        }

                        if ((target.posX - target.width * 0.5 < npc.posX + npc.width * 0.5) &&
                                (target.posX + target.width * 0.5 > npc.posX - npc.width * 0.5) &&
                                (target.posY - target.height * 0.5 < npc.posY + npc.height * 0.5) &&
                                (target.posY + target.height * 0.5 > npc.posY - npc.height * 0.5)
                        ) {
                            target.currentHP -= npc.attackDamage;
                            target.iframes = 20;

                            System.out.println("hello:" + target.currentHP + "," + target.maxHP);

                            if (target.currentHP <= 0) {
                                sprites.remove(target);
                                sprites.remove(npc);
                                enemyCount--;
                                enemyDefeated++;
                                System.out.println("wave:" + currentWave + ":" + enemyCount);
                                break;
                            }

                            sprites.remove(npc);
                        }
                    }
                }
            }

            player.update();

            if (enemyCount <= 0) {
                spawnNewWave();
            }
        }

        if (player.currentHP <= 0) {
            endGameTimer++;
            gameStatus = STATUS_GAME_OVER;

            if (endGameTimer > 180 && !dialogShown) {
                // reused variable
                dialogShown = true;

                game.runOnUiThread(() -> {
                    System.out.println("end:onthethread");
                    Intent mainMenu = new Intent(game, MainActivity.class);
                    game.startActivity(mainMenu);
                    game.finish();

                    isPaused = true;
                });
            }

            System.out.println("end:"+endGameTimer);
        }

        if (gameStatus == STATUS_LEVEL_COMPLETE) {
            endGameTimer++;

            if (endGameTimer > 60 && !dialogShown) {
                dialogShown = true;

                game.runOnUiThread(() -> {
                    System.out.println("end:onthethread");
                    LevelCompleteDialog lc = new LevelCompleteDialog(game, enemyDefeated, player.currentHP, player.maxHP, game.level);
                    lc.show();

                    //Toast.makeText(getContext(), "Level Complete!", Toast.LENGTH_SHORT).show();
                    isPaused = true;
                });
            }

            System.out.println("end:"+endGameTimer);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        //System.out.println(getWidth() + " " + getHeight());
        if (canvas != null) {
            canvas.drawColor(getResources().getColor(R.color.game_sky));

            for (int i = 0; i < 2; i++) {
                canvas.drawBitmap(bgSky, 0, (-bgSky.getHeight() * i) + skyPosY, null);
            }

            for (Sprite npc : sprites) {
                if ((npc.posX + npc.width > 0) && (npc.posX < getWidth())
                    && (npc.posY + npc.height > 0) && (npc.posY < getHeight())) {
                    npc.draw(canvas);
                }
            }

            player.draw(canvas);

            canvas.drawRect(70, 50, 370, 75, hpBackground);

            if (player.currentHP > 0) {
                canvas.drawRect(70, 50, 70 + 300 * ((float) player.currentHP / player.maxHP), 75, hpBar);
            } else if (endGameTimer >= 10) {
                game.overlayGameOver.setAlpha(1.0f);
            }
        }
    }

    public void spawnNewWave() {
        if (enemyCount > 0) return;

        switch (currentWave) {
            case 0:
                sprites.add(new EnemyShipBasic(
                        this,
                        200,
                        -500,
                        360,
                        200,
                        360,
                        2000)
                );

                enemyCount = 1;
                break;
            case 1:
                sprites.add(new EnemyShipBasic(
                        this,
                        -100,
                        -500,
                        100,
                        200,
                        200,
                        2000)
                );

                sprites.add(new EnemyShipBasic(
                        this,
                        800,
                        -500,
                        600,
                        200,
                        600,
                        2000)
                );

                enemyCount = 2;
                break;
            case 2:
                sprites.add(new EnemyShipBasic(
                        this,
                        -300,
                        -500,
                        300,
                        200,
                        300,
                        2000)
                );

                enemyCount = 1;
                break;
            case 3:
                sprites.add(new EnemyShipBasic(
                        this,
                        360,
                        -500,
                        100,
                        400,
                        250,
                        2000)
                );

                sprites.add(new EnemyShipBasic(
                        this,
                        360,
                        -500,
                        600,
                        400,
                        600,
                        2000)
                );

                enemyCount = 2;
                break;
            case 4:
                sprites.add(new EnemyShipBasic(
                        this,
                        360,
                        -500,
                        100,
                        300,
                        250,
                        2000)
                );

                sprites.add(new EnemyShipBasic(
                        this,
                        800,
                        -200,
                        360,
                        400,
                        360,
                        2000)
                );

                sprites.add(new EnemyShipBasic(
                        this,
                        360,
                        -500,
                        600,
                        300,
                        600,
                        2000)
                );

                enemyCount = 3;
                break;
            case 5:
                sprites.add(new EnemyShipBasic(
                        this,
                        800,
                        -500,
                        360,
                        2000,
                        360,
                        2000)
                );

                sprites.add(new EnemyShipBasic(
                        this,
                        1200,
                        -800,
                        360,
                        2000,
                        360,
                        2000)
                );

                enemyCount = 2;
                break;
            case 6:
                sprites.add(new EnemyShipBasic(
                        this,
                        200,
                        -500,
                        450,
                        200,
                        600,
                        2000)
                );

                enemyCount = 1;
                break;
            case 7:
                sprites.add(new EnemyShipBasic(
                        this,
                        -500,
                        -500,
                        250,
                        400,
                        -500,
                        2000)
                );

                sprites.add(new EnemyShipBasic(
                        this,
                        -500,
                        -500,
                        600,
                        500,
                        2000,
                        2000)
                );

                enemyCount = 2;
                break;
            case 8:
                sprites.add(new EnemyShipBasic(
                        this,
                        -500,
                        -500,
                        250,
                        400,
                        -500,
                        2000)
                );

                sprites.add(new EnemyShipBasic(
                        this,
                        -500,
                        -500,
                        600,
                        500,
                        2000,
                        2000)
                );

                enemyCount = 2;
                break;
            case 9:
                sprites.add(new EnemyShipBasic(
                        this,
                        800,
                        -500,
                        360,
                        2000,
                        360,
                        2000)
                );

                sprites.add(new EnemyShipBasic(
                        this,
                        1200,
                        -800,
                        360,
                        2000,
                        360,
                        2000)
                );

                enemyCount = 2;
                break;
            case 10:
                sprites.add(new EnemyShipBasic(
                        this,
                        -500,
                        -500,
                        250,
                        400,
                        -500,
                        2000)
                );

                sprites.add(new EnemyShipBasic(
                        this,
                        -500,
                        -500,
                        600,
                        500,
                        2000,
                        2000)
                );

                enemyCount = 2;
                break;
            case 11:
                sprites.add(new EnemyShipBasic(
                        this,
                        360,
                        -500,
                        100,
                        300,
                        250,
                        2000)
                );

                sprites.add(new EnemyShipBasic(
                        this,
                        800,
                        -200,
                        360,
                        400,
                        360,
                        2000)
                );

                sprites.add(new EnemyShipBasic(
                        this,
                        360,
                        -500,
                        600,
                        300,
                        600,
                        2000)
                );

                enemyCount = 3;
                break;
            case 12:
                sprites.add(new EnemyShipBasic(
                        this,
                        -500,
                        -500,
                        900,
                        2000,
                        900,
                        2000)
                );

                sprites.add(new EnemyShipBasic(
                        this,
                        -500,
                        -500,
                        300,
                        2000,
                        300,
                        2000)
                );

                enemyCount = 2;
                break;
            case 13:
                sprites.add(new EnemyShipBasic(
                        this,
                        360,
                        -500,
                        100,
                        300,
                        250,
                        2000)
                );

                sprites.add(new EnemyShipBasic(
                        this,
                        800,
                        -200,
                        360,
                        400,
                        360,
                        2000)
                );

                sprites.add(new EnemyShipBasic(
                        this,
                        360,
                        -500,
                        600,
                        300,
                        600,
                        2000)
                );

                enemyCount = 3;
                break;
            case 14:
                sprites.add(new EnemyShipBasic(
                        this,
                        800,
                        -500,
                        360,
                        2000,
                        360,
                        2000)
                );

                sprites.add(new EnemyShipBasic(
                        this,
                        1200,
                        -800,
                        360,
                        2000,
                        360,
                        2000)
                );

                enemyCount = 2;
                break;
        }

        if (currentWave < NUM_WAVES) {
            currentWave++;
        } else {
            isPaused = true;
            gameStatus = STATUS_LEVEL_COMPLETE;
        }
    }
}
