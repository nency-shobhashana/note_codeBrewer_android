package com.nency.note.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nency.note.R;
import com.nency.note.detail.NoteActivity;
import com.nency.note.interfaces.OnCategorySelectListener;
import com.nency.note.interfaces.OnItemClickListener;
import com.nency.note.map.MapsActivity;
import com.nency.note.room.Category;
import com.nency.note.room.NoteRoomDatabase;
import com.nency.note.room.NoteWithCategory;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnItemClickListener,
        OnCategorySelectListener, PopupMenu.OnMenuItemClickListener {


    private static final int NUM_COLUMNS = 2;

    RecyclerView recyclerView;
    RecyclerView.Adapter myAdapter;
    private boolean sortByDate = false;

    ArrayList<NoteWithCategory> notes = new ArrayList();
    ArrayList<NoteWithCategory> filterNotes = new ArrayList();
    ArrayList<Category> filterCategory = new ArrayList<>();
    ArrayList<Integer> filterCategoriesId = new ArrayList<>();
    private NoteRoomDatabase noteRoomDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init recycle view
        initRecyclerView();

        // init filter
        initFilter();

        // init search view
        initSearchView();

        //init more menu;
        initMoreMenu();

        // add new note button
        FloatingActionButton addNew = findViewById(R.id.addNew);
        addNew.setOnClickListener(view -> {
            Intent i = new Intent(getApplicationContext(), NoteActivity.class);
            startActivity(i);
        });

        // Room db
        noteRoomDatabase = NoteRoomDatabase.getInstance(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotes();
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);
        // set grid layout
        StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(NUM_COLUMNS, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        //set spacing between grid
        recyclerView.addItemDecoration(new NoteAdapter.GridSpacingItemDecoration(2,
                getResources().getDimensionPixelOffset(R.dimen.list_item_spacing), true));
        // set adapter
        myAdapter = new NoteAdapter(this, filterNotes, this);
        recyclerView.setAdapter(myAdapter);
    }

    private void initFilter() {
        findViewById(R.id.appFilter).setOnClickListener(v -> CategoryFilterListDialogFragment.newInstance(
                filterCategory,
                this)
                .show(getSupportFragmentManager(), "dialog"));
    }

    private void initSearchView() {
        SearchView mSearchView = (SearchView) findViewById(R.id.appSearchBar);
        mSearchView.setQueryHint("Search");
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterNotes.clear();
                for (NoteWithCategory noteWithCategory : notes) {
                    if (noteWithCategory.note.getTitle()
                            .toLowerCase()
                            .contains(newText.toLowerCase())
                            || noteWithCategory.note.getDescription()
                            .toLowerCase()
                            .contains(newText.toLowerCase())) {
                        filterNotes.add(noteWithCategory);
                    }
                }
                recyclerView.getAdapter().notifyDataSetChanged();
                return true;
            }
        });
    }

    private void initMoreMenu() {
        findViewById(R.id.appMore).setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, v);
            MenuInflater inflater = popup.getMenuInflater();
            popup.setOnMenuItemClickListener(this);
            inflater.inflate(R.menu.menu_main, popup.getMenu());
            popup.show();
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.appMap) {
            startActivity(new Intent(this, MapsActivity.class));
            return true;
        } else if (item.getItemId() == R.id.sortByTitle) {
            sortByDate = false;
            loadNotes();
            return true;
        } else if (item.getItemId() == R.id.sortByDate) {
            sortByDate = true;
            loadNotes();
            return true;
        }
        return false;
    }

    public void loadNotes() {
        notes.clear();
        notes.addAll(noteRoomDatabase.NoteDoa().getAllFilterNotes(filterCategoriesId,
                String.valueOf(sortByDate)));
        filterNotes.clear();
        filterNotes.addAll(notes);
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onItemClick(int id, int color) {
        Intent i = new Intent(this, NoteActivity.class);
        i.putExtra("NoteId", id);
        i.putExtra("Color", color);
        startActivity(i);
    }

    @Override
    public void onCategorySelected(Category category) {
        if (filterCategory.contains(category)) {
            filterCategory.remove(category);
            filterCategoriesId.remove(Integer.valueOf(category.getId()));
        } else {
            filterCategory.add(category);
            filterCategoriesId.add(category.getId());
        }
        loadNotes();
    }
}