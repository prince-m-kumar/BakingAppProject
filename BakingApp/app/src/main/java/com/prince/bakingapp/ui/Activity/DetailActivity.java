package com.prince.bakingapp.ui.Activity;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.prince.bakingapp.R;
import com.prince.bakingapp.data.Contract;
import com.squareup.picasso.Picasso;

import com.prince.bakingapp.ui.fragment.FragmentIngredient;
import com.prince.bakingapp.ui.fragment.FragmentStep;

import butterknife.BindView;
import butterknife.ButterKnife;



public class DetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.collapsing_toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @BindView(R.id.view_pager)
    ViewPager viewPager;

    @BindView(R.id.sliding_tabs)
    TabLayout tabLayout;

    @BindView(R.id.recipe_image)
    ImageView recipeImage;

    private Cursor cursor;
    private long recipeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //initialize Butterknife
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Create an adapter that knows which fragment should be shown on each page
        DetailPagerAdapter detailPagerAdapter = new DetailPagerAdapter(getSupportFragmentManager());

        // Set the adapter onto the view pager
        viewPager.setAdapter(detailPagerAdapter);

        // Give the TabLayout the ViewPager
        tabLayout.setupWithViewPager(viewPager);

        //retrieve URI from recipe list
        if (savedInstanceState == null) {
            if (getIntent() != null && getIntent().getData() != null) {
                recipeId = Contract.RecipeEntry.getRecipeId(getIntent().getData());
            }
        }
        getLoaderManager().initLoader(3, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = Contract.RecipeEntry.buildItemUri(recipeId);
        return new CursorLoader(this,
                uri,
                Contract.RecipeEntry.RECIPE_COLUMNS.toArray(new String[]{}),
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            cursor = data;
            collapsingToolbarLayout.setTitle(cursor.getString(Contract.RecipeEntry.POSITION_NAME));
            if (null != getSupportActionBar()) {
                getSupportActionBar().setTitle(cursor.getString(Contract.RecipeEntry.POSITION_NAME));
            }
            if (!cursor.getString(Contract.RecipeEntry.POSITION_IMAGE).equals("")) {
                Picasso.with(this)
                        .load(cursor.getString(Contract.RecipeEntry.POSITION_IMAGE))
                        .placeholder(R.drawable.recipe_image_substitute)
                        .error(R.drawable.recipe_image_substitute)
                        .into(recipeImage);
            } else {
                Picasso.with(this)
                        .load(R.drawable.recipe_image_substitute)
                        .placeholder(R.drawable.recipe_image_substitute)
                        .into(recipeImage);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursor = null;
    }

    private class DetailPagerAdapter extends FragmentPagerAdapter {
        DetailPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Ingredients";
                case 1:
                    return "Steps";
                default:
                    return super.getPageTitle(position);
            }
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return FragmentIngredient.newInstance(recipeId);
                case 1:
                    return FragmentStep.newInstance(recipeId);
                default:
                    return null;
            }
        }
    }
}
