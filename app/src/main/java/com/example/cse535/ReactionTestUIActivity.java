package com.example.cse535;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cse535.databinding.ActivityReactionTestBinding;

import org.json.JSONObject;

public class ReactionTestUIActivity extends AppCompatActivity{

    // initialize variables
    private int reactionTime = 0;

    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        ActivityReactionTestBinding binding;

        binding = ActivityReactionTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.startBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ReactionTestUIActivity.this, ReactionTimeTest.class);
            startActivityForResult(intent, 1);
        });

        binding.saveTimeBtn.setOnClickListener(v -> {
            shareReactionTime(getReactionTime());
        });
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, @Nullable Intent data){
        super.onActivityResult(reqCode, resCode, data);
        Log.d("ActivityResult", "onActivityResult called with requestCode: " + reqCode + ", resultCode: " + resCode);
        if (reqCode == 1 && resCode == Activity.RESULT_OK && data != null){
            int rtime = data.getIntExtra("reactionTime", 0);
            Log.d("ActivityResult", "Received reaction time: " + rtime);
            setReactionTime(rtime);
            Log.d("reactionTime", "" + getReactionTime());
        }
    }

    private void shareReactionTime(int rtime) {
        ShareReactionTimeData reactionTimeData = ShareReactionTimeData.getInstance();
        reactionTimeData.setReactionTime(rtime);
        Log.d("tag", "" + reactionTimeData.getReactionTime());

        if (!isFinishing()){
            finish();
        }
    }

    // getter and setter
    public void setReactionTime(int reactionTime) {this.reactionTime = reactionTime;}
    public int getReactionTime() {return reactionTime;}
}
