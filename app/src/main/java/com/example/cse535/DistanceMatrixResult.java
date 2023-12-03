package com.example.cse535;

public class DistanceMatrixResult {
    private int distance;
    private int duration;
    private int durationInTraffic;

    public DistanceMatrixResult(int distance, int duration, int durationInTraffic) {

        this.distance = distance;
        this.duration = duration;
        this.durationInTraffic = durationInTraffic;
    }

    public int getDistance() {

        return distance;
    }

    public int getDuration() {

        return duration;
    }

    public int getDurationInTraffic() {

        return durationInTraffic;
    }
}
