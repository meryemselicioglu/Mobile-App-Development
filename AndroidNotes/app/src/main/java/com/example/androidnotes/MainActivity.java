package com.example.androidnotes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, View.OnLongClickListener {

    private static final String TAG = "MainActivity";
    private static final int ADD_CODE = 1;
    private static final int EDIT_CODE = 2;

    private final List<Notes> notesList = new ArrayList<>();
    private RecyclerView recyclerView;
    private NotesAdapter nAdapter;
    private Notes note;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "Created");

        recyclerView = findViewById(R.id.recycler);
        nAdapter = new NotesAdapter(notesList, this);
        recyclerView.setAdapter(nAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        notesList.clear();
        notesList.addAll(loadFile());
        nAdapter.notifyDataSetChanged();

        if(nAdapter.getItemCount() == 0) {
            setTitle("My Notes");
        } else {
            setTitle("My Notes (" + nAdapter.getItemCount() + ")");
        }
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "Note clicked");
        int pos = recyclerView.getChildLayoutPosition(v);
        note = notesList.get(pos);

        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra("NOTE", note);
        setResult(RESULT_OK, intent);
        startActivityForResult(intent, EDIT_CODE);
    }

    @Override
    public boolean onLongClick(View v) {
        Log.d(TAG, "Note long clicked");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Deletion");
        builder.setMessage("Are you sure you want to delete this note?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                int pos = recyclerView.getChildLayoutPosition(v);
                if (!notesList.isEmpty()) {
                    notesList.remove(pos);
                    nAdapter.notifyDataSetChanged();

                    if(nAdapter.getItemCount() == 0) {
                        setTitle("My Notes");
                    } else {
                        setTitle("My Notes (" + nAdapter.getItemCount() + ")");
                    }
                    Toast.makeText(MainActivity.this, "Note Deleted!", Toast.LENGTH_SHORT).show();
                    updateJSON();
                }
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {}
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.opt_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "Options item selected: " + item.getItemId());

        if (item.getItemId() == R.id.opt_add) {
            Intent intent = new Intent(this, EditActivity.class);
            startActivityForResult(intent, ADD_CODE);
            return true;
        } else if (item.getItemId() == R.id.opt_info) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        } else {
            Log.d(TAG, "onOptionsItemSelected: Unexpected selection: " + item.getTitle());
            return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, @Nullable Intent data) {

        Log.d(TAG, "Activity result");
        super.onActivityResult(requestCode, resultCode, data);
        Notes n;

        if (requestCode == ADD_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    n = (Notes) data.getSerializableExtra("NOTE");
                    if ((n != null) && !n.getTitle().isEmpty()) {
                        notesList.add(0,n);
                        nAdapter.notifyDataSetChanged();
                        setTitle("Android Notes (" + nAdapter.getItemCount() + ")");
                        Toast.makeText(this,"Note Saved!", Toast.LENGTH_SHORT).show();
                        updateJSON();
                    }
                }
            } else {
                Log.d(TAG, "ADD result not OK!");
            }

        } else if (requestCode == EDIT_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    n = (Notes) data.getSerializableExtra("NOTE");
                    int isChanged = (int) data.getSerializableExtra("CHANGE");

                    if ((n != null) && !n.getTitle().isEmpty()) {
                        note.setTitle(n.getTitle());
                        note.setTime(n.getTime());
                        note.setText(n.getText());
                        if(isChanged == 1) { //if the note is changed, change its position
                            int pos = notesList.indexOf(note);
                            notesList.remove(pos);
                            notesList.add(0, note);
                            Toast.makeText(this, "Note Saved!", Toast.LENGTH_SHORT).show();
                        }
                        nAdapter.notifyDataSetChanged();
                        updateJSON();
                    }
                }
            } else {
                Log.d(TAG, "EDIT result not OK!");
            }
        } else {
            Log.d(TAG, "Unexpected code received: " + requestCode);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private ArrayList<Notes> loadFile() {
        ArrayList<Notes> nList = new ArrayList<>();
        try {
            InputStream is = getApplicationContext().openFileInput(getString(R.string.file_name));
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JSONArray jsonArray = new JSONArray(sb.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String title = jsonObject.getString("title");
                String time = jsonObject.getString("time");
                String text = jsonObject.getString("text");
                Notes nt = new Notes(title, text);
                nt.setTime(time);
                nList.add(nt);
            }
            Log.d(TAG, "JSON file is loaded");

        } catch (FileNotFoundException e) {
            Toast.makeText(this, "No JSON Note File Present", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nList;
    }

    public void updateJSON() {
        try {
            FileOutputStream fos = getApplicationContext().
                    openFileOutput(getString(R.string.file_name), Context.MODE_PRIVATE);

            PrintWriter printWriter = new PrintWriter(fos);
            printWriter.print(notesList);
            printWriter.close();
            fos.close();

            Log.d(TAG, "saveProduct: JSON:\n" + notesList.toString());
            Toast.makeText(this, "Note Saved!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.getStackTrace();
        }
    }
}
