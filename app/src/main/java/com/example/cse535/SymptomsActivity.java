package com.example.cse535;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.cse535.databinding.ActivitySymptomsBinding;

public class SymptomsActivity extends AppCompatActivity {

    private int heartRateValue = 0;
    private int respiratoryRateValue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySymptomsBinding binding;

        binding = ActivitySymptomsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.feverBar.setRating(0);
        binding.feverBar.setStepSize(1);

        binding.nauseaBar.setRating(0);
        binding.nauseaBar.setStepSize(1);

        binding.headacheBar.setRating(0);
        binding.headacheBar.setStepSize(1);

        binding.diarrheaBar.setRating(0);
        binding.diarrheaBar.setStepSize(1);

        binding.soarThroatBar.setRating(0);
        binding.soarThroatBar.setStepSize(1);

        binding.muscleAcheBar.setRating(0);
        binding.muscleAcheBar.setStepSize(1);

        binding.noSmellTasteBar.setRating(0);
        binding.noSmellTasteBar.setStepSize(1);

        binding.coughBar.setRating(0);
        binding.coughBar.setStepSize(1);

        binding.breathlessnessBar.setRating(0);
        binding.breathlessnessBar.setStepSize(1);

        binding.tiredBar.setRating(0);
        binding.tiredBar.setStepSize(1);

        Intent intent = getIntent();
        if(intent != null) {
            heartRateValue = Integer.parseInt(String.valueOf(intent.getSerializableExtra("heartRateValue")));
            respiratoryRateValue = Integer.parseInt(String.valueOf(intent.getSerializableExtra("respiratoryRateValue")));
        }

        binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(SymptomsActivity.this);
                myDatabaseHelper.saveRecord(
                        heartRateValue,
                        respiratoryRateValue,
                        binding.feverBar.getRating(),
                        binding.nauseaBar.getRating(),
                        binding.headacheBar.getRating(),
                        binding.diarrheaBar.getRating(),
                        binding.soarThroatBar.getRating(),
                        binding.muscleAcheBar.getRating(),
                        binding.noSmellTasteBar.getRating(),
                        binding.coughBar.getRating(),
                        binding.breathlessnessBar.getRating(),
                        binding.tiredBar.getRating()
                );
                finish();
            }
        });
    }
}