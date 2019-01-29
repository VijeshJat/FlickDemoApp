package com.flair.flickdemoapp.utility;

import android.app.Application;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

import com.flair.flickdemoapp.R;

import io.flic.poiclib.FlicButton;
import io.flic.poiclib.FlicButtonAdapter;
import io.flic.poiclib.FlicManager;

/**
 * Created by Vijesh Jat on 17-12-2018.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();


        FlicManager.init(this.getApplicationContext(), "2125f7c3-0d0e-42d5-88fe-fda8765867d6", "94d6448c-22d3-4d2e-951f-f625f60f471a");

        startService(new Intent(this.getApplicationContext(), PbfService.class));


        FlicManager manager = FlicManager.getManager();

        for (FlicButton button : manager.getKnownButtons()) {
            button.connect();
            listenToButtonWithToast(button);
        }
    }

    public void listenToButtonWithToast(FlicButton button) {



        button.addEventListener(new FlicButtonAdapter() {

            @Override
            public void onButtonSingleOrDoubleClick(FlicButton button, boolean wasQueued, int timeDiff, boolean isSingleClick, boolean isDoubleClick) {
                super.onButtonSingleOrDoubleClick(button, wasQueued, timeDiff, isSingleClick, isDoubleClick);

                if (isSingleClick){
                    Toast.makeText(getApplicationContext(), "Button is single click for some action", Toast.LENGTH_SHORT).show();

                }else if(isDoubleClick){
                    Toast.makeText(getApplicationContext(), "Button is double click for some action", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onButtonClickOrHold(FlicButton button, boolean wasQueued, int timeDiff, boolean isClick, boolean isHold) {
                super.onButtonClickOrHold(button, wasQueued, timeDiff, isClick, isHold);
                if (isHold){
                    Toast.makeText(getApplicationContext(), "Button is hold for some action", Toast.LENGTH_SHORT).show();

                }

            }

        });
    }
}
