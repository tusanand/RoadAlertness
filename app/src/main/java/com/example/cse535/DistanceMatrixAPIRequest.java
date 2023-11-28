package com.example.Maxwell;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DistanceMatrixAPIRequest extends AsyncTask<String, Void, DistanceMatrixResult> {

    private static final String TAG = DistanceMatrixAPIRequest.class.getSimpleName();
    private static final String API_KEY = "AIzaSyBSabO4ZnsfYZoyl3Mgv2o6TVB-PZ-gPFs";

    private DistanceMatrixCallback callback;

    public DistanceMatrixAPIRequest(DistanceMatrixCallback callback) {
        this.callback = callback;
    }

    @Override
    protected DistanceMatrixResult doInBackground(String... params) {
        String origin = params[0];
        String destination = params[1];

        try {
            URL url = new URL("https://maps.googleapis.com/maps/api/distancematrix/json" +
                    "?departure_time=now" +
                    "&destinations=" + destination +
                    "&origins=" + origin +
                    "&key=" + API_KEY);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            Log.d("URL", url.toString());

            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }

                bufferedReader.close();

                String responseJson = stringBuilder.toString();
                return parseDistanceMatrixApiResponse(responseJson);
            } finally {
                urlConnection.disconnect();
            }
        } catch (IOException e) {
            Log.e(TAG, "Error making API request: " + e.getMessage());
        }

        return null;
    }

    private DistanceMatrixResult parseDistanceMatrixApiResponse(String responseJson) {
        try {
            JSONObject jsonResponse = new JSONObject(responseJson);

            int distance = jsonResponse.getJSONArray("rows")
                    .getJSONObject(0)
                    .getJSONArray("elements")
                    .getJSONObject(0)
                    .getJSONObject("distance")
                    .getInt("value");

            int duration = jsonResponse.getJSONArray("rows")
                    .getJSONObject(0)
                    .getJSONArray("elements")
                    .getJSONObject(0)
                    .getJSONObject("duration")
                    .getInt("value");

            int durationInTraffic = jsonResponse.getJSONArray("rows")
                    .getJSONObject(0)
                    .getJSONArray("elements")
                    .getJSONObject(0)
                    .getJSONObject("duration_in_traffic")
                    .getInt("value");

            String status = jsonResponse.getString("status");

            return new DistanceMatrixResult(distance, duration, durationInTraffic);

        } catch (JSONException e) {
            Log.e(TAG, "Error parsing API response: " + e.getMessage());
        }

        return null;
    }

    @Override
    protected void onPostExecute(DistanceMatrixResult result) {
        if (callback != null) {
            callback.onDistanceMatrixResult(result);
        }
    }

    public interface DistanceMatrixCallback {
        void onDistanceMatrixResult(DistanceMatrixResult result);
    }
}
