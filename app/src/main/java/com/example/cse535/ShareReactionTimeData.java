package com.example.cse535;

public class ShareReactionTimeData {
    private static ShareReactionTimeData reactionTimeData;

    private int reactionTime;

    public static ShareReactionTimeData getInstance() {
        if (reactionTimeData != null) {
            return reactionTimeData;
        }

        reactionTimeData = new ShareReactionTimeData();
        return reactionTimeData;
    }

    // getters and setters
    public int getReactionTime() {return reactionTime;}

    public void setReactionTime(int reactionTime) {this.reactionTime = reactionTime;}
}
