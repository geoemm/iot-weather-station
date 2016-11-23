package com.mpsp.unipi.iotweatherstation;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {
    DBManager dbManager;
    CheckBox turnToFarenheit;
    ImageButton plus;
    ImageButton minus;
    TextView seconds;
    final int SETTINGS_ID = 1;
    int delay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setSettingsToView();

        addListenerOnButton();

        plus = (ImageButton) findViewById(R.id.imageButton2);
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delay = delay + 5000;
                seconds.setText(""+delay/1000);
                if (delay == 60000) {
                    plus.setEnabled(false);
                    minus.setEnabled(true);
                    Toast.makeText(getApplicationContext(), "Reached maximum limit", Toast.LENGTH_SHORT).show();
                } else {
                    plus.setClickable(true);
                    plus.setEnabled(true);
                    minus.setEnabled(true);
                }
            }
        });

        minus = (ImageButton) findViewById(R.id.imageButton);
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delay = delay - 5000;
                seconds.setText(""+delay/1000);
                if (delay == 5000) {
                    minus.setEnabled(false);
                    plus.setEnabled(true);
                    Toast.makeText(getApplicationContext(), "Reached minimum limit", Toast.LENGTH_SHORT).show();
                } else {
                    plus.setEnabled(true);
                    minus.setClickable(true);
                    minus.setEnabled(true);
                }
            }
        });
    }

    private void setSettingsToView() {
        dbManager = new DBManager(this);
        dbManager.open();

        Cursor cursor = dbManager.fetch();
        cursor.moveToFirst();
        int unit = cursor.getInt(cursor.getColumnIndex("temperature_unit"));
        delay = cursor.getInt(cursor.getColumnIndex("sync_period"));

        turnToFarenheit = (CheckBox) findViewById(R.id.checkBox);
        if (unit == 0) {
            turnToFarenheit.setChecked(false);
        } else {
            turnToFarenheit.setChecked(true);
        }
        seconds = (TextView) findViewById(R.id.textView);
        seconds.setText(""+delay/1000);

        dbManager.close();

    }

    private void addListenerOnButton() {
        dbManager = new DBManager(this);
        dbManager.open();

        final Button button = (Button) findViewById(R.id.btnSave);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                switch (delay/1000) {
                    case 5:
                        dbManager.update(SETTINGS_ID, turnToFarenheit.isChecked() ? 1 : 0, 5000);
                        break;
                    case 10:
                        dbManager.update(SETTINGS_ID, turnToFarenheit.isChecked() ? 1 : 0, 10000);
                        break;
                    case 15:
                        dbManager.update(SETTINGS_ID, turnToFarenheit.isChecked() ? 1 : 0, 15000);
                        break;
                    case 20:
                        dbManager.update(SETTINGS_ID, turnToFarenheit.isChecked() ? 1 : 0, 20000);
                        break;
                    case 25:
                        dbManager.update(SETTINGS_ID, turnToFarenheit.isChecked() ? 1 : 0, 25000);
                        break;
                    case 30:
                        dbManager.update(SETTINGS_ID, turnToFarenheit.isChecked() ? 1 : 0, 30000);
                        break;
                    case 35:
                        dbManager.update(SETTINGS_ID, turnToFarenheit.isChecked() ? 1 : 0, 35000);
                        break;
                    case 40:
                        dbManager.update(SETTINGS_ID, turnToFarenheit.isChecked() ? 1 : 0, 40000);
                        break;
                    case 45:
                        dbManager.update(SETTINGS_ID, turnToFarenheit.isChecked() ? 1 : 0, 45000);
                        break;
                    case 50:
                        dbManager.update(SETTINGS_ID, turnToFarenheit.isChecked() ? 1 : 0, 50000);
                        break;
                    case 55:
                        dbManager.update(SETTINGS_ID, turnToFarenheit.isChecked() ? 1 : 0, 55000);
                        break;
                    case 60:
                        dbManager.update(SETTINGS_ID, turnToFarenheit.isChecked() ? 1 : 0, 60000);
                        break;
                    default:
                        break;
                }

                Snackbar snackbar = Snackbar
                        .make(findViewById(android.R.id.content), "Settings saved!", Snackbar.LENGTH_SHORT)
                        .setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {

                        finish();
                    }
                });
                snackbar.show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
