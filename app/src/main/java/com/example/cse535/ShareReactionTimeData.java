package com.example.cse535;

public class ShareReactionTimeData {
    private static ShareReactionTimeData reactionTimeData;

    private double reactionTime;

    public static ShareReactionTimeData getInstance() {
        if (reactionTimeData != null) {
            return reactionTimeData;
        }

        reactionTimeData = new ShareReactionTimeData();
        return reactionTimeData;
    }

    // getters and setters
    public double getReactionTime() {return reactionTime;}

    public void setReactionTime(double reactionTime) {this.reactionTime = reactionTime;}
}
