package com.example.cse535;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cse535.databinding.ActivityReactionTimeBinding;

import java.util.Random;

public class ReactionTimeTest extends AppCompatActivity {
    private int reactionTime = 0;

    private Handler handler;
    int rFactor = 5;

    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityReactionTimeBinding binding;

        binding = ActivityReactionTimeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        handler = new Handler();
        startTest(binding);
    }

    public void startTest(ActivityReactionTimeBinding binding){
        Log.d("ReactionTimeTest", "startTest called");
        // get dimensions of screen area below display
        int screenW = binding.testView.getWidth();
        int screenH = binding.testView.getHeight();

        /*if (screenW <= 0 || screenH <= 0) {
            // Handle the case where screen dimensions are not positive
            return;
        }*/

        Random random = new Random();
        rFactor = random.nextInt(10) + 1;

        new CountDownTimer(rFactor * 1000, 1000){
            public void onTick(long remaining){
                Log.d("tick", "" + remaining);
            }

            public void onFinish(){
                setClickListener(binding);
            }
        }.start();

        // Set the click listener initially
        //setClickListener(binding);
    }

    private void setClickListener(ActivityReactionTimeBinding binding) {
        binding.purpleCircle.setVisibility(View.VISIBLE);

        // calculate random coordinates for purple dot
        Random random = new Random();
        int dotX = random.nextInt(Math.max(1, 600 - binding.purpleCircle.getWidth()));
        int dotY = random.nextInt(Math.max(1, 600 - binding.purpleCircle.getHeight()));
        if (dotY < 36) {
            dotY = 36;
        }

        // set dimensions of purple dot
        binding.purpleCircle.setX((float) dotX);
        binding.purpleCircle.setY((float) dotY);

        long start = System.currentTimeMillis();

        // Set the click listener
        binding.purpleCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long end = System.currentTimeMillis();
                long rt = end - start - 200;
                setTime((int) rt);

                resetTest(binding);

                Intent resIntent = new Intent();
                resIntent.putExtra("reactionTime", getTime());
                setResult(Activity.RESULT_OK, resIntent);

                finish();
            }
        });
    }

    private void resetTest(ActivityReactionTimeBinding binding){
        binding.purpleCircle.setVisibility(View.INVISIBLE);
    }

    // getters and setters
    public int getTime() {return reactionTime;}

    public void setTime(int reactionTime) {this.reactionTime = reactionTime;}
}
