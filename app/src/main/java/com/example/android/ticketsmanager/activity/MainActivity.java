package com.example.android.ticketsmanager.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.ticketsmanager.R;
import com.example.android.ticketsmanager.adapter.EventsAdapter;
import com.example.android.ticketsmanager.datasource.QueryParams;
import com.example.android.ticketsmanager.datasource.RequestExtractor;
import com.example.android.ticketsmanager.db.EventInfo;
import com.example.android.ticketsmanager.utils.StringUtils;
import com.example.android.ticketsmanager.viewmodel.EventsViewModel;
import com.example.android.ticketsmanager.viewmodel.ViewModelFactory;

import java.util.List;

public class MainActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener {

    private EventsAdapter adapter;
    private LinearLayoutManager layoutManager;
    private ViewHandler errorHandler;

    private EventsViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DrawerLayout mainView = findViewById(R.id.drawer_layout);

        NavigationView navigationView = mainView.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        RecyclerView recyclerView = mainView.findViewById(R.id.eventsRecyclerView);
        errorHandler = new ViewHandler(
                recyclerView,
                mainView.findViewById(R.id.loadingView),
                mainView.findViewById(R.id.noDataFetchedView));

        layoutManager = new LinearLayoutManager(this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        viewModel = ViewModelProviders.of(
                this,
                new ViewModelFactory(this, createQueryParams())
        ).get(EventsViewModel.class);

        adapter = new EventsAdapter(viewModel::tryAgain, this);

        recyclerView.setAdapter(adapter);
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
            adapter.clearList();
            viewModel.reset();

            QueryParams params = createQueryParams(data);
            saveRequestParams(params);

            viewModel.setParams(params);
            loadInitial();

            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setEventsList(List<EventInfo> eventsList){
        adapter.setEvents(eventsList);
        if(!eventsList.isEmpty()) {
            makeListVisible();
        }
        else {
            errorHandler.showNoDataFetchedView();
        }
    }

    private void loadInitial(){
        makeListInvisible();
        viewModel.fetchFromDb(
                eventsList -> {
                    if(eventsList.isEmpty()) {
                        viewModel.searchEvents();
                    } else {
                        setEventsList(eventsList);
                    }
                },
                this::onError
        );
    }

    private void onError(Throwable error) {
        error.printStackTrace();
        errorHandler.showErrorState();
    }

    private void makeListVisible() {
        errorHandler.showLoading(false);
    }

    private void makeListInvisible() {
        errorHandler.showLoading(true);
    }

    private void subscribeLiveData(){
        viewModel.getNetworkStateLiveData().observe(
                this,
                networkState -> {
                    switch (networkState){
                        case SUCCESS:
                            viewModel.fetchFromDb(this::setEventsList, this::onError);
                            break;
                        case FAILED:
                            adapter.setNetworkState(networkState);
                            errorHandler.showErrorState();
                            break;
                        case RUNNING:
                            adapter.setNetworkState(networkState);
                            break;
                    }
                }
        );
    }

    private QueryParams createQueryParams(Intent data){
        QueryParams.QueryParamsBuilder paramsBuilder = new QueryParams.QueryParamsBuilder();

        RequestExtractor extractor = new RequestExtractor(this, data);

        extractor.setExtra(
                R.string.country_extra,
                data::getStringExtra,
                (countryName) ->
                        paramsBuilder.setCountryCode(
                                StringUtils.getInstance(this).toCounrtyCode(countryName)
                        )
        );

        extractor.setExtra(
                R.string.keyword_extra,
                data::getStringExtra,
                paramsBuilder::setKeyword
        );

        return paramsBuilder.build();
    }

    private QueryParams createQueryParams(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        QueryParams.QueryParamsBuilder builder = new QueryParams.QueryParamsBuilder();

        return builder
                .setCountryCode(preferences.getString(getString(R.string.country_code), "DE"))
                .setKeyword(preferences.getString(getString(R.string.keyword_extra), ""))
                .build();
    }

    private void saveRequestParams(QueryParams params){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(getString(R.string.country_code), params.getCountryCode());
        editor.putString(getString(R.string.keyword_extra), params.getKeywordRaw());
        editor.apply();
    }

    private class ViewHandler {
        private final View itemsView;
        private final View networkStateView;
        private final View noDataFetchedView;
        private final ProgressBar progressBar;
        private final TextView errorMsg;
        private final Button tryAgainButton;

        public ViewHandler(View itemsView, View networkStateView, View noDataFetchedView){
            this.itemsView = itemsView;
            this.networkStateView = networkStateView;
            this.noDataFetchedView = noDataFetchedView;

            progressBar = networkStateView.findViewById(R.id.progressBarView);
            errorMsg = networkStateView.findViewById(R.id.errorMsgTextView);

            tryAgainButton = networkStateView.findViewById(R.id.tryAgainButton);
            tryAgainButton.setOnClickListener(
                    v -> {
                        progressBar.setVisibility(View.VISIBLE);
                        errorMsg.setVisibility(View.INVISIBLE);
                        tryAgainButton.setVisibility(View.INVISIBLE);
                        viewModel.fetchMore();
                    }
            );
        }

        public void showErrorState() {
            if(networkStateView.getVisibility() == View.VISIBLE) {
                progressBar.setVisibility(View.INVISIBLE);
                errorMsg.setVisibility(View.VISIBLE);
                tryAgainButton.setVisibility(View.VISIBLE);
            }
        }

        public void showNoDataFetchedView(){
            networkStateView.setVisibility(View.INVISIBLE);
            noDataFetchedView.setVisibility(View.VISIBLE);
        }

        public void showLoading(boolean isShown) {
            itemsView.setVisibility(isShown ? View.INVISIBLE : View.VISIBLE);
            noDataFetchedView.setVisibility(View.INVISIBLE);
            networkStateView.setVisibility(isShown ? View.VISIBLE : View.INVISIBLE);
        }
    }
}
