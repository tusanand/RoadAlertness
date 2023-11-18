package com.example.cse535;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.cse535.databinding.ActivityTrafficConditionsBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class TrafficConditionsActivity extends AppCompatActivity {

    private ActivityTrafficConditionsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTrafficConditionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.startLocation.setText("Tempe, AZ");
        binding.destination.setText("Scottsdale, AZ");

        binding.fetchData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new APICallTask(binding.startLocation.getText().toString(), binding.destination.getText().toString()).execute();
            }
        });
    }

    private class APICallTask extends AsyncTask<Void, Void, String> {
        private String startPoint;
        private String endPoint;
        APICallTask(String startPoint, String endPoint) {
            this.startPoint = startPoint;
            this.endPoint = endPoint;
        }
        @Override
        protected String doInBackground(Void... voids) {
            try {
                // Define the API URL
                String googleApiKey = "Enter-Your-API-Key"; // Replace with your actual API key

                String apiUrl = "https://maps.googleapis.com/maps/api/distancematrix/json?" +
                        "origins=" + startPoint + "&destinations=" + endPoint +
                        "&mode=driving&departure_time=now&key=" + googleApiKey;

                // Create a URL object
                URL url = new URL(apiUrl);

                // Create an HttpURLConnection
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                // Read the response
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();

                // Return the response as a string
                return response.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null) {
                // Handle the response here
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    // Extract elements from the JSON response
                    JSONArray rows = jsonObject.getJSONArray("rows");
                    ArrayList<Integer> durations_in_traffic = new ArrayList<>();
                    ArrayList<Integer> durations = new ArrayList<>();
                    ArrayList<Integer> distances = new ArrayList<>();

                    for (int i = 0; i < rows.length(); i++) {
                        JSONArray elements = rows.getJSONObject(i).getJSONArray("elements");
                        for (int j = 0; j < elements.length(); j++) {
                            JSONObject element = elements.getJSONObject(j);
                            int d_in_traffic = element.getJSONObject("duration_in_traffic").getInt("value");
                            durations_in_traffic.add(d_in_traffic);
                            int d = element.getJSONObject("duration").getInt("value");
                            durations.add(d);
                            int dis = element.getJSONObject("distance").getInt("value");
                            distances.add(dis);
                        }
                    }
                    float speedInTraffic = distances.get(0)/durations_in_traffic.get(0);
                    float speedWithoutTraffic = distances.get(0)/durations.get(0);
                    Log.d("Response Speed in traffic:", String.valueOf(speedInTraffic) + "m/s");
                    Log.d("Response Speed without traffic:", String.valueOf(speedWithoutTraffic) + "m/s");

                    String displayText = "Speed in traffic: " + speedInTraffic + "m/s" +
                            "\nSpeed without traffic: " + speedWithoutTraffic + "m/s";

                    Float threshold = 5.0f;

                    if (Math.abs(speedInTraffic - speedWithoutTraffic) > threshold) {
                        Log.d("API Response", "Current road conditions are poor.");
                        displayText += "\nRoad condition: Poor";
                    } else {
                        Log.d("API Response", "Current road conditions are normal.");
                        displayText += "\nRoad condition: Normal";
                    }
                    binding.displayArea.setText(displayText);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("API Error", "JSON parsing error");
                }
            } else {
                // Handle errors or no response
                Log.e("API Error", "API call failed");
            }
        }
    }
}
