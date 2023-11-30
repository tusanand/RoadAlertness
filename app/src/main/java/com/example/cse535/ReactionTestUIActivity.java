package com.example.cse535;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cse535.databinding.ActivityReactionTestBinding;

public class ReactionTestUIActivity extends AppCompatActivity{

    // initialize variables
    private long reactionTime = 0;

    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        ActivityReactionTestBinding binding;

        binding = ActivityReactionTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.startBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ReactionTestUIActivity.this, ReactionTimeTest.class);
            startActivity(intent);
        });
    }
}
