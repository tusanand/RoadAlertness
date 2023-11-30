package com.example.cse535;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import com.example.cse535.databinding.ActivitySleepInputBinding;

public class SleepActivity extends AppCompatActivity {
    // initialize variables
    private int sleepAmt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        ActivitySleepInputBinding binding;

        final String[] hours = {"0", "1", "2", "3", "4", "5", "6", "7",
                                "8", "9", "10", "11", "12", "13", "14"};

        binding = ActivitySleepInputBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ArrayAdapter<String> hoursAdapt = new ArrayAdapter<>(this,
                                                             android.R.layout.simple_spinner_item,
                                                             hours);
        hoursAdapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.hoursAmt.setAdapter(hoursAdapt);

        binding.hoursAmt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setSleepAmt(Integer.parseInt(hours[position]));
                shareHours(getSleepAmt());
            }
        });
    }

    private void shareHours(int hours){
        ShareSleepData sleepData = ShareSleepData.getInstance();
        sleepData.setSleepHours(hours);
        finish();
    }

    // getters and setters
    public int getSleepAmt() {return sleepAmt;}
    public void setSleepAmt(int sleepAmt) {this.sleepAmt = sleepAmt;}

}
