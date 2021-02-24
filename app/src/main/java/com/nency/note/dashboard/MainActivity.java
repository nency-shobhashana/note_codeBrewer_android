package com.nency.note.dashboard;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nency.note.detail.CategoryListDialogFragment;
import com.nency.note.interfaces.OnCategorySelectListener;
import com.nency.note.interfaces.OnItemClickListener;
import com.nency.note.R;
import com.nency.note.detail.NoteActivity;
import com.nency.note.room.Category;
import com.nency.note.room.Note;
import com.nency.note.room.NoteRoomDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnItemClickListener,
        OnCategorySelectListener {


    private static final int NUM_COLUMNS = 2;

    RecyclerView recyclerView;
    RecyclerView.Adapter myAdapter;
    RecyclerView.LayoutManager layoutManager;

    ArrayList<Note> notes = new ArrayList();
    ArrayList<Note> filterNotes = new ArrayList();
    private NoteRoomDatabase noteRoomDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

//        Database.initDatabase();
//        notes = Database.getNotes();

        // set adapter
        myAdapter = new NoteAdapter(this, filterNotes, this);
        initRecyclerView();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add new note button
        FloatingActionButton addNew = findViewById(R.id.addNew);
        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), NoteActivity.class);
                startActivity(i);
            }
        });

        // Room db
        noteRoomDatabase = NoteRoomDatabase.getInstance(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotes();
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.list);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(NUM_COLUMNS, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setAdapter(myAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem mSearch = menu.findItem(R.id.appSearchBar);
        SearchView mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setQueryHint("Search");
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                filterNotes.clear();
                for (Note note : notes) {
                    if(note.getTitle().contains(newText)){
                        filterNotes.add(note);
                    }
                }
                recyclerView.getAdapter().notifyDataSetChanged();
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.appFilter){
            CategoryFilterListDialogFragment.newInstance(this)
                    .show(getSupportFragmentManager(), "dialog");
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void loadNotes() {
        notes.clear();
        notes.addAll(noteRoomDatabase.NoteDoa().getAllNotes());
        filterNotes.clear();
        filterNotes.addAll(notes);
    }

    @Override
    public void onItemClick(int id) {
        Intent i = new Intent(this, NoteActivity.class);
        i.putExtra("NoteId", id);
        startActivity(i);
    }

    @Override
    public void onCategorySelected(Category category) {

    }
}