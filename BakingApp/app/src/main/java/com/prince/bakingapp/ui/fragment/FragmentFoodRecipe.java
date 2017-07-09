package com.prince.bakingapp.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.prince.bakingapp.R;
import com.prince.bakingapp.adapter.RecipeAdapter;
import com.prince.bakingapp.data.BakeAppContract;
import com.prince.bakingapp.ui.Activity.BakeFoodDetailActivity;
import com.prince.bakingapp.utils.RecipeIntentService;
import com.prince.bakingapp.utils.Utilities;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by princ on 08-07-2017.
 */
public class FragmentFoodRecipe extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>, RecipeAdapter.ListItemClickListener {
    private RecipeAdapter recipeAdapter;
    private int position = RecyclerView.NO_POSITION;

    @BindView(R.id.recipe_recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.recipe_empty)
    View emptyView;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    //SwipeRefreshLayout: declare broadcast receiver
    private boolean isRefreshing = false;

    private BroadcastReceiver refreshingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (RecipeIntentService.BROADCAST_ACTION_STATE_CHANGE.equals(intent.getAction())) {
                isRefreshing = intent.getBooleanExtra(RecipeIntentService.EXTRA_REFRESHING, false);
                int responseStatus = intent.getIntExtra(RecipeIntentService.EXTRA_RESPONSE_STATUS, Utilities.ApiResponseStatus.NONE);
                updateRefreshingUI(responseStatus);
            }
        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(0, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe, container, false);

        //initialize ButterKnife library
        ButterKnife.bind(this, rootView);

        recipeAdapter = new RecipeAdapter(null, getActivity(), this);

        recyclerView.setAdapter(recipeAdapter);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(getResources().getInteger(R.integer.main_screen_column_count), StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setHasFixedSize(true);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getActivity().startService(new Intent(getActivity(), RecipeIntentService.class));
            }
        });

        if (savedInstanceState == null) {
            getActivity().startService(new Intent(getActivity(), RecipeIntentService.class));
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        //SwipeRefreshLayout: register broadcast receiver
        getActivity().registerReceiver(refreshingReceiver,
                new IntentFilter(RecipeIntentService.BROADCAST_ACTION_STATE_CHANGE));
    }

    @Override
    public void onPause() {
        super.onPause();

        //SwipeRefreshLayout: unregister broadcast receiver
        getActivity().unregisterReceiver(refreshingReceiver);
    }

    private void updateRefreshingUI(int responseStatus) {
        swipeRefreshLayout.setRefreshing(isRefreshing);

        switch (responseStatus) {
            case Utilities.ApiResponseStatus.SUCCESS:
                emptyView.setVisibility(View.GONE);
                break;
            case Utilities.ApiResponseStatus.ERROR:
                ((TextView) emptyView).setText(R.string.label_empty_no_data);
                emptyView.setVisibility(View.VISIBLE);
                break;
            default:
                if (isRefreshing) {
                    ((TextView) emptyView).setText(R.string.label_empty_refreshing);
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    emptyView.setVisibility(View.GONE);
                }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = BakeAppContract.RecipeEntry.buildDirUri();
        return new CursorLoader(getActivity(),
                uri,
                BakeAppContract.RecipeEntry.RECIPE_COLUMNS.toArray(new String[]{}),
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        recipeAdapter.swapCursor(data);
        if (position == RecyclerView.NO_POSITION) position = 0;
        recyclerView.smoothScrollToPosition(position);

        if ((data != null) && data.moveToFirst()) {
            if (Utilities.isFavoriteEmpty(getContext())) {
                Utilities.setAsFavoriteRecipe(
                        getContext(),
                        data.getLong(BakeAppContract.RecipeEntry.POSITION_ID),
                        data.getString(BakeAppContract.RecipeEntry.POSITION_NAME),
                        data.getString(BakeAppContract.RecipeEntry.POSITION_IMAGE)
                );
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        recipeAdapter.swapCursor(null);
    }

    @Override
    public void onListItemClick(int clickedItemIndex, RecipeAdapter.RecipeViewHolder recipeViewHolder) {
        Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                getActivity(),
                recipeViewHolder.recipeImage,
                ViewCompat.getTransitionName(recipeViewHolder.recipeImage)).toBundle();

        Intent intent = new Intent(getActivity(), BakeFoodDetailActivity.class);
        intent.setData(BakeAppContract.RecipeEntry.buildItemUri(recipeAdapter.getItemId(clickedItemIndex)));
        startActivity(intent, bundle);
    }
}
