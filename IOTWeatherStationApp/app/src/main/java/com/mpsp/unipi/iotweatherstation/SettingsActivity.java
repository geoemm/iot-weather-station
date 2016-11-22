package com.mpsp.unipi.iotweatherstation;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

public class SettingsActivity extends AppCompatActivity {
    DBManager dbManager;
    Spinner unitSpinner;
    Spinner secondsSpinner;
    final int SETTINGS_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbManager = new DBManager(this);
        dbManager.open();

        addListenerOnButton();
    }

    private void addListenerOnButton() {
        final int[] Unit_pos = new int[1];
        final int[] Sec_pos = new int[1];
        unitSpinner = (Spinner) findViewById(R.id.unitSpinner);
        secondsSpinner = (Spinner) findViewById(R.id.secondsSpinner);

        unitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Unit_pos[0] = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        secondsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Sec_pos[0] = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        final Button button = (Button) findViewById(R.id.btnSave);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                switch (Sec_pos[0]) {
                    case 0:
                        dbManager.update(SETTINGS_ID, Unit_pos[0], 5000);
                        break;
                    case 1:
                        dbManager.update(SETTINGS_ID, Unit_pos[0], 15000);
                        break;
                    case 2:
                        dbManager.update(SETTINGS_ID, Unit_pos[0], 30000);
                        break;
                    case 3:
                        dbManager.update(SETTINGS_ID, Unit_pos[0], 60000);
                        break;
                    case 4:
                        dbManager.update(SETTINGS_ID, Unit_pos[0], 120000);
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
