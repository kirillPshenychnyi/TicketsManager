package com.example.android.ticketsmanager.activity;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.android.ticketsmanager.R;

public class SearchEventActivity extends AppCompatActivity {

    public static final int SEARCH_RESULT = 400;

    private Spinner сountriesSpinner;
    private EditText keywordEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_event);

        сountriesSpinner = findViewById(R.id.countriesSpinner);
        keywordEditor = findViewById(R.id.keywordEditText);

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(
                this, R.array.countries, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        сountriesSpinner.setAdapter(arrayAdapter);

        ActionBar bar = getSupportActionBar();

        if(bar != null){
            bar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }

    public void onSearchButtonClicked(View view) {
        String country = сountriesSpinner.getSelectedItem().toString();

        Intent result = new Intent();

        result.putExtra(getString(R.string.country_extra), country);
        result.putExtra(getString(R.string.keyword_extra), keywordEditor.getText().toString());

        setResult(RESULT_OK, result);

        finish();
    }
}