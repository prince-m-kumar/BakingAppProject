package com.prince.bakingapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.prince.bakingapp.R;
import com.prince.bakingapp.util.RecipeSyncUtilities;

import timber.log.Timber;

/**
 * Created by princ on 05-07-2017.
 */
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Timber.plant(new Timber.DebugTree());
        setContentView(R.layout.activity_main);

        RecipeSyncUtilities.scheduleSyncTask(this);
    }
}
