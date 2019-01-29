package com.flair.flickdemoapp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.flair.flickdemoapp.R;
import com.flair.flickdemoapp.utility.MyApplication;

import java.util.HashMap;
import java.util.Map;

import io.flic.poiclib.FlicButton;
import io.flic.poiclib.FlicButtonAdapter;
import io.flic.poiclib.FlicButtonListener;
import io.flic.poiclib.FlicButtonMode;
import io.flic.poiclib.FlicManager;
import io.flic.poiclib.FlicScanWizard;

public class FlicScanActivity extends AppCompatActivity {

    HashMap<FlicButton, FlicButtonListener> listeners = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flic_scan);


        for (FlicButton button : FlicManager.getManager().getKnownButtons()) {
            setupEventListenerForButtonInActivity(button);
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        for (Map.Entry<FlicButton, FlicButtonListener> entry : listeners.entrySet()) {
            entry.getKey().removeEventListener(entry.getValue());
            entry.getKey().returnTemporaryMode(FlicButtonMode.SuperActive);
        }

        // Cancels the scan wizard, if it's running
        FlicManager.getManager().getScanWizard().cancel();
    }

    private void setupEventListenerForButtonInActivity(FlicButton button) {
        FlicButtonListener listener = new FlicButtonAdapter() {

            @Override
            public void onButtonSingleOrDoubleClick(FlicButton button, boolean wasQueued, int timeDiff, boolean isSingleClick, boolean isDoubleClick) {
                super.onButtonSingleOrDoubleClick(button, wasQueued, timeDiff, isSingleClick, isDoubleClick);

                if (isSingleClick){
                    ((TextView) findViewById(R.id.textView)).setText("Button is single click for some action");
                }else if(isDoubleClick){
                    ((TextView) findViewById(R.id.textView)).setText("Button is double click for some action");
                }
            }

            @Override
            public void onButtonClickOrHold(FlicButton button, boolean wasQueued, int timeDiff, boolean isClick, boolean isHold) {
                super.onButtonClickOrHold(button, wasQueued, timeDiff, isClick, isHold);
                if (isHold){
                    ((TextView) findViewById(R.id.textView)).setText("Button is hold for some action");

                }

            }
        };
        button.addEventListener(listener);
        button.setTemporaryMode(FlicButtonMode.SuperActive);

        // Save the event listener so we can remove it later
        listeners.put(button, listener);
    }

    public void scanNewButton(View v) {
        // Disable the button until the scan wizard has finished
        v.setEnabled(false);

        ((TextView) findViewById(R.id.scanWizardStatus)).setText("Press your Flic button");

        FlicManager.getManager().getScanWizard().start(new FlicScanWizard.Callback() {

            @Override
            public void shouldIgnoreOrAcceptButton(FlicScanWizard wizard, String bdAddr, Runnable acceptCallback) {
                super.shouldIgnoreOrAcceptButton(wizard, bdAddr, acceptCallback);
            }

            @Override
            public void onDiscovered(FlicScanWizard wizard, String bdAddr, int rssi, boolean isPrivateMode, int revision) {
                String text = isPrivateMode ? "Found private button. Hold down for 7 seconds." : "Found Flic, now connecting...";
                ((TextView) findViewById(R.id.scanWizardStatus)).setText(text);
            }

            @Override
            public void onBLEConnected(FlicScanWizard wizard, String bdAddr) {
                ((TextView) findViewById(R.id.scanWizardStatus)).setText("Connected. Now pairing...");
            }

            @Override
            public void onCompleted(FlicScanWizard wizard, FlicButton button) {
                findViewById(R.id.scanNewButton).setEnabled(true);
                ((MyApplication) getApplication()).listenToButtonWithToast(button);

                ((TextView) findViewById(R.id.scanWizardStatus)).setText("Scan wizard success!");
                setupEventListenerForButtonInActivity(button);
            }

            @Override
            public void onFailed(FlicScanWizard wizard, int flicScanWizardErrorCode) {
                findViewById(R.id.scanNewButton).setEnabled(true);
                ((TextView) findViewById(R.id.scanWizardStatus)).setText("Scan wizard failed with code " + flicScanWizardErrorCode);
            }
        });

        ///////////////////////
    }
}