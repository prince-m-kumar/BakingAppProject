package com.prince.bakingapp.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.prince.bakingapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.prince.bakingapp.data.Contract.IngredientEntry;

/**
 * @author Prince
 * This class responsible for Ingredient which used in show in FragmentIngredient fragment
 *
 */
public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {
    private Cursor cursor;
    private Context context;

    final private ListItemClickListener onClickListener;

    /**
     * @param cursor : database cursor
     * @param context : current context
     * @param listener : set listener in recycle view
     */
    public IngredientAdapter(Cursor cursor, Context context, ListItemClickListener listener) {
        this.cursor = cursor;
        this.context = context;
        this.onClickListener = listener;
    }

    /**
     *
     */
    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex, IngredientViewHolder ingredientViewHolder);
    }

    /**
     * @param parent : The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType : The view type of the new View.
     * @return A new ViewHolder that holds a View for {@link R.layout (list_item_ingredient)
     * } list_item_ingredient.
     */
    @Override
    public IngredientAdapter.IngredientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(context).inflate(R.layout.list_item_ingredient, parent, false);
        return new IngredientViewHolder(item);
    }

    /**
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(IngredientAdapter.IngredientViewHolder holder, int position) {
        cursor.moveToPosition(position);
        if (null != cursor) {
            holder.ingredientQty.setText(cursor.getString(IngredientEntry.POSITION_QUANTITY));
            holder.ingredientMeasure.setText(cursor.getString(IngredientEntry.POSITION_MEASURE));
            holder.ingredientName.setText(cursor.getString(IngredientEntry.POSITION_INGREDIENT));
        }
    }

    /**
     * @param position
     * @return
     */
    @Override
    public long getItemId(int position) {
        cursor.moveToPosition(position);
        return cursor.getLong(IngredientEntry.POSITION_ID);
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
    public class IngredientViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.ingredient_qty)
        TextView ingredientQty;

        @BindView(R.id.ingredient_measure)
        TextView ingredientMeasure;

        @BindView(R.id.ingredient_name)
        TextView ingredientName;

        IngredientViewHolder(View itemView) {
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
