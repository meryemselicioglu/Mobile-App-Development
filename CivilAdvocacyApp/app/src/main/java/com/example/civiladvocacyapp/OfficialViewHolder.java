package com.example.civiladvocacyapp;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class OfficialViewHolder extends RecyclerView.ViewHolder {
    public TextView officeName, officialInfo;

    public OfficialViewHolder(View view) {
        super(view);
        officeName = view.findViewById(R.id.officeName);
        officialInfo = view.findViewById(R.id.officialInfo); ;
    }
}
