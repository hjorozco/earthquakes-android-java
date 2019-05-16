package com.weebly.hectorjorozco.earthquakes.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;

import com.weebly.hectorjorozco.earthquakes.R;
import com.weebly.hectorjorozco.earthquakes.adapters.EarthquakesListAdapter;
import com.weebly.hectorjorozco.earthquakes.models.Earthquake;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EarthquakesListAdapter mEarthquakesListAdapter;
    private RecyclerView mRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);


        List<Earthquake> earthquakes = new ArrayList<>();

        for (int i = 0; i < 500; i++) {
            earthquakes.add(new Earthquake("Earthquake " + i));
        }

        mEarthquakesListAdapter = new EarthquakesListAdapter(this);
        mEarthquakesListAdapter.setEarthquakesListData(earthquakes);

        setupRecyclerView();
    }


    private void setupRecyclerView(){
        mRecyclerView = findViewById(R.id.activity_main_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mEarthquakesListAdapter);
        mRecyclerView.setNestedScrollingEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_activity_main_action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
