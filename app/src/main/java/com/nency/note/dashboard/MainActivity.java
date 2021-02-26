package com.nency.note.dashboard;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
    SwipeRefreshLayout swiperefresh;
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

        // init item touch helper for swipe to delete note
        initItemTouchHelper();

        // init swipe to refresh
        initSwipeToRefresh();

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

    // to get all note onResume when come back from deatil activity
    @Override
    protected void onResume() {
        super.onResume();
        loadNotes();
    }

    // to diplay notte in grid view
    private void initRecyclerView() {
        recyclerView = findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);
        // set grid layout
        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(NUM_COLUMNS, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        //set spacing between grid
        recyclerView.addItemDecoration(new NoteAdapter.GridSpacingItemDecoration(NUM_COLUMNS,
                getResources().getDimensionPixelOffset(R.dimen.list_item_spacing), true));
        // set adapter
        myAdapter = new NoteAdapter(this, filterNotes, this);
        recyclerView.setAdapter(myAdapter);
    }

    // swipe to delete note
    private void initItemTouchHelper() {
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder,
                            RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder,
                            int actionState) {
                        if (viewHolder != null) {
                            getDefaultUIUtil().onSelected(viewHolder.itemView);
                        }
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        if (viewHolder instanceof NoteAdapter.NoteViewHolder) {
                            showRemoveNoteAlert(((NoteAdapter.NoteViewHolder) viewHolder).id,
                                    viewHolder.getAdapterPosition());
                        }
                    }

                    @Override
                    public void onChildDraw(Canvas c,
                            RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder,
                            float dX,
                            float dY,
                            int actionState,
                            boolean isCurrentlyActive) {
                getDefaultUIUtil().onDraw(c, recyclerView, viewHolder.itemView , dX, dY,
                        actionState, isCurrentlyActive);
                    }
                };
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
    }

    // pull to refresh
    private void initSwipeToRefresh() {
        swiperefresh = findViewById(R.id.swiperefresh);
        swiperefresh.setOnRefreshListener(() -> loadNotes());
    }

    // filter bottom sheet dialog open on click listenn
    private void initFilter() {
        findViewById(R.id.appFilter).setOnClickListener(v -> CategoryFilterListDialogFragment.newInstance(
                filterCategory,
                this)
                .show(getSupportFragmentManager(), "dialog"));
    }


    // search notes using search view
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
                // data reload
                recyclerView.getAdapter().notifyDataSetChanged();
                return true;
            }
        });
    }

    // more menu option pop up
    private void initMoreMenu() {
        findViewById(R.id.appMore).setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, v);
            MenuInflater inflater = popup.getMenuInflater();
            popup.setOnMenuItemClickListener(this);
            inflater.inflate(R.menu.menu_main, popup.getMenu());
            popup.show();
        });
    }

    // override method of setOnMenuItemClickListener
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
        swiperefresh.setRefreshing(false);
    }


    // note onclick method
    @Override
    public void onItemClick(int id, int color) {
        Intent i = new Intent(this, NoteActivity.class);
        i.putExtra("NoteId", id);
        i.putExtra("Color", color);
        startActivity(i);
    }

    // category filter
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

    // show alert before remove category
    private void showRemoveNoteAlert(int id, int holderPosition) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Remove Note");
        dialogBuilder.setMessage(
                "Are you sure you want to remove note?");
        dialogBuilder.setPositiveButton("Remove",
                (dialog, whichButton) -> {
                    if (id > -1) {
                        noteRoomDatabase.NoteDoa().deleteNote(id);
                        recyclerView.getAdapter().notifyItemRemoved(holderPosition);
                    }
                });
        dialogBuilder.setNegativeButton("Cancel", (dialog, whichButton) -> {
            recyclerView.getAdapter().notifyItemChanged(holderPosition);
        });
        dialogBuilder.create().show();
    }
}