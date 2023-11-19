package com.example.cse535;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;

import com.example.cse535.databinding.ActivityFuzzyTestBinding;
import com.example.cse535.databinding.ActivityMainBinding;

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

        Editable hr = binding.HRText.getText();
        Editable rr = binding.RRText.getText();
        Editable tr = binding.TRText.getText();
        Editable sleep = binding.SleepText.getText();
        Editable sympt = binding.SymptText.getText();


        binding.OutputText.setText("TESTTESTTEST");


    }
}