package com.example.cse535;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.cse535.databinding.ActivityFuzzyTestBinding;

public class FuzzyTestActivity extends AppCompatActivity {

    private ActivityFuzzyTestBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuzzy_test);
        binding = ActivityFuzzyTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.TestFuzzyButton.setOnClickListener(v -> onButtonClicked());
    }

    private void onButtonClicked() {

        String hr = binding.HRText.getText().toString();
        double hrDouble = Double.parseDouble(hr);

        String rr = binding.RRText.getText().toString();
        double rrDouble = Double.parseDouble(rr);

        String sleep = binding.SleepText.getText().toString();
        double sleepDouble = Double.parseDouble(sleep);

        String symptom = binding.SymptText.getText().toString();
        double symptomDouble = Double.parseDouble(symptom);

        double outputReaction = 0; //TRFuzzyLogicController.ComputeFuzzy(hrDouble, rrDouble, sleepDouble, symptomDouble);

        String outputText = "TR(ms): " + outputReaction;
        binding.OutputText.setText(outputText);

    }
}