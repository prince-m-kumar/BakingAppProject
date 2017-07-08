package com.prince.bakingapp.ui.Activity;

import android.app.LoaderManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.prince.bakingapp.R;
import com.prince.bakingapp.data.BakeAppContract;
import com.prince.bakingapp.ui.fragment.FragmentBakingVideo;
import com.prince.bakingapp.ui.fragment.FragmentRecipeIngredient;
import com.prince.bakingapp.ui.fragment.FragmentBakingStep;
import com.prince.bakingapp.util.IngredientWidgetProvider;
import com.squareup.picasso.Picasso;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.prince.bakingapp.util.Utilities.setAsFavoriteRecipe;

/**
 * Created by princ on 06-07-2017.
 */
public class BakeFoodDetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG_FRAGMENT_VIDEO = "tag_fragment_video";

    @BindView(R.id.detail_toolbar)
    Toolbar toolbar;

    @BindView(R.id.detail_view_pager)
    ViewPager viewPager;

    @BindView(R.id.sliding_tabs)
    TabLayout tabLayout;

    @BindView(R.id.detail_recipe_image)
    ImageView recipeImage;

    @BindView(R.id.fab)
    FloatingActionButton fabFavorite;

    private Cursor cursor;
    private long recipeId;
    private int numOfServings;

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

        //retrieve URI from recipe list
        if (getIntent() != null && getIntent().getData() != null) {
            recipeId = BakeAppContract.RecipeEntry.getRecipeId(getIntent().getData());
        }

        if (findViewById(R.id.video_container) != null) {
            long stepId = 0;
            Fragment fragmentVideo = FragmentBakingVideo.newInstance(recipeId, stepId);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.video_container, fragmentVideo, TAG_FRAGMENT_VIDEO)
                    .commit();
        }

        // Give the TabLayout the ViewPager
        tabLayout.setupWithViewPager(viewPager);

        getLoaderManager().initLoader(3, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = BakeAppContract.RecipeEntry.buildItemUri(recipeId);
        return new CursorLoader(this,
                uri,
                BakeAppContract.RecipeEntry.RECIPE_COLUMNS.toArray(new String[]{}),
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            cursor = data;
            numOfServings = cursor.getInt(BakeAppContract.RecipeEntry.POSITION_SERVINGS);
            recipeImage.setContentDescription(cursor.getString(BakeAppContract.RecipeEntry.POSITION_NAME));
            if (findViewById(R.id.collapsing_toolbar_layout) != null) {
                CollapsingToolbarLayout collapsingToolbarLayout =
                        (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
                collapsingToolbarLayout.setTitle(cursor.getString(BakeAppContract.RecipeEntry.POSITION_NAME));
            }

            if (null != getSupportActionBar()) {
                getSupportActionBar().setTitle(cursor.getString(BakeAppContract.RecipeEntry.POSITION_NAME));
            }

            if (!cursor.getString(BakeAppContract.RecipeEntry.POSITION_IMAGE).equals("")) {
                Picasso.with(this)
                        .load(cursor.getString(BakeAppContract.RecipeEntry.POSITION_IMAGE))
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .into(recipeImage);
            } else {
                Picasso.with(this)
                        .load(R.drawable.placeholder)
                        .placeholder(R.drawable.placeholder)
                        .into(recipeImage);
            }
            // Create an adapter that knows which fragment should be shown on each page
            DetailPagerAdapter detailPagerAdapter = new DetailPagerAdapter(getSupportFragmentManager());

            // Set the adapter onto the view pager
            viewPager.setAdapter(detailPagerAdapter);

            fabFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setAsFavoriteRecipe(getApplicationContext(),
                            recipeId,
                            cursor.getString(BakeAppContract.RecipeEntry.POSITION_NAME),
                            cursor.getString(BakeAppContract.RecipeEntry.POSITION_IMAGE)
                    );

                    //update widget immediately to show updated favorite recipe
                    Intent intent = new Intent(v.getContext(), IngredientWidgetProvider.class);
                    intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                    int[] ids = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), IngredientWidgetProvider.class));
                    // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
                    // since it seems the onUpdate() is only fired on that:
                    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                    sendBroadcast(intent);

                    //notify user
                    Toast.makeText(v.getContext(), R.string.notification_favorite_added, Toast.LENGTH_SHORT).show();
                }
            });
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
                    return getString(R.string.label_ingredients);
                case 1:
                    return getString(R.string.label_steps);
                default:
                    return super.getPageTitle(position);
            }
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return FragmentRecipeIngredient.newInstance(recipeId, numOfServings);
                case 1:
                    return FragmentBakingStep.newInstance(recipeId);
                default:
                    return null;
            }
        }
    }
}
