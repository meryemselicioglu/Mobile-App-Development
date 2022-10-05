package com.example.civiladvocacyapp;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OfficialAdapter extends RecyclerView.Adapter<OfficialViewHolder>{
    private final List<Official> officialList;
    private final MainActivity mainAct;

    OfficialAdapter(List<Official> oList, MainActivity ma) {
        this.officialList = oList;
        mainAct = ma;
    }
    @NonNull
    @Override
    public OfficialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.official_list, parent, false);

        itemView.setOnClickListener((View.OnClickListener) mainAct);
        return new OfficialViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull OfficialViewHolder holder, int position) {
        Official official = officialList.get(position);
        String officeName = official.getOfficeName();
        String officialName = official.getOfficialName();
        String party = official.getParty();

        holder.officeName.setText(officeName);
        holder.officialInfo.setText(officialName + " ("+party+")");

    }

    @Override
    public int getItemCount() {
        return officialList.size();
    }
}
