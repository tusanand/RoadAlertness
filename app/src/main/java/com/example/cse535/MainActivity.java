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
    private String recommendation = "";

    private MyDatabaseHelper myDatabaseHelper;
    private int heartRateValue = 0;
    private int respiratoryRateValue = 0;

    private int reactionValue = 10;
    private ShareRecommendationData rec;
    private ShareReactionTimeData shareReactionTimeData;
    private ShareHeartRespiratoryData shareHeartRespiratoryData;

    private BroadcastReceiver respiratoryRateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("respiratory_rate_calculated".equals(intent.getAction())) {
                respiratoryRateValue = intent.getIntExtra("respiratory_rate", 0);
                binding.respiratoryRateValue.setText("Respiratory rate: " + respiratoryRateValue);
                shareHeartRespiratoryData.setRespiratoryValue(respiratoryRateValue);
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

        shareReactionTimeData = ShareReactionTimeData.getInstance();
        shareHeartRespiratoryData = ShareHeartRespiratoryData.getInstance();
        rec = ShareRecommendationData.getInstance();
        recommendation = rec.getRecommendation();
        if (recommendation == null) {

            recommendation = "A recommendation has not been generated";
        }

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
            openFileDialog();
        });

        binding.measureRespiratoryRate.setOnClickListener(v -> {
            binding.respiratoryRateValue.setText("Calculating...");
            binding.measureRespiratoryRate.setEnabled(false);

            Intent intent = new Intent(MainActivity.this, RespiratoryRateService.class);
            startService(intent);
        });

        binding.reactionBtn.setOnClickListener(v -> {
            binding.reactionTime.setText("Calculating...");
            binding.reactionBtn.setEnabled(false);

            Intent intent = new Intent(MainActivity.this, ReactionTestUIActivity.class);
            startActivity(intent);

            Log.d("main_reaction_time", "" + shareReactionTimeData.getReactionTime());

            binding.reactionTime.setText("Reaction Time: " + shareReactionTimeData.getReactionTime());
        });

        binding.responseBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TrafficConditionsActivity.class);
            startActivity(intent);
        });

        binding.save.setOnClickListener(v -> {
            myDatabaseHelper = new MyDatabaseHelper(MainActivity.this);
            ShareSymptomsData shareSymptomsData = ShareSymptomsData.getInstance();
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
                    recommendation,
                    shareReactionTimeData.getReactionTime()
            );
        });
    }

    public void openFileDialog() {
        Intent data = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        data.setType("video/*");
        data = Intent.createChooser(data, "Select file");
        mGetContent.launch(data);
    }

    ActivityResultLauncher<Intent> mGetContent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
        new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult activityResult) {
                if(activityResult.getResultCode() == Activity.RESULT_OK) {
                    Intent data = activityResult.getData();
                    Uri uri = data.getData();

                    HeartRateService heartRateService = new HeartRateService();
                    binding.heartRateValue.setText("Calculating...");
                    binding.heartRateValue.setEnabled(false);
                    Toast.makeText(MainActivity.this, "Service started.", Toast.LENGTH_SHORT).show();
                    heartRateService.getHeartRateAsync(MainActivity.this, uri, new HeartRateService.HeartRateListener() {
                        @Override
                        public void onHeartRateCalculated(String heartRate) {
                            if (heartRate != null) {
                                heartRateValue = Integer.parseInt(heartRate);
                                shareHeartRespiratoryData.setHeartRateValue(heartRateValue);
                                binding.heartRateValue.setText("Heart rate: " + heartRateValue);
                                binding.heartRateValue.setEnabled(true);
                            } else {
                                Toast.makeText(MainActivity.this, "Heart rate calculation failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

}