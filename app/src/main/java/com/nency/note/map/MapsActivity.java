package com.nency.note.map;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nency.note.R;
import com.nency.note.detail.LocationHandler;
import com.nency.note.detail.NoteActivity;
import com.nency.note.room.Note;
import com.nency.note.room.NoteRoomDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ArrayList<Note> notes = new ArrayList();
    HashMap<Marker, Note> mapMarkerNote= new HashMap();
    private NoteRoomDatabase noteRoomDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Room db
        noteRoomDatabase = NoteRoomDatabase.getInstance(this);
        loadAllNotes();

//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private void loadAllNotes() {
        notes.clear();
        notes.addAll(noteRoomDatabase.NoteDoa().getAllNotes());
        displayMarker();
        moveToUserLocation();
    }

    private void displayMarker() {
        mMap.clear();
        mapMarkerNote.clear();
        for (Note note : notes) {
            LatLng noteLocation = new LatLng(note.getLat(), note.getLng());
            Marker marker =
                    mMap.addMarker(new MarkerOptions().position(noteLocation).title(note.getTitle()));
            mapMarkerNote.put(marker, note);
        }

        mMap.setOnMarkerClickListener(marker -> {
            Note note = mapMarkerNote.get(marker);
            onMarkerClicked(note);
            return true;
        });
    }

    private void moveToUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        Location myLocation =
                (new LocationHandler(this, null)).locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(myLocation != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(),
                    myLocation.getLongitude()), 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(myLocation.getLatitude(),
                            myLocation.getLongitude()))      // Sets the center of the map to location user
                    .zoom(17)                   // Sets the zoom
                    .bearing(0)                // Sets the orientation of the camera to east
                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    private void onMarkerClicked(Note note){
        Intent i = new Intent(this, NoteActivity.class);
        i.putExtra("NoteId", note.getId());
        startActivity(i);
    }
}