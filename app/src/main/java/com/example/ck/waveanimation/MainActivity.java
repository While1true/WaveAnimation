package com.example.ck.waveanimation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WaveView waveView= (WaveView) findViewById(R.id.waveline);
        waveView.setWaveHeightListener(new WaveView.WaveHeightListener() {
            @Override
            public void currentWaveHeightMove(float currentWavePercent) {
                Log.i("WaveView", "currentWaveHeightScal: "+currentWavePercent);
            }
        });
    }
}
