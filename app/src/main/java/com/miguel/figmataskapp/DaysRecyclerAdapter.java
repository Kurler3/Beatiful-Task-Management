package com.miguel.figmataskapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class DaysRecyclerAdapter extends RecyclerView.Adapter<DaysRecyclerAdapter.DaysViewHolder> {
    String[] mDaysMap;
    int mDaySelected;
    OnDayItemSelectedListener mOnDayItemListener;

    public DaysRecyclerAdapter(OnDayItemSelectedListener listener, String[] daysMap, int initialPosition){
        mOnDayItemListener = listener;
        mDaysMap = daysMap;
        mDaySelected = initialPosition;
    }

    public class DaysViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout mItemLayout;
        TextView mDayNumber, mDayText;
        public DaysViewHolder(@NonNull View itemView) {
            super(itemView);

            mDayNumber = itemView.findViewById(R.id.day_item_number);
            mDayText = itemView.findViewById(R.id.day_item_day_week);
            mItemLayout = itemView.findViewById(R.id.day_item_layout);

            itemView.setOnClickListener(v -> {

                notifyItemChanged(mDaySelected);
                mDaySelected = getLayoutPosition();
                notifyItemChanged(mDaySelected);

                // Day will always be -1 from the actual day in month
                mOnDayItemListener.changeDay(getAdapterPosition());
            });
        }

    }
    @NonNull
    @Override
    public DaysViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.day_select_item, parent, false);

        return new DaysViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DaysViewHolder holder, int position) {
        String day = mDaysMap[position];

        holder.mDayText.setText(day);
        holder.mDayNumber.setText(String.valueOf(position+1));

        // Changes the background
        holder.itemView.setSelected(mDaySelected==position);
    }


    @Override
    public int getItemCount() {
        return mDaysMap.length;
    }

    public interface OnDayItemSelectedListener{
        void changeDay(int dayMonth);
    }

}
