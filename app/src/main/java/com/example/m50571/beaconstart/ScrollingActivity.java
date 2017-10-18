package com.example.m50571.beaconstart;

import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.TimedBeaconSimulator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ScrollingActivity extends AppCompatActivity implements BeaconConsumer {


    //for the beacon alone
    private BeaconManager _beaconManager;
    private static String B_TAG = "Beacon:: ";
    private static String ETH_TAG = "Ethereum fetch:: ";

    //for the Async manager
    private AsyncHttpClient _ajaxClient;

    //https://bitcoinaverage.com/en/apikeys
    private static final String COIN_URL_BTC = "https://apiv2.bitcoinaverage.com/indices/global/ticker/BTCUSD";
    private static final String COIN_URL_ETH = "https://apiv2.bitcoinaverage.com/indices/global/ticker/ETHUSD";

    private static final String BCA_PUBLIC_KEY = "MGNiM2FhNmNhYTMxNDkzNGIxMzBiMGUwNjE2OTBhZmI";
    private static final String BCA_SECRET_KEY = "MTY2ZTAyMDVjZWMyNGI0MmI4ODYxODg4OWY4NTAwYWE0ZGMwOGVmNTY4NmI0ZGQzOWNiMzNmNjRiNzM3MWZlMw";

    private double _ethOwned;
    private double _initInvest;
    private double _ethValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        _initInvest = 0.0;
        _ethOwned = 0.0;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Fetching latest price", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                fetchEthPrice();
            }
        });

        Log.d(B_TAG, "<begin> initializing the beacon.");

        Toast.makeText(getApplicationContext(), "initializing the beacon manager", Toast.LENGTH_SHORT).show();
        _beaconManager = BeaconManager.getInstanceForApplication(this);
        BeaconManager.setBeaconSimulator(new TimedBeaconSimulator());

        Log.d(B_TAG, "beacon instance retrieved");
        _beaconManager.bind(this);
        Log.d(B_TAG, "<complete> done with beacon initialization");

        fetchEthPrice();

        //start a timer to do this every 10 secs


    }

    @Override
    public void onBeaconServiceConnect() {
        _beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                Toast.makeText(getApplicationContext(), "I just saw the beacon for the first time", Toast.LENGTH_SHORT).show();
                Log.d(B_TAG, "beacon spotted on did enter region");
            }

            @Override
            public void didExitRegion(Region region) {
                Log.d(B_TAG, "beacon spotted on did EXIT region");
            }

            @Override
            public void didDetermineStateForRegion(int i, Region region) {
                Log.d(B_TAG, "beacon spotted on did determine state for region");
            }
        });

        try {
            _beaconManager.startMonitoringBeaconsInRegion(new Region("myMonitoringUniqueId", null, null, null));
        } catch (RemoteException e) {
            Log.d(B_TAG, "there was an error in startMonitoringBeaconsInRegion");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Log.d(ETH_TAG, "handling a click on the settings menu item");
            handleSettingClick();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void handleSettingClick() {
        Log.d(ETH_TAG, "in handle settingClick");
        Intent settingsIntent = new Intent(ScrollingActivity.this, SettingsActivity.class);
        settingsIntent.putExtra("ethOwned", _ethOwned);
        settingsIntent.putExtra("initialInvestment", _initInvest);
        startActivity(settingsIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(B_TAG, "calling OnDestroy, unbinding the beacon");
        //kill it when the app closes
        _beaconManager.unbind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent incomingIntent = getIntent();
        _ethOwned = (double) incomingIntent.getDoubleExtra("ethOwned", 0);
        _initInvest = (double) incomingIntent.getDoubleExtra("initialInvestment",0 );

        Log.d(ETH_TAG, "got back some info from the other view: " + _ethOwned);
        Log.d(ETH_TAG, "got back some info from the other view: " + _initInvest);

        fetchEthPrice();

    }

    protected void fetchEthPrice() {
        Log.d(ETH_TAG, "FETCHING ETH PRICE");
        //setup the test client to go fetch the current price of bitcoing and ethereum
        AsyncHttpClient _ajaxClient = new AsyncHttpClient();
        _ajaxClient.get(COIN_URL_ETH, null, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(ETH_TAG, "got back a json object " + response);
                try {

                    double lastPrice = (double) response.getDouble("last");
                    Log.d(ETH_TAG, "Last: " +lastPrice);

                    logEthProfit(lastPrice);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response){

                Log.d(ETH_TAG, "got back a json array " +response);
            }

        });
    }

    protected void logEthProfit(double lastEthPrice) {
        EthResult lastEthResult = new EthResult(lastEthPrice, _initInvest, _ethOwned);

        //add it to the list
        Log.d(ETH_TAG, "last ETH result = " + lastEthResult.get_profit());

        TextView longTextView = (TextView) findViewById(R.id.results_holder );
        String currentText = (String) longTextView.getText();
        String newText = currentText + "\n" +lastEthResult.get_profit();

        longTextView.setText(newText);
    }
}
