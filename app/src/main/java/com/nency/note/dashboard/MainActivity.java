package com.nency.note.dashboard;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.nency.note.R;
import com.nency.note.model.Category;
import com.nency.note.model.Note;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    RecyclerView.Adapter myAdapter;
    RecyclerView.LayoutManager layoutManager;

    List<Note> notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        notes = new ArrayList<Note>();
        notes.add(new Note("Sample Title 1","Sample description 1","2nd June, 2020","2nd June 2020",null, null, "Canada", new Category(1,"Personal", 2)));
        notes.add(new Note("Sample Title 2","Sample description 2","12nd July, 2020","12nd July 2020",null, null, "Canada", new Category(2,"Shopping", 3)));
        notes.add(new Note("Sample Title 3","Sample description 3","25nd Aug, 2020","25nd Aug 2020",null, null, "Canada", new Category(3,"Shopping", 3)));
        notes.add(new Note("Sample Title 4","Sample description 4","20nd Jan, 2020","20nd Jan 2020",null, null, "Canada", new Category(4,"Personal", 2)));
        notes.add(new Note("Sample Title 5","Sample description 5","2nd June, 2020","2nd June 2020",null, null, "Canada", new Category(1,"Personal", 2)));
        notes.add(new Note("Sample Title 6","Sample description 6","12nd July, 2020","12nd July 2020",null, null, "Canada", new Category(2,"Shopping", 3)));
        notes.add(new Note("Sample Title 7","Sample description 7","25nd Aug, 2020","25nd Aug 2020",null, null, "Canada", new Category(3,"Shopping", 3)));
        notes.add(new Note("Sample Title 8","Sample description 8","20nd Jan, 2020","20nd Jan 2020",null, null, "Canada", new Category(4,"Personal", 2)));

        myAdapter = new NoteAdapter(this, notes);

        recyclerView.setAdapter(myAdapter);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fabNewNote);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}