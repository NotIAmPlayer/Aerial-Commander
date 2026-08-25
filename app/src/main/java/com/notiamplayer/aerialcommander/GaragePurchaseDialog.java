package com.notiamplayer.aerialcommander;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;

import org.w3c.dom.Text;

import java.util.Objects;

public class GaragePurchaseDialog extends Dialog implements View.OnClickListener {
    public Activity c;
    public Dialog d;

    public Button btnClose, btnConfirmPurchase, button;
    public TextView text;

    int icon, itemName, price;

    public GaragePurchaseDialog(Activity a, int icon, int itemName, int price, Button btn, TextView txt) {
        super(a);
        this.c = a;

        this.icon = icon;
        this.itemName = itemName;
        this.price = price;

        this.button = btn;
        this.text = txt;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_garage_purchase);

        Objects.requireNonNull(getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        btnClose = (Button) findViewById(R.id.close_button);
        btnConfirmPurchase = (Button) findViewById(R.id.btn_purchase);

        btnClose.setOnClickListener(this);
        btnConfirmPurchase.setOnClickListener(this);

        ImageView icon = (ImageView) findViewById(R.id.purchase_icon);
        TextView text = (TextView) findViewById(R.id.purchase_text);

        icon.setImageDrawable(AppCompatResources.getDrawable(getContext(), this.icon));
        text.setText(String.format(getContext().getString(R.string.purchase_dialog_confirm_text), getContext().getString(this.itemName), this.price));
    }

    @Override
    public void onClick(View v) {
        int vID = v.getId();

        if (vID == R.id.btn_purchase) {
            SharedPreferences gameData = c.getSharedPreferences("game_data", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = gameData.edit();

            int goldCount = gameData.getInt("curr_gold_amount", 0);

            editor.putInt("curr_gold_amount", goldCount - price);

            if (this.itemName == R.string.item_armor_basic) {
                editor.putBoolean("purchased_armor_0", true);
            } else if (this.itemName == R.string.item_armor_upgrade1) {
                editor.putBoolean("purchased_armor_1", true);
            } else if (this.itemName == R.string.item_armor_upgrade2) {
                editor.putBoolean("purchased_armor_2", true);
            }

            button.setText(c.getString(R.string.item_equip));
            button.setEnabled(true);

            text.setText(String.valueOf(goldCount - price));

            editor.apply();
        } else if (vID == R.id.close_button) {
            dismiss();
        }

        dismiss();
    }
}
