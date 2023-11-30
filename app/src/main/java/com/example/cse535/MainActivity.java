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
        getMetaData();
        onAddButtonClicked(false);
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

        binding.addBtn.setOnClickListener(view -> onAddButtonClicked(!clicked));

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
        binding.totalCount.setText("0");
        binding.averageHeartRate.setText("Average Heart rate: 0");
        binding.averageRespiratoryRate.setText("Average Respiratory rate: 0");

        binding.cardView.setOnClickListener(v -> {
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

        binding.gotoMapBtn.setOnClickListener(v -> {
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
                    shareSymptomsData.getSymptomComputedEffect()
            );
            getMetaData();
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


    private void onAddButtonClicked(boolean isClicked) {
        clicked = isClicked;
        setVisibility();
        setAnimation();
        isClickable();
    }

    private void setVisibility() {
        if(clicked) {
            binding.symptomBtn.setVisibility(View.VISIBLE);
            binding.symptomTitle.setVisibility(View.VISIBLE);
        } else {
            binding.symptomBtn.setVisibility(View.INVISIBLE);
            binding.symptomTitle.setVisibility(View.INVISIBLE);
        }
    }

    private void setAnimation() {
        if(clicked) {
            Animation rotateOpen = AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim);
            Animation fromBottom = AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim);
            binding.symptomBtn.startAnimation(fromBottom);
            binding.symptomTitle.startAnimation(fromBottom);
            binding.addBtn.startAnimation(rotateOpen);
        } else {
            Animation rotateClose = AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim);
            Animation toBottom = AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim);
            binding.symptomBtn.startAnimation(toBottom);
            binding.symptomTitle.startAnimation(toBottom);
            binding.addBtn.startAnimation(rotateClose);
        }
    }

    private void isClickable() {
        binding.symptomBtn.setClickable(clicked);
    }

    private void getMetaData() {
        Cursor cursor = myDatabaseHelper.getRecordsMetaData();

        if(cursor != null && cursor.moveToFirst()) {
            binding.averageHeartRate.setText("Average Heart rate: " + String.format("%.3f", cursor.getDouble(0)));
            binding.averageRespiratoryRate.setText("Average Respiratory rate: " +  String.format("%.3f", cursor.getDouble(1)));
            binding.totalCount.setText(String.valueOf(cursor.getInt(2)));
            cursor.close();
        }
    }
}