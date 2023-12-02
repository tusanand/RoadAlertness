package com.example.cse535;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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

        binding.crashChance.setOnClickListener(v -> {
            binding.crashChance.setEnabled(false);


            String cogWorkload = "HCW";
            int reactionTime = -1;

            try {

                if (String.valueOf(binding.crashChanceInputCog.getText()).equals("LCW")) {
                    cogWorkload = "LCW";
                } else if (String.valueOf(binding.crashChanceInputCog.getText()).equals("HCW") == false) {
                    throw new Exception();
                }
                reactionTime = Integer.parseInt(String.valueOf(binding.crashChanceInputReaction.getText()));
                if (reactionTime <= 0) {
                    throw new Exception();
                }

                binding.crashChanceLabel.setText("Calculating...");
                CrashChanceService ccs = new CrashChanceService();
                ccs.getCrashChanceAsync(cogWorkload, reactionTime, new CrashChanceService.CrashChanceListener() {
                    @Override
                    public void onCrashChanceCalculated(String crashChance) {
                        if (crashChance != null) {
                            binding.crashChanceLabel.setText("Speed of crash: " + crashChance + " km/h");
                            binding.crashChance.setEnabled(true);
                        } else {
                            Toast.makeText(MainActivity.this, "Calculating crash chance failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            catch (Exception e) {
                binding.crashChance.setEnabled(true);
                Toast.makeText(MainActivity.this, "Invalid inputs. Try again", Toast.LENGTH_SHORT).show();
            }
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

        binding.FuzzyButton.setOnClickListener( v -> {
            Intent intent = new Intent(MainActivity.this, FuzzyTestActivity.class);
            startActivity(intent);
        });

        binding.save.setOnClickListener(v -> {
            myDatabaseHelper = new MyDatabaseHelper(MainActivity.this);
            ShareSymptomsData shareSymptomsData = ShareSymptomsData.getInstance();
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
                    Integer.parseInt(String.valueOf(binding.sleepRate.getText())),
                    response,
                    shareReactionTimeData.getReactionTime()
            );
        });
    }

}