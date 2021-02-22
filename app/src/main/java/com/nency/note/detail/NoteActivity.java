package com.nency.note.detail;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nency.note.R;
import com.nency.note.room.Converter;
import com.nency.note.room.Note;
import com.nency.note.room.NoteRoomDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class NoteActivity extends AppCompatActivity {

    int noteId = -1;
    EditText title, description;
    TextView category, date, location;
    ImageView iconImage, iconAudio;
    Button saveNote;
    ArrayList<Uri> imageList = new ArrayList<>();

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
                ImageListDialogFragment.newInstance(imageList).show(getSupportFragmentManager(), "dialog");
            }
        });

        iconAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioRecordFragment.newInstance().show(getSupportFragmentManager(), "dialog");
            }
        });

        // getting the current time
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd hh:mm:ss", Locale.CANADA);
        String createdDate = sdf.format(cal.getTime());
        date.setText(createdDate);

        saveNote = findViewById(R.id.saveNote);
        saveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noteId < 0) {
                    addNote();
                } else {
                    updateNote();
                }
            }
        });

        if (noteId >= 0) {
            Note note = noteRoomDatabase.NoteDoa().getNote(noteId);
            title.setText(note.getTitle());
            description.setText(note.getDescription());
            location.setText(note.getPlaceAddress());
            date.setText(note.getDate());
            List<String> images = note.getImages();
            for (String path : images) {
                if (!TextUtils.isEmpty(path)) {
                    imageList.add(Uri.parse(path));
                }
            }
//            category.setText(note.getca());
        }

    }

    private void updateNote() {
        String noteTitle = title.getText().toString();
        String noteDesc = description.getText().toString();

        ArrayList<String> images = new ArrayList<>();
        for (Uri uri : imageList) {
            images.add(uri.toString());
        }

        noteRoomDatabase.NoteDoa().updateNote(noteId, noteTitle, noteDesc, Converter.toString(images));
        redirectAllNotes();
    }

    private void addNote() {
        String noteTitle = title.getText().toString();
        String noteDesc = description.getText().toString();

        String location = "Canada";

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

        ArrayList<String> images = new ArrayList<>();
        for (Uri uri : imageList) {
            images.add(uri.toString());
        }

        // Insert note into room
        Note note = new Note(noteTitle, noteDesc, date.getText().toString(), location, images);
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