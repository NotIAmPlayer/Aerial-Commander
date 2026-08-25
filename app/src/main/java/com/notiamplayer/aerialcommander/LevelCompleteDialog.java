package com.notiamplayer.aerialcommander;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;

import java.util.Locale;

public class LevelCompleteDialog extends Dialog implements View.OnClickListener {
    public Activity c;
    public Dialog d;

    public Button btnClose;

    int enemyCount, currentHP, maxHP, level;

    int bounty;

    SharedPreferences gameData;
    SharedPreferences.Editor editor;

    public LevelCompleteDialog(Activity a, int enemiesDefeated, int currentHP, int maxHP, int level) {
        super(a);
        this.c = a;

        this.enemyCount = enemiesDefeated;
        this.currentHP = currentHP;
        this.maxHP = maxHP;
        this.level = level;

        bounty = (int) (((float) currentHP/maxHP) * 500) + (enemiesDefeated * 10);

        gameData = c.getSharedPreferences("game_data", Context.MODE_PRIVATE);
        editor = gameData.edit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_level_complete);

        btnClose = (Button) findViewById(R.id.btn_close);
        btnClose.setOnClickListener(this);

        TextView enemiesDefeated, healthRemaining, goldBounty;

        enemiesDefeated = (TextView) findViewById(R.id.text_enemies_defeated);
        enemiesDefeated.setText(String.valueOf(this.enemyCount));

        healthRemaining = (TextView) findViewById(R.id.text_health_remaining);

        healthRemaining.setText(String.format(Locale.ENGLISH, "%d%%", Math.round(((float) this.currentHP/this.maxHP) * 100)));

        goldBounty = (TextView) findViewById(R.id.gold_counter);
        goldBounty.setText(String.valueOf(this.bounty));

        int goldCount = gameData.getInt("curr_gold_amount", 0);
        int lastLevelCompleted = gameData.getInt("last_completed_level", 0);

        editor.putInt("curr_gold_amount", goldCount + bounty);

        if (level > lastLevelCompleted) {
            editor.putInt("last_completed_level", level);
        }

        editor.apply();

        Log.d("LevelCompleteDialog", "Dialog onCreate called");

        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                Intent mainMenu = new Intent(c, MainActivity.class);
                c.startActivity(mainMenu);
                c.finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        int vID = v.getId();

        if (vID == R.id.btn_close) {
            dismiss();
        }

        dismiss();
    }
}
