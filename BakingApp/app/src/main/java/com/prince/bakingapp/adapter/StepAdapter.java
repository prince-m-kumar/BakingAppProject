package com.prince.bakingapp.adapter;

import android.annotation.SuppressLint;
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

import static com.prince.bakingapp.data.Contract.StepEntry;

/**
 *
 */
public class StepAdapter extends RecyclerView.Adapter<StepAdapter.StepViewHolder> {
    private Cursor cursor;
    private Context context;

    final private ListItemClickListener onClickListener;

    /**
     * @param cursor
     * @param context
     * @param listener
     */
    public StepAdapter(Cursor cursor, Context context, ListItemClickListener listener) {
        this.cursor = cursor;
        this.context = context;
        this.onClickListener = listener;
    }

    /**
     *
     */
    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex, StepViewHolder stepViewHolder);
    }

    /**
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public StepAdapter.StepViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(context).inflate(R.layout.list_item_step, parent, false);
        return new StepViewHolder(item);
    }

    /**
     * @param holder
     * @param position
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(StepAdapter.StepViewHolder holder, int position) {
        cursor.moveToPosition(position);
        if (null != cursor) {
            holder.stepNumber.setText(Integer.toString(cursor.getInt(StepEntry.POSITION_ID)));
            holder.stepDescription.setText(cursor.getString(StepEntry.POSITION_SHORT_DESCRIPTION));
        }
    }

    @Override
    public long getItemId(int position) {
        cursor.moveToPosition(position);
        return cursor.getLong(StepEntry.POSITION_ID);
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

    /**
     *
     */
    public class StepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.step_number)
        TextView stepNumber;

        @BindView(R.id.step_description)
        TextView stepDescription;

        /**
         * @param itemView
         */
        StepViewHolder(View itemView) {
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
