package com.example.cse535;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cse535.databinding.ListRecyclerViewDataBinding;

import java.util.ArrayList;

public class ListRecordsAdapter extends RecyclerView.Adapter<ListRecordsAdapter.MyViewHolder> {

    private final Context context;
    private final ArrayList<String> id, heartRate, respiratoryRate, nausea, headache, diarrhea, soarThroat, fever, muscleAche, noSmellTaste, cough, breathlessness, tired;
    ListRecordsAdapter(
            Context context,
            ArrayList<String> id,
            ArrayList<String> heartRate,
            ArrayList<String> respiratoryRate,
            ArrayList<String> nausea,
            ArrayList<String> headache,
            ArrayList<String> diarrhea,
            ArrayList<String> soarThroat,
            ArrayList<String> fever,
            ArrayList<String> muscleAche,
            ArrayList<String> noSmellTaste,
            ArrayList<String> cough,
            ArrayList<String> breathlessness,
            ArrayList<String> tired
    ) {

        this.context = context;
        this.id = id;
        this.heartRate = heartRate;
        this.respiratoryRate = respiratoryRate;
        this.nausea = nausea;
        this.headache = headache;
        this.diarrhea = diarrhea;
        this.soarThroat = soarThroat;
        this.fever = fever;
        this.muscleAche = muscleAche;
        this.noSmellTaste = noSmellTaste;
        this.cough = cough;
        this.breathlessness = breathlessness;
        this.tired = tired;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ListRecyclerViewDataBinding binding = ListRecyclerViewDataBinding.inflate(inflater, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return heartRate.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final ListRecyclerViewDataBinding binding;
        public MyViewHolder(@NonNull ListRecyclerViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(int position) {
            binding.recordId.setText(id.get(position));
            binding.heartRate.setText("Heart rate: " + Integer.parseInt(heartRate.get(position)));
            binding.respiratoryRate.setText("Respiratory rate: " + Integer.parseInt(respiratoryRate.get(position)));
            binding.cardViewListRecycler.setOnClickListener(v -> {
                Intent intent = new Intent(context, ViewSymptomsActivity.class);

                Bundle b = new Bundle();

                b.putString("nausea", nausea.get(position));
                b.putString("headache", headache.get(position));
                b.putString("diarrhea", diarrhea.get(position));
                b.putString("soarThroat", soarThroat.get(position));
                b.putString("fever", fever.get(position));
                b.putString("muscleAche", muscleAche.get(position));
                b.putString("noSmellTaste", noSmellTaste.get(position));
                b.putString("cough", cough.get(position));
                b.putString("breathlessness", breathlessness.get(position));
                b.putString("tired", tired.get(position));

                intent.putExtras(b);

                context.startActivity(intent);
            });
        }
    }
}
