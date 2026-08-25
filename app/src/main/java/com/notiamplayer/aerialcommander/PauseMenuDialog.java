package com.notiamplayer.aerialcommander;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class PauseMenuDialog extends Dialog implements View.OnClickListener {
    public Activity c;
    public Dialog d;

    public Button resumeGame, quitGame;

    public PauseMenuDialog(Activity a, GameView g) {
        super(a);
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_pause_menu);

        resumeGame = (Button) findViewById(R.id.btnResume);
        quitGame = (Button) findViewById(R.id.btnExit);

        resumeGame.setOnClickListener(this);
        quitGame.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int vID = v.getId();

        if (vID == R.id.btnExit) {
            Intent mainMenu = new Intent(c, MainActivity.class);
            c.startActivity(mainMenu);
            c.finish();
        } else if (vID == R.id.btnResume) {
            dismiss();
        }

        dismiss();
    }
}
