package com.example.cse535;

public class ShareCrashSpeedData {
    private static ShareCrashSpeedData crashSpeedData;

    private int crashSpeed;

    public static ShareCrashSpeedData getInstance() {
        if (crashSpeedData != null) {
            return crashSpeedData;
        }

        crashSpeedData = new ShareCrashSpeedData();
        return crashSpeedData;
    }

    public int getCrashSpeed() {
        return crashSpeed;
    }

    public void setCrashSpeed(int crashSpeed) {
        this.crashSpeed = crashSpeed;
    }

}
