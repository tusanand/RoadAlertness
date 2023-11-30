package com.example.cse535;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cse535.databinding.ActivityReactionTimeBinding;

public class ReactionTimeTest extends AppCompatActivity {
    private long reactionTime = 0;

    private Handler handler;

    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityReactionTimeBinding binding;

        binding = ActivityReactionTimeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        handler = new Handler();
    }

    public void startTest(ActivityReactionTimeBinding binding){
        binding.purpleCircle.setVisibility(View.VISIBLE);

        new CountDownTimer(3000, 1000){
            public void onTick(long remaining){
                binding.timerDisplay.setText("Seconds Remaining: " + remaining);
            }

            public void onFinish(){
                long start = System.currentTimeMillis();
                binding.purpleCircle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        long end = System.currentTimeMillis();

                    }
                });
            }
        };
    }

    // getters and setters
    public long getReactionTime() {return reactionTime;}

    public void setReactionTime(long reactionTime) {this.reactionTime = reactionTime;}
}
