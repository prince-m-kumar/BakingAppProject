package com.prince.bakingapp.ui.Activity;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.prince.bakingapp.R;
import com.prince.bakingapp.data.Contract;
import com.prince.bakingapp.ui.fragment.FragmentVideo;

import butterknife.BindView;
import butterknife.ButterKnife;



public class InstructionActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {
    @BindView(R.id.view_pager)
    ViewPager viewPager;

    private Cursor cursor;
    private long recipeId;
    private long stepId;
    private InstructionPagerAdapter instructionPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }

        //initialize Butterknife
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            if (getIntent() != null && getIntent().getData() != null) {
                recipeId = Contract.StepEntry.getRecipeId(getIntent().getData());
                stepId = Contract.StepEntry.getStepId(getIntent().getData());
            }
        }
        getLoaderManager().initLoader(4, null, this);

        // Create an adapter that knows which fragment should be shown on each page
        instructionPagerAdapter = new InstructionPagerAdapter(getSupportFragmentManager());

        // Set the adapter onto the view pager
        viewPager.setAdapter(instructionPagerAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (cursor != null) {
                    cursor.moveToPosition(position);
                }
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = Contract.StepEntry.buildDirUri(recipeId);
        return new CursorLoader(this,
                uri,
                Contract.StepEntry.STEP_COLUMNS.toArray(new String[]{}),
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            cursor = data;
            instructionPagerAdapter.notifyDataSetChanged();
            viewPager.setCurrentItem((int) stepId);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursor = null;
    }

    private class InstructionPagerAdapter extends FragmentStatePagerAdapter {
        InstructionPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            cursor.moveToPosition(position);
            long recipeId = cursor.getLong(Contract.StepEntry.POSITION_RECIPE_ID);
            long stepId = cursor.getLong(Contract.StepEntry.POSITION_ID);
            return FragmentVideo.newInstance(recipeId, stepId);
        }

        @Override
        public int getCount() {
            return (cursor != null) ? cursor.getCount() : 0;
        }
    }
}
