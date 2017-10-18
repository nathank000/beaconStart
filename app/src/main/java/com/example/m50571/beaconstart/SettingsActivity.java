package com.example.m50571.beaconstart;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    private FloatingActionButton _saveButton;
    private double _initialInvestment;
    private double _ethOwned;

    public static final String SETTINGS_TAG = "Settings:: ";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Log.d(SETTINGS_TAG, "in onCreate");

        _saveButton = (FloatingActionButton) findViewById(R.id.saveSettingsButton);

        _saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(SETTINGS_TAG, "handling a click on the save button");

                EditText ethOwnedTxtField = (EditText) findViewById(R.id.ethOwned);
                EditText initialInvestmentTxtField = (EditText) findViewById(R.id.InitInvestment);

                String ethToText = ethOwnedTxtField.getText().toString();
                String initInv = initialInvestmentTxtField.getText().toString();

                _initialInvestment = Double.parseDouble(initInv);
                _ethOwned = Double.parseDouble(ethToText);

                Intent intent = new Intent(SettingsActivity.this, ScrollingActivity.class);
                intent.putExtra("initialInvestment", _initialInvestment);
                intent.putExtra("ethOwned", _ethOwned);
                startActivity(intent);

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(SETTINGS_TAG, "in onResume");
        Intent incomingIntent = getIntent();
        _ethOwned = (double) incomingIntent.getDoubleExtra("ethOwned", 0);
        _initialInvestment = (double) incomingIntent.getDoubleExtra("initialInvestment",0 );

        TextView initInvestText = (TextView) findViewById(R.id.InitInvestment);
        TextView ethOwnedText = (TextView) findViewById(R.id.ethOwned);

        initInvestText.setText(Double.toString(_initialInvestment));
        ethOwnedText.setText(Double.toString(_ethOwned));
    }
}
