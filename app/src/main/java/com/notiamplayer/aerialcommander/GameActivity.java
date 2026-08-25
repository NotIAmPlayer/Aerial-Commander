package com.notiamplayer.aerialcommander;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class GameActivity extends AppCompatActivity {
    public GameView game;

    private ImageButton pauseBtn;
    private FrameLayout layout;
    private TextView hpText;

    public LinearLayout overlayGameOver;
    private TextView gameOverText;

    int level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        );

        level = getIntent().getIntExtra("level", 1);

        hideSystemUI();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }

        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        layout = new FrameLayout(this);
        layout.setId(R.id.game);

        FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        );

        layout.setLayoutParams(frameParams);

        game = new GameView(this, GameActivity.this);

        pauseBtn = new ImageButton(this);

        FrameLayout.LayoutParams buttonParams = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        );

        int marginPixels = (int) (5 * this.getResources().getDisplayMetrics().density);
        buttonParams.setMargins(0, 0, marginPixels, marginPixels);

        buttonParams.gravity = Gravity.BOTTOM | Gravity.END;

        pauseBtn.setId(R.id.pauseBtn);
        pauseBtn.setImageResource(R.drawable.pause_button);
        pauseBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent));
        pauseBtn.setLayoutParams(buttonParams);

        hpText = new TextView(this);

        FrameLayout.LayoutParams textParams = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        );

        textParams.setMargins(marginPixels * 2, marginPixels * 4, 0, 0);

        hpText.setLayoutParams(textParams);
        hpText.setTypeface(Typeface.DEFAULT_BOLD);
        hpText.setText("HP");
        hpText.setTextColor(getColor(R.color.white));

        overlayGameOver = new LinearLayout(this);

        FrameLayout.LayoutParams overlayParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );

        overlayParams.gravity = Gravity.CENTER;

        overlayGameOver.setLayoutParams(overlayParams);
        overlayGameOver.setOrientation(LinearLayout.VERTICAL);
        overlayGameOver.setBackgroundColor(Color.parseColor("#7F000000"));

        float paddingDensity = this.getResources().getDisplayMetrics().density;
        overlayGameOver.setPadding(0, Math.round(10 * paddingDensity), 0, Math.round(10 * paddingDensity));
        overlayGameOver.setAlpha(0.0f);

        gameOverText = new TextView(this);
        gameOverText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        gameOverText.setText(getResources().getString(R.string.game_over));
        gameOverText.setTextColor(getResources().getColor(R.color.white));
        gameOverText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 50);
        gameOverText.setTypeface(gameOverText.getTypeface(), Typeface.BOLD);
        gameOverText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        gameOverText.setGravity(Gravity.CENTER);

        layout.addView(game);
        layout.addView(pauseBtn);
        layout.addView(hpText);
        layout.addView(overlayGameOver);

        overlayGameOver.addView(gameOverText);

        setContentView(layout);

        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (game.gameStatus != 0) return;

                pauseGame();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (game.gameStatus != 0) return;

        pauseGame();
    }

    public void pauseGame() {
        PauseMenuDialog pd = new PauseMenuDialog(this, game);
        pd.show();
        game.isPaused = true;

        pd.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                game.isPaused = false;
            }
        });
    }

    public void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }
}