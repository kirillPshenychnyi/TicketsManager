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
import com.example.android.ticketsmanager.db.EventInfo;
import com.example.android.ticketsmanager.utils.NetworkState;
import com.example.android.ticketsmanager.utils.StringUtils;
import com.example.android.ticketsmanager.viewmodel.EventsViewModel;
import com.example.android.ticketsmanager.viewmodel.ViewModelFactory;

import java.util.List;

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
        loadInitial();

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
            String countryExtra = getString(R.string.countryExtra);
            if(data.hasExtra(countryExtra)) {
                String country = data.getStringExtra(countryExtra);

                adapter.clearList();
                viewModel.unsubscribe(this);

                QueryParams.QueryParamsBuilder params = new QueryParams.QueryParamsBuilder();
                params.setCountryCode(StringUtils.toCounrtyCode(this, country));

                viewModel.setParams(params.build());
                subscribeLiveData();
                loadInitial();

                return;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setEventsList(List<EventInfo> eventsList){
        adapter.setEvents(eventsList);
    }

    private void loadInitial(){
        viewModel.fetchFromDb(
                eventsList -> {
                    if(eventsList.isEmpty()){
                        viewModel.searchEvents();
                    }else {
                        setEventsList(eventsList);
                    }
                }
        );
    }

    private void subscribeLiveData(){
        viewModel.getNetworkStateLiveData().observe(
                this,
                networkState -> {
                    adapter.setNetworkState(networkState);

                    if(networkState == NetworkState.LOADED){
                        viewModel.fetchFromDb(this::setEventsList);
                    }
                }
        );
    }
}
