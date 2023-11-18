package com.example.cse535;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.cse535.databinding.ActivityViewSymptomsBinding;

public class ViewSymptomsActivity extends AppCompatActivity {

    private ActivityViewSymptomsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewSymptomsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        if (intent != null) {
            Bundle extras = intent.getExtras();
            binding.nauseaBar1.setRating(Integer.parseInt(extras.getString("nausea", "0")));
            binding.headacheBar1.setRating(Integer.parseInt(extras.getString("headache", "0")));
            binding.diarrheaBar1.setRating(Integer.parseInt(extras.getString("diarrhea", "0")));
            binding.soarThroatBar1.setRating(Integer.parseInt(extras.getString("soarThroat", "0")));
            binding.feverBar1.setRating(Integer.parseInt(extras.getString("fever", "0")));
            binding.muscleAcheBar1.setRating(Integer.parseInt(extras.getString("muscleAche", "0")));
            binding.noSmellTasteBar1.setRating(Integer.parseInt(extras.getString("noSmellTaste", "0")));
            binding.coughBar1.setRating(Integer.parseInt(extras.getString("cough", "0")));
            binding.breathlessnessBar1.setRating(Integer.parseInt(extras.getString("breathlessness", "0")));
            binding.tiredBar1.setRating(Integer.parseInt(extras.getString("tired", "0")));
        }
    }
}