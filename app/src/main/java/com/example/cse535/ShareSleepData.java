package com.example.cse535;

public class ShareSleepData {
    private static ShareSleepData sleepData;

    private int sleepHours;

    public static ShareSleepData getInstance() {
        if (sleepData != null) {return sleepData;}

        sleepData = new ShareSleepData();
        return sleepData;
    }

    // getters and setters
    public void setSleepHours(int sleepHours) {this.sleepHours = sleepHours;}

    public int getSleepHours() {return sleepHours;}
}
