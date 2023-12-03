package com.example.cse535;

public class ShareHeartRespiratorySleepData {

    private static ShareHeartRespiratorySleepData shareHeartRespiratorySleepData;
    private int respiratoryValue;
    private int heartRateValue;
    private double sleepValue = 8.0;

    public static ShareHeartRespiratorySleepData getInstance() {
        if (shareHeartRespiratorySleepData != null) {
            return shareHeartRespiratorySleepData;
        }

        shareHeartRespiratorySleepData = new ShareHeartRespiratorySleepData();
        return shareHeartRespiratorySleepData;
    }

    public int getRespiratoryValue() {
        return respiratoryValue;
    }

    public void setRespiratoryValue(int respiratoryValue) {
        this.respiratoryValue = respiratoryValue;
    }

    public int getHeartRateValue() {
        return heartRateValue;
    }

    public void setHeartRateValue(int heartRateValue) {
        this.heartRateValue = heartRateValue;
    }

    public double getSleepValue() { return sleepValue; }

    public void setSleepValue(double sleepValue) { this.sleepValue = sleepValue; }
}
