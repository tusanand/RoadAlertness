package com.example.cse535;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.cse535.databinding.ActivitySymptomsBinding;

import java.util.HashMap;
import java.util.Map;

public class SymptomsActivity extends AppCompatActivity {

    private int heartRateValue = 0;
    private int respiratoryRateValue = 0;

    private Map<String, Double> symptomWeightage = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySymptomsBinding binding;

        binding = ActivitySymptomsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.feverBar.setRating(0);
        binding.feverBar.setStepSize(1);
        symptomWeightage.put("fever", 0.75);

        binding.nauseaBar.setRating(0);
        binding.nauseaBar.setStepSize(1);
        symptomWeightage.put("nausea", 0.6);

        binding.headacheBar.setRating(0);
        binding.headacheBar.setStepSize(1);
        symptomWeightage.put("headache", 0.6);

        binding.diarrheaBar.setRating(0);
        binding.diarrheaBar.setStepSize(1);
        symptomWeightage.put("diarrhea", 0.5);

        binding.soarThroatBar.setRating(0);
        binding.soarThroatBar.setStepSize(1);
        symptomWeightage.put("soarThroat", 0.3);

        binding.muscleAcheBar.setRating(0);
        binding.muscleAcheBar.setStepSize(1);
        symptomWeightage.put("muscleAche", 0.3);

        binding.noSmellTasteBar.setRating(0);
        binding.noSmellTasteBar.setStepSize(1);
        symptomWeightage.put("noSmellTaste", 0.0);

        binding.coughBar.setRating(0);
        binding.coughBar.setStepSize(1);
        symptomWeightage.put("cough", 0.2);

        binding.breathlessnessBar.setRating(0);
        binding.breathlessnessBar.setStepSize(1);
        symptomWeightage.put("breathlessness", 1.0);

        binding.tiredBar.setRating(0);
        binding.tiredBar.setStepSize(1);
        symptomWeightage.put("tired", 0.4);

        binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                computeSymptomsEffectOnDrivingAlertness(binding);
            }
        });
    }

    private void computeSymptomsEffectOnDrivingAlertness(ActivitySymptomsBinding binding) {
        double computedEffect = binding.feverBar.getRating() * symptomWeightage.get("fever") +
            binding.nauseaBar.getRating() * symptomWeightage.get("nausea") +
            binding.headacheBar.getRating() * symptomWeightage.get("headache") +
            binding.diarrheaBar.getRating() * symptomWeightage.get("diarrhea") +
            binding.soarThroatBar.getRating() * symptomWeightage.get("soarThroat") +
            binding.muscleAcheBar.getRating() * symptomWeightage.get("muscleAche") +
            binding.noSmellTasteBar.getRating() * symptomWeightage.get("noSmellTaste") +
            binding.coughBar.getRating() * symptomWeightage.get("cough") +
            binding.breathlessnessBar.getRating() * symptomWeightage.get("breathlessness") +
            binding.tiredBar.getRating() * symptomWeightage.get("tired");

        ShareSymptomsData shareSymptomsData = ShareSymptomsData.getInstance();
        shareSymptomsData.setSymptomComputedEffect(computedEffect);
        shareSymptomsData.setFever((int)binding.feverBar.getRating());
        shareSymptomsData.setNausea((int)binding.nauseaBar.getRating());
        shareSymptomsData.setHeadache((int)binding.headacheBar.getRating());
        shareSymptomsData.setDiarrhea((int)binding.diarrheaBar.getRating());
        shareSymptomsData.setSoar_throat((int)binding.soarThroatBar.getRating());
        shareSymptomsData.setMuscle_ache((int)binding.muscleAcheBar.getRating());
        shareSymptomsData.setNo_smell_taste((int)binding.noSmellTasteBar.getRating());
        shareSymptomsData.setCough((int)binding.coughBar.getRating());
        shareSymptomsData.setBreathlessness((int)binding.breathlessnessBar.getRating());
        shareSymptomsData.setTired((int)binding.tiredBar.getRating());

        finish();
    }
}