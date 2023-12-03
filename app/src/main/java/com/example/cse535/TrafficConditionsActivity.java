package com.example.cse535;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TrafficConditionsActivity extends AppCompatActivity implements DistanceMatrixAPIRequest.DistanceMatrixCallback {

    Button makeRequest;
    String originOG, destinationOG, roadCondition;
    EditText originText, desText;
    TextView displayArea;
    ShareRecommendationData rec;
    ShareCrashSpeedData shareCSD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        shareCSD = ShareCrashSpeedData.getInstance();

        rec = ShareRecommendationData.getInstance();
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

                if (originOG != null && destinationOG != null) {

                    String origin = originOG.replace(" ", "%20").replace(",", "%2C");
                    String destination = destinationOG.replace(" ", "%20").replace(",", "%2C");
                    makeDistanceMatrixRequest(origin, destination);
                } else {

                    displayArea.setText("");
                }
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

            try {
                recommendationAlgo(distance, duration, durationInTraffic);
            }
            catch (Exception e) {
                Toast.makeText(this, "Error generating recommendations. " +
                        "Make sure all inputs are given.", Toast.LENGTH_LONG).show();
            }
        } else {
            // Handle the case where the result is null or an error occurred
            Log.d("Error", "API Result is Null");
            displayArea.setText("There was an issue generating your recommendation. Please ensure your origin and destination are correct.");
        }
    }

    public void recommendationAlgo(int distance, int duration, int durationInTraffic) throws Exception {

        double speedWithoutTraffic = distance / (double) duration;
        double speedWithTraffic = distance / (double) durationInTraffic;

        if (speedWithTraffic <= speedWithoutTraffic) {

            roadCondition = "HCW";
        } else {

            roadCondition = "LCW";
        }

        double reactionTime = TRFuzzyLogicController.ComputeFuzzy();
        CrashChanceService ccs = new CrashChanceService();

        ccs.getCrashChanceAsync(roadCondition, reactionTime, new CrashChanceService.CrashChanceListener() {
            @Override
            public void onCrashChanceCalculated(String crashChance) {
                int crashSpeed = shareCSD.getCrashSpeed();
                // USE CRASH A KPH INSTEAD OF REACTION TIME
                String recommendation = "No specific recommendation";
                if (distance >= 500000) {

                    // Very long trip, recommend plane or train
                    if (durationInTraffic <= 72000) {

                        // Consider train for shorter very long trips
                        if (crashSpeed >= 140) {

                            // Very long trip with low crash risk, recommend personal car
                            recommendation = "You are not at risk of crashing. We recommend using your personal car for this very long trip.";
                        } else if (crashSpeed >= 100) {

                            // Very long trip with mild crash risk recommend personal car or train
                            recommendation = "You are at mild risk of crashing. Using your personal car should be fine, but you should consider taking a train or plane for this very long trip.";
                        } else if (crashSpeed >= 60) {

                            recommendation = "You are at risk of crashing. You should consider taking a train or plane for this very long trip. If you do use your personal car, avoid driving on freeways and take breaks when necessary.";
                        } else {

                            // Very long trip with severe crash risk, recommend train or plane
                            recommendation = "You are at severe risk of crashing. It is highly recommended you take a plane or train for this very long trip.";
                        }
                    } else {

                        // Consider plane for longer very long trips
                        recommendation = "For this very long trip, we recommend taking a plane for faster travel.";
                    }
                } else if (crashSpeed >= 140) {

                    // Low crash risk, consider personal car or bike
                    if (distance <= 5000) {

                        recommendation = "You are not at risk of crashing. Since your destination is close, we recommend using a bike. " +
                                "If you are in a hurry, your car would also be a good option";
                    } else {

                        recommendation = "You are not at risk of crashing. Your car is a solid transportation option.";
                    }

                } else if (crashSpeed >= 100) {

                    // Average reaction time, consider road conditions
                    if (roadCondition.equals("HCW") == true) {

                        Log.d("Test", speedWithTraffic + " and " + speedWithoutTraffic);
                        // Poor road conditions, recommend public transit options
                        if (durationInTraffic <= 1800) {

                            // Short trip, consider bus, taxi, or bike
                            if (distance <= 5000) {

                                recommendation = "Road conditions are poor. We softly recommend taking a bus or taxi, though taking your car should be fine. " +
                                        "A bike may also be a good option, though it may be a long journey.";
                            } else {

                                recommendation = "Road conditions are poor. We softly recommend taking a bus or taxi, though taking your car should be fine.";
                            }
                        } else {

                            // Longer trip, consider train, Uber, or bike
                            if (distance <= 10000) {

                                recommendation = "Road conditions are poor. We softly recommend taking a train or Uber for this medium-length trip, though your reaction time suggests taking your car should be fine.";
                            } else {

                                recommendation = "Road conditions are poor. We softly recommend taking a train or Uber, though your reaction time suggests taking your car should be fine. ";
                            }
                        }
                    } else if (roadCondition.equals("LCW") == true) {

                        // Normal road conditions, consider personal car or bike
                        if (distance <= 5000) {

                            recommendation = "Road conditions are normal and your crash risk is mild. We recommend using your car or bike since this is a short trip.";
                        } else {

                            recommendation = "Road conditions are normal and your crash risk is mild. We recommend using your car.";
                        }
                    }

                } else if (crashSpeed >= 60) {

                    // Poor reaction time, recommend public transit options or bike
                    if (roadCondition.equals("LCW") == true) {
                        if (durationInTraffic <= 1200) {

                            // Short trip, recommend bus, taxi, or bike
                            if (distance <= 5000) {

                                recommendation = "Road conditions are normal, but you are at risk of crashing. We recommend taking a bus or taxi. " +
                                        "Since your destination is close, it will be fine to use your personal car as long as you do not exceed " + crashSpeed + " KPH.";
                            } else {

                                recommendation = "Road conditions are normal, but you are at risk of crashing. We recommend taking a bus or taxi. " +
                                        "If you do use your personal car, it is highly recommended to avoid freeways.";
                            }
                        } else if (durationInTraffic <= 2400) {

                            // Medium trip, recommend train, Uber, or bike
                            if (distance <= 10000) {

                                recommendation = "Road conditions are normal, but you are at risk of crashing. We recommend taking a train or Uber. " +
                                        "Since your destination is fairly close, it will be fine to use your personal car as long as you do not exceed " + crashSpeed + " KPH.";
                            } else {

                                recommendation = "Road conditions are normal, but you are at risk of crashing. We recommend taking a train or Uber. " +
                                        "If you do use your personal car, it is highly recommended to avoid freeways.";
                            }
                        } else {

                            // Longer trip, recommend train, long-distance taxi, or bike
                            if (distance <= 15000) {

                                recommendation = "Road conditions are normal, but you are at risk of crashing. We recommend taking a train or Uber.";
                            } else {

                                recommendation = "Road conditions are normal, but you are at risk of crashing. We highly recommend taking a train or Uber since this is a longer trip.";
                            }
                        }
                    } else if (roadCondition.equals("HCW") == true) {
                        if (durationInTraffic <= 1200) {

                            // Short trip, recommend bus, taxi, or bike
                            if (distance <= 5000) {

                                recommendation = "Road conditions are poor and you are at risk of crashing. We recommend taking a bus or taxi. " +
                                        "Since your destination is close, biking may be a good alternative.";
                            } else {

                                recommendation = "Road conditions are poor and you are at risk of crashing. We recommend taking a bus or taxi.";
                            }
                        } else if (durationInTraffic <= 2400) {

                            // Medium trip, recommend train, Uber, or bike
                            if (distance <= 10000) {

                                recommendation = "Road conditions are poor and you are at risk of crashing. We recommend taking a train or Uber. You could consider your bike as an alternative, but it may be a long journey.";
                            } else {

                                recommendation = "Road conditions are poor and you are at risk of crashing. We recommend taking a train or Uber.";
                            }
                        } else {

                            // Longer trip, recommend train, long-distance taxi, or bike
                            recommendation = "Road conditions are poor and you are at risk of crashing. We highly recommend taking a train or possibly plane since this is a longer trip.";
                        }

                    }

                } else {

                    if (durationInTraffic <= 1200) {

                        // Short trip, recommend bus, taxi, or bike
                        if (distance <= 5000) {

                            recommendation = "You are at severe risk of crashing. We highly recommend taking a bus or taxi. " +
                                    "Since your destination is close, biking may be a good alternative.";
                        } else {

                            recommendation = "You are at severe risk of crashing. We highly recommend taking a bus or taxi.";
                        }
                    } else if (durationInTraffic <= 2400) {

                        // Medium trip, recommend train, Uber, or bike
                        if (distance <= 10000) {

                            recommendation = "You are at severe risk of crashing. We recommend taking a train or Uber.";
                        } else {

                            recommendation = "You are at severe risk of crashing. We highly recommend taking a train or Uber.";
                        }
                    } else {

                        // Longer trip
                        recommendation = "You are at severe risk of crashing. We highly recommend taking a train or possibly plane since this is a longer trip.";
                    }
                }

                // Print the recommendation
                Log.d("Recommendation", recommendation);
                rec.setRecommendation(recommendation);
                displayArea.setText(recommendation);

            }
        });

    }
}
