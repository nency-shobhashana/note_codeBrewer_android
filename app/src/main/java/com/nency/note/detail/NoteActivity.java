package com.nency.note.detail;

import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

    private static final int LOCATION_REQUEST = 101;

    int noteId = -1;

    EditText title, description;
    TextView category, date, location;
    ImageView iconCategory, iconImage, iconAudio;
    Button saveNote;

    ArrayList<Uri> imageList = new ArrayList<>();
    ArrayList<String> recordsList = new ArrayList<>();

    private LocationHandler locationHandler;
    private double lat = 0, lng = 0;
    private String address = "";

    private NoteRoomDatabase noteRoomDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        noteId = getIntent().getIntExtra("NoteId", -1);
        // Room db
        noteRoomDatabase = noteRoomDatabase.getInstance(this);

        initViews();
        initListener();

        // getting the current time
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd hh:mm:ss", Locale.CANADA);
        String createdDate = sdf.format(cal.getTime());
        date.setText(createdDate);

        if (noteId >= 0) {
            Note note = noteRoomDatabase.NoteDoa().getNote(noteId);
            title.setText(note.getTitle());
            description.setText(note.getDescription());
            location.setText(note.getAddress());
            date.setText(note.getDate());
            List<String> images = note.getImages();
            for (String path : images) {
                if (!TextUtils.isEmpty(path)) {
                    imageList.add(Uri.parse(path));
                }
            }
            recordsList.addAll(note.getRecords());
//            category.setText(note.getca());
        } else {
            initLocation();
        }

    }

    private void initViews() {
        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        category = findViewById(R.id.category);
        date = findViewById(R.id.date);
        location = findViewById(R.id.location);
        iconCategory = findViewById(R.id.iconCategory);
        iconImage = findViewById(R.id.iconImage);
        iconAudio = findViewById(R.id.iconAudio);
        saveNote = findViewById(R.id.saveNote);
    }

    private void initListener() {
        iconCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategoryListDialogFragment.newInstance()
                        .show(getSupportFragmentManager(), "dialog");
            }
        });
        iconImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageListDialogFragment.newInstance(imageList)
                        .show(getSupportFragmentManager(), "dialog");
            }
        });
        iconAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioRecordFragment.newInstance(recordsList)
                        .show(getSupportFragmentManager(), "dialog");
            }
        });
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
    }

    private void initLocation() {
        locationHandler = new LocationHandler(this, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull android.location.Location userLocation) {
                updateLocation(userLocation);
            }
        });

        if (locationHandler.hasLocationPermission(this)) {
            updateLocation(locationHandler.startUpdateLocation(this));
        } else {
            locationHandler.requestLocationPermission(this, LOCATION_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        if (LOCATION_REQUEST == requestCode) {
            if (locationHandler.hasLocationPermission(this)) {
                updateLocation(locationHandler.startUpdateLocation(this));
            }
        }
    }

    private void updateLocation(@NonNull Location userLocation) {
        address = locationHandler.getAddress(NoteActivity.this, userLocation.getLatitude(),
                userLocation.getLongitude());
        location.setText(address);
        lat = userLocation.getLatitude();
        lng = userLocation.getLongitude();
    }

    private void updateNote() {
        String noteTitle = title.getText().toString();
        String noteDesc = description.getText().toString();

        ArrayList<String> images = new ArrayList<>();
        for (Uri uri : imageList) {
            images.add(uri.toString());
        }

        noteRoomDatabase.NoteDoa()
                .updateNote(noteId,
                        noteTitle,
                        noteDesc,
                        Converter.toString(images),
                        Converter.toString(recordsList));
        redirectAllNotes();
    }

    private void addNote() {
        String noteTitle = title.getText().toString();
        String noteDesc = description.getText().toString();

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
        Note note = new Note(noteTitle, noteDesc, date.getText().toString(), address,
                lat,
                lng,
                images,
                recordsList);
        noteRoomDatabase.NoteDoa().insertNote(note);
        Toast.makeText(this, "Note Added", Toast.LENGTH_SHORT).show();
        redirectAllNotes();
    }

    private void redirectAllNotes() {
        finish();
    }
}