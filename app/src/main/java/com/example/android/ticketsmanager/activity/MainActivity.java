package com.example.android.ticketsmanager.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.example.android.ticketsmanager.R;
import com.example.android.ticketsmanager.adapter.EventsAdapter;
import com.example.android.ticketsmanager.datasource.QueryParams;
import com.example.android.ticketsmanager.viewmodel.EventsViewModel;
import com.example.android.ticketsmanager.viewmodel.ViewModelFactory;

public class MainActivity extends AppCompatActivity
    implements  NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView;
    private EventsAdapter adapter;
    private LinearLayoutManager layoutManager;

    private EventsViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DrawerLayout mainView = findViewById(R.id.drawer_layout);

        NavigationView navigationView = mainView.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        recyclerView = mainView.findViewById(R.id.eventsRecyclerView);

        layoutManager = new LinearLayoutManager(this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new EventsAdapter();
        recyclerView.setAdapter(adapter);

        QueryParams.QueryParamsBuilder params = new QueryParams.QueryParamsBuilder();

        viewModel = ViewModelProviders.of(
                this,
                new ViewModelFactory(this, params.setCountryCode("UK").build())
        ).get(EventsViewModel.class);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int visibleCount = layoutManager.getChildCount();
                int total = layoutManager.getItemCount();
                int firstVisible = layoutManager.findFirstVisibleItemPosition();

                if(firstVisible + visibleCount >= total){
                    viewModel.fetchMore();
                }
            }
        });

        subscribeLiveData();

        DividerItemDecoration itemDecoration = new DividerItemDecoration(
                this, DividerItemDecoration.VERTICAL
        );

        recyclerView.addItemDecoration(itemDecoration);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if(itemId == R.id.nav_prefs){
            Intent prefsIntent = new Intent(this, SettingsActivity.class);
            startActivity(prefsIntent);
        }
        else if (itemId == R.id.nav_search){
            Intent searchIntent = new Intent(this, SearchEventActivity.class);
            startActivityForResult(searchIntent, SearchEventActivity.SEARCH_RESULT);
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == SearchEventActivity.SEARCH_RESULT && resultCode == RESULT_OK){
            adapter.clearList();

            viewModel.unsubscribe(this);
            QueryParams.QueryParamsBuilder params = new QueryParams.QueryParamsBuilder();
            viewModel.setParams(params.setCountryCode("US").build());

            subscribeLiveData();
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void subscribeLiveData(){
        viewModel.getEventsLiveData().observe(
                this,
                eventInfos -> adapter.setEvents(eventInfos)
        );

        viewModel.getNetworkStateLiveData().observe(
                this,
                networkState -> adapter.setNetworkState(networkState)
        );

        viewModel.searchEvents();
    }
}
