package com.nency.note.detail;

import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.nency.note.R;
import com.nency.note.interfaces.OnCategorySelectListener;
import com.nency.note.room.Category;
import com.nency.note.room.Converter;
import com.nency.note.room.Note;
import com.nency.note.room.NoteRoomDatabase;
import com.nency.note.room.NoteWithCategory;
import com.nency.note.utils.ColorUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class NoteActivity extends AppCompatActivity implements OnCategorySelectListener {

    private static final int LOCATION_REQUEST = 101;

    int noteId = -1;

    EditText title, description;
    TextView txtCategory, date, location;
    ImageView iconCategory, iconImage, iconAudio, saveNote, backBtn, deleteNote;

    ArrayList<Uri> imageList = new ArrayList<>();
    ArrayList<String> recordsList = new ArrayList<>();

    private LocationHandler locationHandler;
    private double lat = 0, lng = 0;
    private String address = "";

    private NoteRoomDatabase noteRoomDatabase;

    private Category category;

    private int color = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        noteId = getIntent().getIntExtra("NoteId", -1);

        // set color of background
        color = getIntent().getIntExtra("Color", ColorUtils.getColor(this,
                new Random().nextInt(6)));
        // Room db
        noteRoomDatabase = noteRoomDatabase.getInstance(this);

        initViews();
        initListener();

        // getting the current time
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CANADA);
        String createdDate = sdf.format(cal.getTime());
        date.setText(createdDate);

        if (noteId >= 0) {
            NoteWithCategory noteWithCategory = noteRoomDatabase.NoteDoa().getNote(noteId);
            Note note = noteWithCategory.note;
            category = noteWithCategory.category;

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
            deleteNote.setVisibility(View.VISIBLE);
        } else {
            initLocation();
            createOrSetUnCategorised();
            deleteNote.setVisibility(View.GONE);
        }
        txtCategory.setText("Category : " + category.getName());
    }

    private void createOrSetUnCategorised() {
        category = noteRoomDatabase.CategoryDao().getUnCategorised();
        if (category == null) {
            category = new Category("UnCategorised");
            noteRoomDatabase.CategoryDao().insertCategory(category);
            category = noteRoomDatabase.CategoryDao().getUnCategorised();
        }
    }

    private void initViews() {
        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        txtCategory = findViewById(R.id.category);
        date = findViewById(R.id.date);
        location = findViewById(R.id.location);
        iconCategory = findViewById(R.id.iconCategory);
        iconImage = findViewById(R.id.iconImage);
        iconAudio = findViewById(R.id.iconAudio);
        saveNote = findViewById(R.id.saveNote);
        deleteNote = findViewById(R.id.deleteNote);
        backBtn = findViewById(R.id.backBtn);

        View detail_layout = findViewById(R.id.detail_layout);
        detail_layout.setBackgroundColor(color);

    }

    private void initListener() {
        iconCategory.setOnClickListener(v -> CategoryListDialogFragment.newInstance(category.getId(), NoteActivity.this)
                .show(getSupportFragmentManager(), "dialog"));
        iconImage.setOnClickListener(v -> ImageListDialogFragment.newInstance(imageList)
                .show(getSupportFragmentManager(), "dialog"));
        iconAudio.setOnClickListener(v -> AudioRecordFragment.newInstance(recordsList)
                .show(getSupportFragmentManager(), "dialog"));
        saveNote.setOnClickListener(v -> {
            if (noteId < 0) {
                addNote();
            } else {
                updateNote();
            }
        });
        deleteNote.setOnClickListener(v -> showRemoveNoteAlert(noteId));
        backBtn.setOnClickListener(v -> {
            finish();
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
            Location loc = locationHandler.startUpdateLocation(this);
            if (loc != null) {
                updateLocation(loc);
            }
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
                Location loc = locationHandler.startUpdateLocation(this);
                if (loc != null) {
                    updateLocation(loc);
                }
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

    @Override
    public void onCategorySelected(Category category) {
        this.category = category;
        txtCategory.setText("Category : " + category.getName());
    }

    private void updateNote() {
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

        noteRoomDatabase.NoteDoa()
                .updateNote(noteId,
                        noteTitle,
                        noteDesc,
                        Converter.toString(images),
                        Converter.toString(recordsList),
                        category.getId());
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
                recordsList, category.getId());
        noteRoomDatabase.NoteDoa().insertNote(note);
        Toast.makeText(this, "Note Added", Toast.LENGTH_SHORT).show();
        redirectAllNotes();
    }

    // show alert before remove category
    private void showRemoveNoteAlert(int id) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Remove Note");
        dialogBuilder.setMessage(
                "Are you sure you want to remove note?");
        dialogBuilder.setPositiveButton("Remove",
                (dialog, whichButton) -> {
                    if (id > -1) {
                        noteRoomDatabase.NoteDoa().deleteNote(id);
                    }
                    finish();
                });
        dialogBuilder.setNegativeButton("Cancel", (dialog, whichButton) -> {
        });
        dialogBuilder.create().show();
    }

    private void redirectAllNotes() {
        finish();
    }
}