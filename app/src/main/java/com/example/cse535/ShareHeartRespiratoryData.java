package com.example.cse535;

public class ShareHeartRespiratoryData {

    private static ShareHeartRespiratoryData shareHeartRespiratoryData;
    private int respiratoryValue;
    private int heartRateValue;

    public static ShareHeartRespiratoryData getInstance() {
        if (shareHeartRespiratoryData != null) {
            return shareHeartRespiratoryData;
        }

        shareHeartRespiratoryData = new ShareHeartRespiratoryData();
        return shareHeartRespiratoryData;
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
}
