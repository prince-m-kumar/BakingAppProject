package com.prince.bakingapp.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.prince.bakingapp.R;
import com.prince.bakingapp.data.BakeAppContract;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;



/**
 * Created by princ on 08-07-2017.
 */
public class RecipeIngredientAdapter extends RecyclerView.Adapter<RecipeIngredientAdapter.IngredientViewHolder> {
    private Cursor cursor;
    private Context context;

    final private ListItemClickListener onClickListener;

    public RecipeIngredientAdapter(Cursor cursor, Context context, ListItemClickListener listener) {
        this.cursor = cursor;
        this.context = context;
        this.onClickListener = listener;
    }

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex, IngredientViewHolder ingredientViewHolder);
    }

    @Override
    public RecipeIngredientAdapter.IngredientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(context).inflate(R.layout.list_item_ingredient, parent, false);
        return new IngredientViewHolder(item);
    }

    @Override
    public void onBindViewHolder(RecipeIngredientAdapter.IngredientViewHolder holder, int position) {
        cursor.moveToPosition(position);
        if (null != cursor) {
            DecimalFormat decimalFormat = new DecimalFormat("#,###.#");
            holder.ingredientQty.setText(decimalFormat.format(cursor.getDouble(BakeAppContract.IngredientEntry.POSITION_QUANTITY)));
            holder.ingredientMeasure.setText(cursor.getString(BakeAppContract.IngredientEntry.POSITION_MEASURE));
            holder.ingredientName.setText(cursor.getString(BakeAppContract.IngredientEntry.POSITION_INGREDIENT));
        }
    }

    @Override
    public long getItemId(int position) {
        cursor.moveToPosition(position);
        return cursor.getLong(BakeAppContract.IngredientEntry.POSITION_ID);
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

        @Override
        public void onClick(View v) {
            onClickListener.onListItemClick(getAdapterPosition(), this);
        }
    }
}
