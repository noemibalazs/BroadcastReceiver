package com.example.android.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Vibrator vibrator;
    private IntentFilter myFilter;
    private static final int CHARGED_LEVEL = 63;
    private static final String NAME = "name";

    private BatteryBroadcastReceiver myReceiver;

    private TextView text;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        myFilter = new IntentFilter();
        myFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        myFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);

        myReceiver = new BatteryBroadcastReceiver();

        text = findViewById(R.id.battery);
        progressBar = findViewById(R.id.progressBar);
    }


    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent intent = registerReceiver(null, filter);

        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        double percentage = level / (double) scale;
        int actionLevel = (int)(percentage * 100);

        text.setText(String.format("Battery level: %s%%", Integer.toString(actionLevel)));
        progressBar.setProgress(actionLevel);

        setVibrator(actionLevel);
        registerReceiver(myReceiver, myFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(myReceiver);

    }

    private void setVibrator(int level){

        if(level == CHARGED_LEVEL) {
            vibrator.vibrate(30000);
        }
    }

    private class BatteryBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                int level = intent.getIntExtra(NAME, 0);
                setVibrator(level);
            }

        }
    }
}
