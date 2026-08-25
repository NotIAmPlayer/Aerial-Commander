package com.notiamplayer.aerialcommander;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import org.w3c.dom.Text;

public class GarageActivity extends AppCompatActivity {

    public TextView textGoldCounter;
    public Button btnArmorBasic, btnArmorUpgrade1, btnArmorUpgrade2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        );

        hideSystemUI();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }

        setContentView(R.layout.activity_garage);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.garage), (v, insets) -> {
            return insets;
        });

        Button garageCloseButton = (Button) findViewById(R.id.garageCloseButton);

        garageCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w) {
                finish();
            }
        });

        SharedPreferences gameData = getApplicationContext().getSharedPreferences("game_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = gameData.edit();

        int goldCount = gameData.getInt("curr_gold_amount", 0);

        textGoldCounter = (TextView) findViewById(R.id.gold_counter);
        textGoldCounter.setText(String.valueOf(goldCount));

        int[] armorPrices = {0, 250, 3000};

        boolean purchasedArmorBasic = gameData.getBoolean("purchased_armor_0", true);
        boolean purchasedArmorUpgrade1 = gameData.getBoolean("purchased_armor_1", false);
        boolean purchasedArmorUpgrade2 = gameData.getBoolean("purchased_armor_2", false);

        int equippedArmor = gameData.getInt("armor_equipped", 0);

        btnArmorBasic = (Button) findViewById(R.id.btn_armor_basic);
        btnArmorUpgrade1 = (Button) findViewById(R.id.btn_armor_upgrade1);
        btnArmorUpgrade2 = (Button) findViewById(R.id.btn_armor_upgrade2);

        switch (equippedArmor) {
            case 0:
                btnArmorBasic.setText(getString(R.string.item_equipped));
                btnArmorBasic.setEnabled(false);
                break;
            case 1:
                btnArmorUpgrade1.setText(getString(R.string.item_equipped));
                btnArmorUpgrade1.setEnabled(false);
                break;
            case 2:
                btnArmorUpgrade2.setText(getString(R.string.item_equipped));
                btnArmorUpgrade2.setEnabled(false);
        }

        if (!purchasedArmorBasic) {
            btnArmorBasic.setText(String.format(getString(R.string.item_buy), armorPrices[0]));
        }

        if (!purchasedArmorUpgrade1) {
            btnArmorUpgrade1.setText(String.format(getString(R.string.item_buy), armorPrices[1]));
        }

        if (!purchasedArmorUpgrade2) {
            btnArmorUpgrade2.setText(String.format(getString(R.string.item_buy), armorPrices[2]));
        }

        btnArmorBasic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gameData.getBoolean("purchased_armor_0", true)) {
                    btnArmorBasic.setText(getString(R.string.item_equipped));
                    btnArmorBasic.setEnabled(false);

                    if (!btnArmorUpgrade1.isEnabled()) {
                        btnArmorUpgrade1.setText(getString(R.string.item_equip));
                        btnArmorUpgrade1.setEnabled(true);
                    }

                    if (!btnArmorUpgrade2.isEnabled()) {
                        btnArmorUpgrade2.setText(getString(R.string.item_equip));
                        btnArmorUpgrade2.setEnabled(true);
                    }

                    editor.putInt("armor_equipped", 0);
                    editor.apply();
                } else {
                    if (goldCount >= armorPrices[0]) {
                        GaragePurchaseDialog gp = new GaragePurchaseDialog(GarageActivity.this, R.drawable.upgrade_generic, R.string.item_armor_basic, armorPrices[0], btnArmorBasic, textGoldCounter);
                        gp.show();
                    } else {
                        GarageInsufficientDialog gi = new GarageInsufficientDialog(GarageActivity.this, R.string.item_armor_basic);
                        gi.show();
                    }
                }
            }
        });

        btnArmorUpgrade1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gameData.getBoolean("purchased_armor_1", false)) {
                    btnArmorUpgrade1.setText(getString(R.string.item_equipped));
                    btnArmorUpgrade1.setEnabled(false);

                    if (!btnArmorBasic.isEnabled()) {
                        btnArmorBasic.setText(getString(R.string.item_equip));
                        btnArmorBasic.setEnabled(true);
                    }

                    if (!btnArmorUpgrade2.isEnabled()) {
                        btnArmorUpgrade2.setText(getString(R.string.item_equip));
                        btnArmorUpgrade2.setEnabled(true);
                    }

                    editor.putInt("armor_equipped", 1);
                    editor.apply();
                } else {
                    if (goldCount >= armorPrices[1]) {
                        GaragePurchaseDialog gp = new GaragePurchaseDialog(GarageActivity.this, R.drawable.upgrade_generic, R.string.item_armor_upgrade1, armorPrices[1], btnArmorUpgrade1, textGoldCounter);
                        gp.show();
                    } else {
                        GarageInsufficientDialog gi = new GarageInsufficientDialog(GarageActivity.this, R.string.item_armor_upgrade1);
                        gi.show();
                    }
                }
            }
        });

        btnArmorUpgrade2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gameData.getBoolean("purchased_armor_2", false)) {
                    btnArmorUpgrade2.setText(getString(R.string.item_equipped));
                    btnArmorUpgrade2.setEnabled(false);

                    if (!btnArmorBasic.isEnabled()) {
                        btnArmorBasic.setText(getString(R.string.item_equip));
                        btnArmorBasic.setEnabled(true);
                    }

                    if (!btnArmorUpgrade1.isEnabled()) {
                        btnArmorUpgrade1.setText(getString(R.string.item_equip));
                        btnArmorUpgrade1.setEnabled(true);
                    }

                    editor.putInt("armor_equipped", 2);
                    editor.apply();
                } else {
                    if (goldCount >= armorPrices[2]) {
                        GaragePurchaseDialog gp = new GaragePurchaseDialog(GarageActivity.this, R.drawable.upgrade_generic, R.string.item_armor_upgrade2, armorPrices[2], btnArmorUpgrade2, textGoldCounter);
                        gp.show();
                    } else {
                        GarageInsufficientDialog gi = new GarageInsufficientDialog(GarageActivity.this, R.string.item_armor_upgrade2);
                        gi.show();
                    }
                }
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