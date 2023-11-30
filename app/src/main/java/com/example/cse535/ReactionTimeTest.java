package com.example.cse535;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cse535.databinding.ActivityReactionTimeBinding;

import java.util.Random;

public class ReactionTimeTest extends AppCompatActivity {
    private double reactionTime = 0;

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

        // get dimensions of screen area below display
        int screenW = binding.getRoot().getWidth();
        int screenH = binding.getRoot().getHeight();

        // calculate random coordinates for purple dot
        Random random = new Random();
        int dotX = random.nextInt(screenW - binding.purpleCircle.getWidth());
        int dotY = random.nextInt(screenH - binding.purpleCircle.getHeight());
        if (dotY < 36){
            dotY = 36;
        }

        // set dimensions of purple dot
        binding.purpleCircle.setX((float) dotX);
        binding.purpleCircle.setY((float) dotY);

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
                        long rt = (end - start) / 1000;
                        setTime((double) rt);

                        Intent resIntent = new Intent();
                        resIntent.putExtra("reactionTime", getTime());
                        setResult(Activity.RESULT_OK, resIntent);

                        resetTest(binding);
                        finish();
                    }
                });
            }
        }.start();
    }

    private void resetTest(ActivityReactionTimeBinding binding){
        binding.purpleCircle.setVisibility(View.INVISIBLE);
        binding.timerDisplay.setText("Seconds Remaining: ");
    }

    // getters and setters
    public double getTime() {return reactionTime;}

    public void setTime(double reactionTime) {this.reactionTime = reactionTime;}
}
