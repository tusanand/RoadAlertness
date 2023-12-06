package com.example.cse535;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cse535.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private MyDatabaseHelper myDatabaseHelper;
    private int heartRateValue = 0;
    private int respiratoryRateValue = 0;

    private int reactionValue = 10;
    private ShareRecommendationData rec;
    private ShareReactionTimeData shareReactionTimeData;
    private ShareHeartRespiratorySleepData shareHeartRespiratorySleepData;

    private BroadcastReceiver respiratoryRateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("respiratory_rate_calculated".equals(intent.getAction())) {
                respiratoryRateValue = intent.getIntExtra("respiratory_rate", 0);
                binding.respiratoryRateValue.setText("Respiratory rate: " + respiratoryRateValue);
                shareHeartRespiratorySleepData.setRespiratoryValue(respiratoryRateValue);
                binding.measureRespiratoryRate.setEnabled(true);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(respiratoryRateReceiver, new IntentFilter("respiratory_rate_calculated"));

        if(shareReactionTimeData.getReactionTime() != 0) {
            binding.reactionTime.setText("Reaction Time: " + shareReactionTimeData.getReactionTime());
        }
        else {
            binding.reactionTime.setText("No reaction time generated");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(respiratoryRateReceiver);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        shareReactionTimeData = ShareReactionTimeData.getInstance();
        shareHeartRespiratorySleepData = ShareHeartRespiratorySleepData.getInstance();
        rec = ShareRecommendationData.getInstance();

        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if(shareReactionTimeData.getReactionTime() != 0) {
            binding.reactionTime.setText("Reaction Time: " + shareReactionTimeData.getReactionTime() + "ms");
        }
        else {
            binding.reactionTime.setText("No reaction time generated");
        }
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
            binding.reactionBtn.setEnabled(false);

            Intent intent = new Intent(MainActivity.this, ReactionTestUIActivity.class);
            startActivity(intent);

            Log.d("main_reaction_time", "" + shareReactionTimeData.getReactionTime());
        });

        binding.responseBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TrafficConditionsActivity.class);
            startActivity(intent);
        });

        binding.sleepRate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
                // This method is called to notify you that characters within start to start + before are about to be replaced with new text with a length of count.
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // This method is called to notify you that somewhere within start to start + before characters have been replaced with new text with a length of count.
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // This method is called to notify you that the characters within Editable have changed.
                String newText = editable.toString();
                // Do something with the changed text
                try {
                    shareHeartRespiratorySleepData.setSleepValue(Double.parseDouble(newText));
                } catch (Exception e) {
                    shareHeartRespiratorySleepData.setSleepValue(0.0);
                }
            }
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
                    (rec.getRecommendation() != null) ? rec.getRecommendation() : "A recommendation has not been generated",
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
                                shareHeartRespiratorySleepData.setHeartRateValue(heartRateValue);
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