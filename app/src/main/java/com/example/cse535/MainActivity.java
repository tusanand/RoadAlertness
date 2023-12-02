package com.example.cse535;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cse535.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    MyDatabaseHelper myDatabaseHelper;
    private boolean clicked = false;
    private int heartRateValue = 0;
    private int respiratoryRateValue = 0;

    private int sleepRateValue = 8;
    private int reactionValue = 10;
    private String response = "You have a fast reaction time. We recommend using your personal car for this very long trip.";

    private BroadcastReceiver respiratoryRateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("respiratory_rate_calculated".equals(intent.getAction())) {
                respiratoryRateValue = intent.getIntExtra("respiratory_rate", 0);
                binding.respiratoryRateValue.setText("Respiratory rate: " + respiratoryRateValue);
                binding.measureRespiratoryRate.setEnabled(true);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(respiratoryRateReceiver, new IntentFilter("respiratory_rate_calculated"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(respiratoryRateReceiver);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.symptomBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SymptomsActivity.class);
            startActivity(intent);
        });

        binding.sleepBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SleepActivity.class);
            startActivity(intent);
        });

        binding.reactBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ReactionTestUIActivity.class);
            startActivity(intent);
        });

        myDatabaseHelper = new MyDatabaseHelper(MainActivity.this);


        binding.viewResponse.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ListRecordsActivity.class);
            startActivity(intent);
        });

        binding.measureHeartRate.setOnClickListener(v -> {
            binding.heartRateValue.setText("Calculating...");
            binding.measureHeartRate.setEnabled(false);
//            Intent intent = new Intent(MainActivity.this, HeartRateService.class);
//            startService(intent);
            heartRateValue = 72;
            binding.heartRateValue.setText("Heart rate: " + heartRateValue); //Setting dummy value
        });

        binding.measureRespiratoryRate.setOnClickListener(v -> {
            binding.respiratoryRateValue.setText("Calculating...");
            binding.measureRespiratoryRate.setEnabled(false);

//            Intent intent = new Intent(MainActivity.this, RespiratoryRateService.class);
//            startService(intent);

            respiratoryRateValue = 93; //setting dummy value
            binding.respiratoryRateValue.setText("Respiratory rate: " + respiratoryRateValue);
        });

        binding.reactionBtn.setOnClickListener(v -> {
            binding.reactionTime.setText("Calculating...");
            binding.reactionBtn.setEnabled(false);

//            Intent intent = new Intent(MainActivity.this, RespiratoryRateService.class);
//            startService(intent);

            reactionValue = 93; //setting dummy value
            binding.reactionTime.setText("Reaction Time: " + reactionValue);
        });

        binding.responseBtn.setOnClickListener(v -> {
//            Intent intent = new Intent(MainActivity.this, TrafficConditionsActivity.class);
//            startActivity(intent);
        });

        binding.save.setOnClickListener(v -> {
            myDatabaseHelper = new MyDatabaseHelper(MainActivity.this);
            ShareSymptomsData shareSymptomsData = ShareSymptomsData.getInstance();
            ShareSleepData shareSleepData = ShareSleepData.getInstance();
            ShareReactionTimeData shareReactionTimeData = ShareReactionTimeData.getInstance();
            myDatabaseHelper.saveRecord(
                    heartRateValue,
                    respiratoryRateValue,
                    shareSymptomsData.getFever(),
                    shareSymptomsData.getNausea(),
                    shareSymptomsData.getHeadache(),
                    shareSymptomsData.getDiarrhea(),
                    shareSymptomsData.getSoar_throat(),
                    shareSymptomsData.getMuscle_ache(),
                    shareSymptomsData.getNo_smell_taste(),
                    shareSymptomsData.getCough(),
                    shareSymptomsData.getBreathlessness(),
                    shareSymptomsData.getTired(),
                    shareSymptomsData.getSymptomComputedEffect(),
                    shareSleepData.getSleepHours(),
                    response,
                    shareReactionTimeData.getReactionTime()
            );
        });
    }




}