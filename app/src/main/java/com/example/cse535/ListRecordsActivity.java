package com.example.cse535;

import android.database.Cursor;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cse535.databinding.ActivityListRecordsBinding;

import java.util.ArrayList;

public class ListRecordsActivity extends AppCompatActivity {

    private MyDatabaseHelper myDatabaseHelper;
    private ActivityListRecordsBinding binding;
    private static final String COLUMN_ID = "_id";
    private static final String HEART_RATE = "heart_rate";
    private static final String RESPIRATORY_RATE = "respiratory_rate";
    private static final String NAUSEA = "nausea";
    private static final String HEADACHE = "headache";
    private static final String DIARRHEA = "diarrhea";
    private static final String SOAR_THROAT = "soar_throat";
    private static final String FEVER = "fever";
    private static final String MUSCLE_ACHE = "muscle_ache";
    private static final String NO_SMELL_TASTE = "no_smell_taste";
    private static final String COUGH = "cough";
    private static final String BREATHLESSNESS = "breathlessness";
    private static final String TIRED = "tired";
    private static final String SLEEP = "sleep";
    private static final String RESPONSE = "response";

    private static final String REACTION_TIME = "reaction_time";

    private ArrayList<String> id, heartRate, respiratoryRate, nausea, headache, diarrhea, soarThroat, fever, muscleAche, noSmellTaste, cough, breathlessness, tired, sleepRate, response, reactionTime;

    private ListRecordsAdapter listRecordsAdapter;

    @Override
    protected void onResume() {
        super.onResume();
        getAllMetrics();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityListRecordsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        myDatabaseHelper = new MyDatabaseHelper(ListRecordsActivity.this);
        id = new ArrayList<>();
        heartRate = new ArrayList<>();
        respiratoryRate = new ArrayList<>();
        nausea = new ArrayList<>();
        headache = new ArrayList<>();
        diarrhea = new ArrayList<>();
        soarThroat = new ArrayList<>();
        fever = new ArrayList<>();
        muscleAche = new ArrayList<>();
        noSmellTaste = new ArrayList<>();
        cough = new ArrayList<>();
        breathlessness = new ArrayList<>();
        tired = new ArrayList<>();
        sleepRate = new ArrayList<>();
        response = new ArrayList<>();
        reactionTime = new ArrayList<>();

        listRecordsAdapter = new ListRecordsAdapter(
                ListRecordsActivity.this,
                id,
                heartRate,
                respiratoryRate,
                nausea,
                headache,
                diarrhea,
                soarThroat,
                fever,
                muscleAche,
                noSmellTaste,
                cough,
                breathlessness,
                tired,
                sleepRate,
                response,
                reactionTime
        );
        binding.recyclerView.setAdapter(listRecordsAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(ListRecordsActivity.this));
    }

    private void getAllMetrics() {
        Cursor cursor = myDatabaseHelper.getAllRecords();

        if (cursor != null) {
            int idIndex = cursor.getColumnIndex(COLUMN_ID);
            int heartRateIndex = cursor.getColumnIndex(HEART_RATE);
            int respiratoryRateIndex = cursor.getColumnIndex(RESPIRATORY_RATE);
            int nauseaIndex = cursor.getColumnIndex(NAUSEA);
            int headacheIndex = cursor.getColumnIndex(HEADACHE);
            int diarrheaIndex = cursor.getColumnIndex(DIARRHEA);
            int soarThroatIndex = cursor.getColumnIndex(SOAR_THROAT);
            int feverIndex = cursor.getColumnIndex(FEVER);
            int muscleAcheIndex = cursor.getColumnIndex(MUSCLE_ACHE);
            int noSmellTasteIndex = cursor.getColumnIndex(NO_SMELL_TASTE);
            int coughIndex = cursor.getColumnIndex(COUGH);
            int breathlessnessIndex = cursor.getColumnIndex(BREATHLESSNESS);
            int tiredIndex = cursor.getColumnIndex(TIRED);
            int sleepIndex = cursor.getColumnIndex(SLEEP);
            int responseIndex = cursor.getColumnIndex(RESPONSE);
            int reactionIndex = cursor.getColumnIndex(REACTION_TIME);

            while (cursor.moveToNext()) {
                if(idIndex != -1) {
                    id.add(cursor.getString(idIndex));
                }
                if(heartRateIndex != -1) {
                    heartRate.add(cursor.getString(heartRateIndex));
                }
                if(respiratoryRateIndex != -1) {
                    respiratoryRate.add(cursor.getString(respiratoryRateIndex));
                }
                if (nauseaIndex != -1) {
                    nausea.add(cursor.getString(nauseaIndex));
                }
                if (headacheIndex != -1) {
                    headache.add(cursor.getString(headacheIndex));
                }
                if (diarrheaIndex != -1) {
                    diarrhea.add(cursor.getString(diarrheaIndex));
                }
                if (soarThroatIndex != -1) {
                    soarThroat.add(cursor.getString(soarThroatIndex));
                }
                if (feverIndex != -1) {
                    fever.add(cursor.getString(feverIndex));
                }
                if (muscleAcheIndex != -1) {
                    muscleAche.add(cursor.getString(muscleAcheIndex));
                }
                if (noSmellTasteIndex != -1) {
                    noSmellTaste.add(cursor.getString(noSmellTasteIndex));
                }
                if (coughIndex != -1) {
                    cough.add(cursor.getString(coughIndex));
                }
                if (breathlessnessIndex != -1) {
                    breathlessness.add(cursor.getString(breathlessnessIndex));
                }
                if (tiredIndex != -1) {
                    tired.add(cursor.getString(tiredIndex));
                }
                if(sleepIndex != -1) {
                    sleepRate.add(cursor.getString(sleepIndex));
                }
                if(responseIndex != -1) {
                    response.add(cursor.getString(responseIndex));
                }
                if(reactionIndex != -1) {
                    reactionTime.add(cursor.getString(reactionIndex));
                }
            }
            cursor.close();
        }
    }
}