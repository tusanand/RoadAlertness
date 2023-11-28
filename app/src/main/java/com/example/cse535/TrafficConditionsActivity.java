package com.example.cse535;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class TrafficConditionsActivity extends AppCompatActivity implements DistanceMatrixAPIRequest.DistanceMatrixCallback {

    Button makeRequest;
    String originOG, destinationOG, roadCondition;
    EditText originText, desText;
    TextView displayArea;
    final double reactionTime = 0.7324;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic_conditions);

        originText = (EditText) findViewById(R.id.startLocation);
        desText = (EditText) findViewById(R.id.destination);
        displayArea = (TextView) findViewById(R.id.displayArea);

        makeRequest = findViewById(R.id.fetchData);
        originText.setText("Tempe, AZ");
        desText.setText("Scottsdale, AZ");

        makeRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                originOG = originText.getText().toString();
                destinationOG = desText.getText().toString();

                String origin = originOG.replace(" ", "%20").replace(",", "%2C");
                String destination = destinationOG.replace(" ", "%20").replace(",", "%2C");

                makeDistanceMatrixRequest(origin, destination);
            }
        });
    }

    private void makeDistanceMatrixRequest(String origin, String destination) {
        DistanceMatrixAPIRequest apiRequest = new DistanceMatrixAPIRequest(this);
        apiRequest.execute(origin, destination);
    }

    @Override
    public void onDistanceMatrixResult(DistanceMatrixResult result) {
        if (result != null) {
            int distance = result.getDistance();
            int duration = result.getDuration();
            int durationInTraffic = result.getDurationInTraffic();
            int reactionTime = 550; // In milliseconds. This is just a testing value; actual value will be anywhere from 1-1000

            Log.d("CheckDur", "Duration" + durationInTraffic);

            recommendationAlgo(distance, duration, durationInTraffic, reactionTime);

        } else {
            // Handle the case where the result is null or an error occurred
            Log.d("Error", "API Result is Null");
        }
    }

    public void recommendationAlgo(int distance, int duration, int durationInTraffic, int reactionTime) {

        double speedWithoutTraffic = distance / (double) duration;
        double speedWithTraffic = distance / (double) durationInTraffic;

        // Assign threshold for reaction time (in MS)
        int fastReactionTimeThreshold = 300;
        int averageReactionTimeThreshold = 600;

        // Assigning threshold for road condition
        double poorRoadConditionThreshold = 0.7;

        String recommendation = "No specific recommendation";
        if (distance >= 500000) {

            // Very long trip, recommend plane or train
            if (durationInTraffic <= 36000) {

                // Consider train for shorter very long trips
                if (reactionTime <= fastReactionTimeThreshold) {

                    // Very long trip with fast reaction time, recommend personal car
                    recommendation = "You have a fast reaction time. We recommend using your personal car for this very long trip.";
                } else if (reactionTime <= averageReactionTimeThreshold) {

                    // Very long trip with average reaction time, recommend personal car or train
                    recommendation = "You have an average reaction time. Using your personal car should be fine, but you should consider taking a train or plane for this very long trip.";
                } else {

                    // Very long trip with poor reaction time, recommend train or plane
                    recommendation = "Your reaction time is poor. We recommend taking a train or a plane for this very long trip.";
                }
            } else {

                // Consider plane for longer very long trips
                recommendation = "For this very long trip, we recommend taking a plane for faster travel. You can use your car, but it may take well over a day.";
            }
        } else if (reactionTime <= fastReactionTimeThreshold) {

            // Fast reaction time, consider personal car or bike
            if (distance <= 10000) {

                recommendation = "You have a fast reaction time. Since your destination is close, we recommend using a bike. " +
                        "If you are in a hurry, your car would also be a good option";
            } else {

                recommendation = "You have a fast reaction time. Since your destination is far, we recommend using your car.";
            }
        } else if (reactionTime <= averageReactionTimeThreshold) {

            // Average reaction time, consider road conditions
            if (speedWithTraffic >= speedWithoutTraffic * poorRoadConditionThreshold) {

                // Poor road conditions, recommend public transit options
                if (durationInTraffic <= 1800) {

                    // Short trip, consider bus, taxi, or bike
                    if (distance <= 5000) {

                        recommendation = "Road conditions are poor. We softly recommend taking a bus or taxi, though your reaction time suggests taking your car should be fine. " +
                                "A bike may also be a good option, though it may be a long journey.";
                    } else {

                        recommendation = "Road conditions are poor. We softly recommend taking a bus or taxi, though your reaction time suggests taking your car should be fine.";
                    }
                } else {

                    // Longer trip, consider train, Uber, or bike
                    if (distance <= 10000) {

                        recommendation = "Road conditions are poor. We softly recommend taking a train or Uber for this medium-length trip, though your reaction time suggests taking your car should be fine.";
                    } else {

                        recommendation = "Road conditions are poor. We softly recommend taking a train or Uber, though your reaction time suggests taking your car should be fine. ";
                    }
                }
            } else {

                // Normal road conditions, consider personal car or bike
                if (distance <= 5000) {

                    recommendation = "Road conditions are normal. We recommend using your car or bike since this is a short trip.";
                } else {

                    recommendation = "Road conditions are normal. We recommend using your car. You can also use your bike, but it may be a long journey.";
                }
            }
        } else {

            // Poor reaction time, recommend public transit options or bike
            if (durationInTraffic <= 1200) {

                // Short trip, recommend bus, taxi, or bike
                if (distance <= 5000) {

                    recommendation = "Your reaction time is poor. We recommend taking a bus or taxi. " +
                            "Since your destination is somewhat close, biking may also be a good option.";
                } else {

                    recommendation = "Your reaction time is poor. We recommend taking a bus or taxi, especially since this is a long trip.";
                }
            } else if (durationInTraffic <= 2400) {

                // Medium trip, recommend train, Uber, or bike
                if (distance <= 10000) {

                    recommendation = "Your reaction time is poor. We recommend taking a train or Uber.";
                } else {

                    recommendation = "Your reaction time is poor. We highly recommend taking a train or Uber, especially since this is a long trip.";
                }
            } else {

                // Longer trip, recommend train, long-distance taxi, or bike
                if (distance <= 15000) {

                    recommendation = "Your reaction time is poor. We recommend taking a train or Uber since this is a longer trip.";
                } else {

                    recommendation = "Your reaction time is poor. Since this is a very long trip, we highly recommend taking a train or Uber.";
                }
            }
        }

        // Print the recommendation
        Log.d("Recommendation", recommendation);
        displayArea.setText(recommendation);
    }
}
