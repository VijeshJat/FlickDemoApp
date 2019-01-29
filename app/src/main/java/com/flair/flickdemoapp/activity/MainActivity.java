package com.flair.flickdemoapp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.flair.flickdemoapp.R;

import io.flic.poiclib.FlicButton;
import io.flic.poiclib.FlicButtonAdapter;
import io.flic.poiclib.FlicButtonListener;
import io.flic.poiclib.FlicManager;
import io.flic.poiclib.FlicScanWizard;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    String TAG = "TESTING";
    FlicButtonListener buttonListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initViews();


        buttonListener = new FlicButtonAdapter() {
            @Override
            public void onConnect(FlicButton button) {
                Log.i(TAG, "onConnect: " + button.getBdAddr());
            }

            @Override
            public void onReady(FlicButton button) {
                Log.i(TAG, "onReady: " + button.getBdAddr());
            }

            @Override
            public void onDisconnect(FlicButton button, int flicError, boolean willReconnect) {
                Log.i(TAG, "onDisconnect: " + button.getBdAddr() + ", error: " + flicError + ", willReconnect: " + willReconnect);
            }

            @Override
            public void onConnectionFailed(FlicButton button, int status) {
                Log.e(TAG, "onConnectionFailed " + button.getBdAddr() + ", status " + status);
            }

            @Override
            public void onButtonUpOrDown(final FlicButton button, boolean wasQueued, int timeDiff, final boolean isUp, final boolean isDown) {
                Log.i(TAG, "Button " + button.getBdAddr() + " was " + (isDown ? "pressed" : "released"));
            }
        };

    }

    private void initViews() {

        findViewById(R.id.btnScanFlickButton).setOnClickListener(this);
        findViewById(R.id.btnGetAddedFlickButton).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {


            case R.id.btnScanFlickButton:

                startActivity(new Intent(MainActivity.this, FlicScanActivity.class));

                //onButtonScan();
                break;


            case R.id.btnGetAddedFlickButton:

                for (FlicButton button : FlicManager.getManager().getKnownButtons()) {
                    button.connect();
                    button.setEventListener(buttonListener);
                }
                break;
        }
    }

    private void onButtonScan() {

        Toast.makeText(this, "Press your button to pair", Toast.LENGTH_SHORT).show();

        FlicManager.getManager().getScanWizard().start(new FlicScanWizard.Callback() {
            @Override
            public void onDiscovered(FlicScanWizard wizard, String bdAddr, int rssi, final boolean isPrivateMode, int revision) {
                if (isPrivateMode) {
                    Toast.makeText(MainActivity.this, "Found a private button. Hold it down for 7 seconds to make it public.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Found a button. Now connecting...", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onBLEConnected(FlicScanWizard wizard, String bdAddr) {
                Toast.makeText(MainActivity.this, "Connection established. Now verifying...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCompleted(FlicScanWizard wizard, final FlicButton button) {
                Toast.makeText(MainActivity.this, "New button successfully added!", Toast.LENGTH_SHORT).show();
                // Do something with the button object
            }

            @Override
            public void onFailed(FlicScanWizard wizard, int flicScanWizardErrorCode) {
                Toast.makeText(MainActivity.this, "Adding a button failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        FlicManager.getManager().getScanWizard().cancel();
    }
}
