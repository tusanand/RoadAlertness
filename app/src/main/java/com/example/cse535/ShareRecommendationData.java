package com.example.cse535;

public class ShareRecommendationData {
    private static ShareRecommendationData recommendationData;

    private String recommendation;

    public static ShareRecommendationData getInstance() {

        if (recommendationData != null) {

            return recommendationData;
        }

        recommendationData = new ShareRecommendationData();
        return recommendationData;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }
}

