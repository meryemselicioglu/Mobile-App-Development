package com.example.androidnotes;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotesAdapter extends RecyclerView.Adapter<NotesViewHolder> {

    private static final String TAG = "NotesAdapter";
    private final List<Notes> notesList;
    private final MainActivity mainAct;

    public NotesAdapter(List<Notes> ntsList, MainActivity ma){
       this.notesList = ntsList;
       mainAct = ma;
    }
    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: MAKING NEW MyViewHolder");

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notes_list_row, parent, false);

        itemView.setOnClickListener(mainAct);
        itemView.setOnLongClickListener(mainAct);

        return new NotesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: FILLING VIEW HOLDER Notes " + position);

        Notes note = notesList.get(position);

        holder.title.setText(note.getTitle());
        holder.time.setText(note.getTime());

        if(note.getText().length() > 80){
            String displayText = note.getText().substring(0,80) + "...";
            holder.text.setText(displayText);
        } else {
            holder.text.setText(note.getText());
        }
    }

    @Override
    public int getItemCount() {

        return notesList.size();
    }

}
