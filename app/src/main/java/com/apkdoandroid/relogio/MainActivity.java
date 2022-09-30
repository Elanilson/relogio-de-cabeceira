package com.apkdoandroid.relogio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private TextView textViewHora, textViewSegundos,textViewBateria,textViewBoanoite;
    private BroadcastReceiver broadcastReceiver;
    private Runnable runnable;
    private Handler  handler = new Handler();
    private Boolean ticker = false;
    private Boolean lampscap = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewHora = findViewById(R.id.textViewHora_minuto);
        textViewSegundos = findViewById(R.id.textViewSegundos);
        textViewBoanoite = findViewById(R.id.textViewBoanoite);
        textViewBateria = findViewById(R.id.bateria);

        if(getSupportActionBar() != null){
            getSupportActionBar().hide();

        }




        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);
                textViewBateria.setText(level+"%");
            }
        };


    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        ticker = true;
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            lampscap = true;
        }
        startClock();
    }

    private void startClock(){
          final Calendar calendar = Calendar.getInstance();
            this.runnable = new Runnable() {
                @Override
                public void run() {

                    if(!ticker){
                        return;
                    }
                    calendar.setTimeInMillis(System.currentTimeMillis());

                    int hora = calendar.get(Calendar.HOUR_OF_DAY);
                    int minutos = calendar.get(Calendar.MINUTE);
                    int segundos = calendar.get(Calendar.SECOND);

                    if(lampscap){
                        if(hora >= 18){
                            textViewBoanoite.setVisibility(View.VISIBLE);
                        }else{
                            textViewBoanoite.setVisibility(View.GONE);

                        }
                    }


                    textViewHora.setText(hora+":"+minutos);
                    textViewSegundos.setText(segundos+"");
                    Long now = SystemClock.uptimeMillis();
                    Long next = now + (1000 - (now % 1000));
                  handler.postAtTime(runnable,next);

                }
            };
            this.runnable.run();
        }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            // Esconde nav bar e status bar
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
            );

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ticker = false;
        unregisterReceiver(broadcastReceiver);
    }
}