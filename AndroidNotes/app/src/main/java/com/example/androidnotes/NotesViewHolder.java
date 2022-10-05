package com.example.androidnotes;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class NotesViewHolder extends RecyclerView.ViewHolder {

    TextView title;
    TextView text;
    TextView time;

    NotesViewHolder(View view) {
        super(view);
        title = view.findViewById(R.id.title);
        text = view.findViewById(R.id.text);
        time = view.findViewById(R.id.dateTime);
    }

}
