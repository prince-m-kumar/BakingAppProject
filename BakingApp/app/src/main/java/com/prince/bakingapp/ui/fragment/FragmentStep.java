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
import com.prince.bakingapp.adapter.StepAdapter;
import com.prince.bakingapp.data.Contract;
import com.prince.bakingapp.ui.Activity.InstructionActivity;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentStep extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>, StepAdapter.ListItemClickListener {
    public static final String ARG_RECIPE_ID = "recipeId";
    private long recipeId;
    private StepAdapter stepAdapter;
    private int position = RecyclerView.NO_POSITION;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    public FragmentStep() {
        // Required empty public constructor
    }

    public static FragmentStep newInstance(long recipeId) {
        Bundle arguments = new Bundle();
        arguments.putLong(ARG_RECIPE_ID, recipeId);
        FragmentStep fragment = new FragmentStep();
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

        stepAdapter = new StepAdapter(null, getActivity(), this);

        recyclerView.setAdapter(stepAdapter);
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
        Uri uri = Contract.StepEntry.buildDirUri(recipeId);
        return new CursorLoader(getActivity(),
                uri,
                Contract.StepEntry.STEP_COLUMNS.toArray(new String[]{}),
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        stepAdapter.swapCursor(data);
        if (position == RecyclerView.NO_POSITION) position = 0;
        recyclerView.smoothScrollToPosition(position);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        stepAdapter.swapCursor(null);
    }

    @Override
    public void onListItemClick(int clickedItemIndex, StepAdapter.StepViewHolder stepViewHolder) {
        Intent intent = new Intent(getActivity(), InstructionActivity.class);
        intent.setData(Contract.StepEntry.buildItemUri(recipeId, clickedItemIndex));
        startActivity(intent);
    }
}
