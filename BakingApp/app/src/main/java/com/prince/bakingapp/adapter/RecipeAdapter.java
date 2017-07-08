package com.prince.bakingapp.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.prince.bakingapp.R;
import com.prince.bakingapp.data.BakeAppContract;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by princ on 08-07-2017.
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private Cursor cursor;
    private Context context;

    final private ListItemClickListener onClickListener;

    public RecipeAdapter(Cursor cursor, Context context, ListItemClickListener listener) {
        this.cursor = cursor;
        this.context = context;
        this.onClickListener = listener;
    }

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex, RecipeViewHolder recipeViewHolder);
    }

    @Override
    public RecipeAdapter.RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(context).inflate(R.layout.list_item_recipe, parent, false);
        return new RecipeViewHolder(item);
    }

    @Override
    public void onBindViewHolder(RecipeAdapter.RecipeViewHolder holder, int position) {
        cursor.moveToPosition(position);
        if (null != cursor) {
            holder.recipeName.setText(cursor.getString(BakeAppContract.RecipeEntry.POSITION_NAME));
            holder.recipeImage.setContentDescription(cursor.getString(BakeAppContract.RecipeEntry.POSITION_IMAGE));
            if (!cursor.getString(BakeAppContract.RecipeEntry.POSITION_IMAGE).equals("")) {
                Picasso.with(context)
                        .load(cursor.getString(BakeAppContract.RecipeEntry.POSITION_IMAGE))
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .into(holder.recipeImage);
            } else {
                Picasso.with(context)
                        .load(R.drawable.placeholder)
                        .placeholder(R.drawable.placeholder)
                        .into(holder.recipeImage);
            }
        }
    }

    @Override
    public long getItemId(int position) {
        cursor.moveToPosition(position);
        return cursor.getLong(BakeAppContract.RecipeEntry.POSITION_ID);
    }

    @Override
    public int getItemCount() {
        if (null == cursor) return 0;
        return cursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        cursor = newCursor;
        notifyDataSetChanged();
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.recipe_image)
        public
        ImageView recipeImage;

        @BindView(R.id.recipe_name)
        TextView recipeName;

        RecipeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onClickListener.onListItemClick(getAdapterPosition(), this);
        }
    }
}
