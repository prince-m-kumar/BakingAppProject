package com.prince.bakingapp.ui.fragment;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prince.bakingapp.R;
import com.prince.bakingapp.adapter.BakeStepAdapter;
import com.prince.bakingapp.data.BakeAppContract;
import com.prince.bakingapp.ui.Activity.BakingFoodInstructionActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by princ on 08-07-2017.
 */

public class FragmentBakingStep extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>, BakeStepAdapter.ListItemClickListener {
    private static final String ARG_RECIPE_ID = "recipeId";
    private static final String TAG_FRAGMENT_VIDEO = "tag_fragment_video";
    private long recipeId;
    private BakeStepAdapter bakeStepAdapter;
    private int position = RecyclerView.NO_POSITION;

    @BindView(R.id.step_recycler_view)
    RecyclerView recyclerView;

    public FragmentBakingStep() {
        // Required empty public constructor
    }

    public static FragmentBakingStep newInstance(long recipeId) {
        Bundle arguments = new Bundle();
        arguments.putLong(ARG_RECIPE_ID, recipeId);
        FragmentBakingStep fragment = new FragmentBakingStep();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //retrieve recipe id from detail activity
        if (getArguments().containsKey(ARG_RECIPE_ID)) {
            recipeId = getArguments().getLong(ARG_RECIPE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_step, container, false);

        //initialize ButterKnife library
        ButterKnife.bind(this, rootView);

        bakeStepAdapter = new BakeStepAdapter(null, getActivity(), this);

        recyclerView.setAdapter(bakeStepAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL));

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(2, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = BakeAppContract.StepEntry.buildDirUri(recipeId);
        return new CursorLoader(getActivity(),
                uri,
                BakeAppContract.StepEntry.STEP_COLUMNS.toArray(new String[]{}),
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        bakeStepAdapter.swapCursor(data);
        if (position == RecyclerView.NO_POSITION) position = 0;
        recyclerView.smoothScrollToPosition(position);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        bakeStepAdapter.swapCursor(null);
    }

    @Override
    public void onListItemClick(int clickedItemIndex, BakeStepAdapter.StepViewHolder stepViewHolder) {
        if (getActivity().findViewById(R.id.video_container) != null) {
            Fragment fragmentVideo = FragmentBakingVideo.newInstance(recipeId, clickedItemIndex);
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.video_container, fragmentVideo, TAG_FRAGMENT_VIDEO)
                    .commit();
        } else {
            Intent intent = new Intent(getActivity(), BakingFoodInstructionActivity.class);
            intent.setData(BakeAppContract.StepEntry.buildItemUri(recipeId, clickedItemIndex));
            startActivity(intent);
        }
    }
}
