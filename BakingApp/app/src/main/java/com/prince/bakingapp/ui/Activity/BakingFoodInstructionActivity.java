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
import android.widget.Toast;

import com.prince.bakingapp.R;
import com.prince.bakingapp.data.BakeAppContract;
import com.prince.bakingapp.ui.fragment.FragmentBakingVideo;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by princ on 07-07-2017.
 */

public class BakingFoodInstructionActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {
    @BindView(R.id.instruction_view_pager)
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
                recipeId = BakeAppContract.StepEntry.getRecipeId(getIntent().getData());
                stepId = BakeAppContract.StepEntry.getStepId(getIntent().getData());
            }
        }
        getLoaderManager().initLoader(4, null, this);

        // Create an adapter that knows which fragment should be shown on each page
        instructionPagerAdapter = new InstructionPagerAdapter(getSupportFragmentManager());

        // Set the adapter onto the view pager
        viewPager.setAdapter(instructionPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (cursor != null) {
                    cursor.moveToPosition(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        Toast.makeText(this, R.string.notification_instruction_starting, Toast.LENGTH_LONG).show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = BakeAppContract.StepEntry.buildDirUri(recipeId);
        return new CursorLoader(this,
                uri,
                BakeAppContract.StepEntry.STEP_COLUMNS.toArray(new String[]{}),
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
            long recipeId = cursor.getLong(BakeAppContract.StepEntry.POSITION_RECIPE_ID);
            long stepId = cursor.getLong(BakeAppContract.StepEntry.POSITION_ID);
            return FragmentBakingVideo.newInstance(recipeId, stepId);
        }

        @Override
        public int getCount() {
            return (cursor != null) ? cursor.getCount() : 0;
        }
    }
}
