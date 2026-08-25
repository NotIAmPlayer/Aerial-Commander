package com.notiamplayer.aerialcommander;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.core.content.ContextCompat;

public class LevelSelectDialog extends Dialog implements View.OnClickListener {
    public Activity c;
    public Dialog d;

    public Button close, buttonLv1, buttonLv2, buttonLv3, buttonLv4, buttonLv5;

    public int levelsUnlocked = 1;

    public LevelSelectDialog(Activity a, int unlocked) {
        super(a);
        this.c = a;

        levelsUnlocked = unlocked;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_level_select);

        close = (Button) findViewById(R.id.lsCloseButton);
        buttonLv1 = (Button) findViewById(R.id.lsLevel1);

        /*
        buttonLv2 = (Button) findViewById(R.id.lsLevel2);
        buttonLv3 = (Button) findViewById(R.id.lsLevel3);
        buttonLv4 = (Button) findViewById(R.id.lsLevel4);
        buttonLv5 = (Button) findViewById(R.id.lsLevel5);
         */

        close.setOnClickListener(this);
        buttonLv1.setOnClickListener(this);

        /*
        buttonLv2.setOnClickListener(this);
        buttonLv3.setOnClickListener(this);
        buttonLv4.setOnClickListener(this);
        buttonLv5.setOnClickListener(this);

        if (levelsUnlocked < 2) {
            buttonLv2.setEnabled(false);
        }

        if (levelsUnlocked < 3) {
            buttonLv3.setEnabled(false);
        }

        if (levelsUnlocked < 4) {
            buttonLv4.setEnabled(false);
        }

        if (levelsUnlocked < 5) {
            buttonLv5.setEnabled(false);
        }

         */
    }

    @Override
    public void onClick(View v) {
        int vID = v.getId();

        if (vID == R.id.lsLevel1) {
            Intent startGame = new Intent(c, GameActivity.class);
            startGame.putExtra("level", 1);

            c.startActivity(startGame);
            c.finish();
        } else if (vID == R.id.lsCloseButton) {
            dismiss();
        }

        dismiss();
    }
}
