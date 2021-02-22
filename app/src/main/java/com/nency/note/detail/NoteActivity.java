package com.nency.note.detail;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nency.note.R;
import com.nency.note.dashboard.MainActivity;
import com.nency.note.room.Note;
import com.nency.note.room.NoteRoomDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class NoteActivity extends AppCompatActivity {

    int noteId = -1;
    EditText title, description;
    TextView category, date, location;
    ImageView iconImage, iconAudio;
    Button saveNote;

    private NoteRoomDatabase noteRoomDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        noteId = getIntent().getIntExtra("NoteId", -1);
        // Room db
        noteRoomDatabase = noteRoomDatabase.getInstance(this);

        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        category = findViewById(R.id.category);
        date = findViewById(R.id.date);
        location = findViewById(R.id.location);
        iconImage = findViewById(R.id.iconImage);
        iconAudio = findViewById(R.id.iconAudio);

        iconImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageListDialogFragment.newInstance().show(getSupportFragmentManager(), "dialog");
            }
        });

        saveNote = findViewById(R.id.saveNote);
        saveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(noteId < 0) {
                    addNote();
                }else {
                    updateNote();
                }
            }
        });
        
        if(noteId >= 0) {
            Note note = noteRoomDatabase.NoteDoa().getNote(noteId);
            title.setText(note.getTitle());
            description.setText(note.getDescription());
            location.setText(note.getPlaceAddress());
            date.setText(note.getDate());
//            category.setText(note.getca());
        }
        
    }

    private void updateNote() {
        String noteTitle = title.getText().toString();
        String noteDesc = description.getText().toString();

        noteRoomDatabase.NoteDoa().updateNote(noteId, noteTitle, noteDesc);
        redirectAllNotes();
    }

    private void addNote() {
        String noteTitle = title.getText().toString();
        String noteDesc = description.getText().toString();

        String location = "Canada";

        // getting the current time
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd hh:mm:ss", Locale.CANADA);
        String createdDate = sdf.format(cal.getTime());
        date.setText(createdDate);

        if (noteTitle.isEmpty()) {
            title.setError("Note title cannot be empty.");
            title.requestFocus();
            return;
        }
        if (noteDesc.isEmpty()) {
            description.setError("Note description cannot be empty");
            description.requestFocus();
            return;
        }

        // Insert note into room
        Note note = new Note(noteTitle, noteDesc, createdDate, location);
        noteRoomDatabase.NoteDoa().insertNote(note);
        Toast.makeText(this, "Note Added", Toast.LENGTH_SHORT).show();
        redirectAllNotes();
    }

    private void redirectAllNotes() {
//        Intent i = new Intent(getApplicationContext(), MainActivity.class);
//        startActivity(i);
        finish();
    }
}