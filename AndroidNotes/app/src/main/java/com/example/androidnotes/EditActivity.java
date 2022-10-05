package com.example.androidnotes;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class EditActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private String currentTitle, currentText;
    EditText noteTitle, noteText;
    private Notes n;

    private AlertDialog.Builder builder;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        setTitle("My Notes");
        Log.d(TAG, "Created");

        noteTitle = findViewById(R.id.editTitle);
        noteText = findViewById(R.id.editText);

        Intent intent = getIntent();
        if (intent.hasExtra("NOTE")) {
            n = (Notes)intent.getSerializableExtra("NOTE");
            if (n != null) {
                noteTitle.setText(n.getTitle());
                noteText.setText(n.getText());
            } else {
                n = new Notes("", "");
            }
        } else {
            n = new Notes("", "");
        }
        currentTitle = noteTitle.getText().toString();
        currentText = noteText.getText().toString();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "Options item selected: " + item.getItemId());

        if (item.getItemId() == R.id.opt_save) {
            checkTitle();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "Back pressed");

        if(currentTitle.equals((noteTitle.getText().toString()))
                && currentText.equals(noteText.getText().toString())){
            super.onBackPressed();
        } else {
            builder = new AlertDialog.Builder(this);
            builder.setTitle("Save Changes");
            builder.setMessage("Your note is not saved! " +
                    "\nDo you want to save changes before returning?");

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    checkTitle();
                }
            });
            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(EditActivity.this, "Note Not Saved", Toast.LENGTH_SHORT).show();
                    EditActivity.super.onBackPressed();
                }
            });
            dialog = builder.create();
            dialog.show();
        }
    }

    public void saveNote() {
        Log.d(TAG, "Saving note");

        Intent intent = new Intent();
        int isChanged;
        if(currentTitle.equals(noteTitle.getText().toString()) &&
                currentText.equals(noteText.getText().toString())) {
            isChanged = 0;
        } else{
            isChanged = 1;
            n.setTime();
        }

        n.setTitle(noteTitle.getText().toString());
        n.setText(noteText.getText().toString());

        intent.putExtra("NOTE", n);
        intent.putExtra("CHANGE", isChanged);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void checkTitle() {
        if(noteTitle.getText().toString().isEmpty()) {
            builder = new AlertDialog.Builder(this);
            builder.setTitle("Add Title");
            builder.setMessage("Note with no title cannot be saved\n" +
                    "Select Cancel to continue editing\n" + "Select OK to exit without saving");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(EditActivity.this, "Note Not Saved!", Toast.LENGTH_SHORT).show();
                    saveNote();
                }
            });

            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            dialog = builder.create();
            dialog.show();
        } else {
            saveNote();
        }
    }
}