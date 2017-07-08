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
import com.squareup.picasso.Picasso;



import butterknife.BindView;
import butterknife.ButterKnife;

import static com.prince.bakingapp.data.Contract.RecipeEntry;

/**
 *
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private Cursor cursor;
    private Context context;

    final private ListItemClickListener onClickListener;

    /**
     * @param cursor
     * @param context
     * @param listener
     */
    public RecipeAdapter(Cursor cursor, Context context, ListItemClickListener listener) {
        this.cursor = cursor;
        this.context = context;
        this.onClickListener = listener;
    }

    /**
     *
     */
    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex, RecipeViewHolder recipeViewHolder);
    }

    /**
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public RecipeAdapter.RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(context).inflate(R.layout.list_item_recipe, parent, false);
        return new RecipeViewHolder(item);
    }

    /**
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(RecipeAdapter.RecipeViewHolder holder, int position) {
        cursor.moveToPosition(position);
        if (null != cursor) {
            holder.recipeName.setText(cursor.getString(RecipeEntry.POSITION_NAME));
            if (!cursor.getString(RecipeEntry.POSITION_IMAGE).equals("")) {
                Picasso.with(context)
                        .load(cursor.getString(RecipeEntry.POSITION_IMAGE))
                        .placeholder(R.drawable.recipe_image_substitute)
                        .error(R.drawable.recipe_image_substitute)
                        .into(holder.recipeImage);
            } else {
                Picasso.with(context)
                        .load(R.drawable.recipe_image_substitute)
                        .placeholder(R.drawable.recipe_image_substitute)
                        .into(holder.recipeImage);
            }
        }
    }

    /**
     * @param position
     * @return
     */
    @Override
    public long getItemId(int position) {
        cursor.moveToPosition(position);
        return cursor.getLong(RecipeEntry.POSITION_ID);
    }

    /**
     * @return
     */
    @Override
    public int getItemCount() {
        if (null == cursor) return 0;
        return cursor.getCount();
    }

    /**
     * @param newCursor
     */
    public void swapCursor(Cursor newCursor) {
        cursor = newCursor;
        notifyDataSetChanged();
    }

    /**
     *
     */
    public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.recipe_image)
        public
        ImageView recipeImage;

        @BindView(R.id.recipe_name)
        TextView recipeName;

        /**
         * @param itemView
         */
        RecipeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        /**
         * @param v
         */
        @Override
        public void onClick(View v) {
            onClickListener.onListItemClick(getAdapterPosition(), this);
        }
    }
}
