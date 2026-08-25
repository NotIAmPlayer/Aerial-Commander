package com.notiamplayer.aerialcommander;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;

public class GarageInsufficientDialog extends Dialog implements View.OnClickListener {
    public Activity c;
    public Dialog d;

    public Button btnClose;

    int itemName;

    public GarageInsufficientDialog(Activity a, int itemName) {
        super(a);
        this.c = a;

        this.itemName = itemName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_garage_insufficient);

        btnClose = (Button) findViewById(R.id.close_button);

        btnClose.setOnClickListener(this);
        TextView text = (TextView) findViewById(R.id.no_purchase_text);

        text.setText(String.format(getContext().getString(R.string.purchase_dialog_failed_text), getContext().getString(this.itemName)));
    }

    @Override
    public void onClick(View v) {
        int vID = v.getId();

        if (vID == R.id.close_button) {
            dismiss();
        }

        dismiss();
    }
}
