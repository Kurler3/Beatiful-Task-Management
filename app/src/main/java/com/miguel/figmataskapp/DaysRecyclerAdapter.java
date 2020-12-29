package com.miguel.figmataskapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Map;

public class DaysRecyclerAdapter extends RecyclerView.Adapter<DaysRecyclerAdapter.DaysViewHolder> {
    String[] mDaysMap;
    Context mContext;

    public DaysRecyclerAdapter(Context context, String[] daysMap){
        mContext = context;
        mDaysMap = daysMap;
    }

    public class DaysViewHolder extends RecyclerView.ViewHolder{
        TextView mDayNumber, mDayText;
        public DaysViewHolder(@NonNull View itemView) {
            super(itemView);

            mDayNumber = itemView.findViewById(R.id.day_item_number);
            mDayText = itemView.findViewById(R.id.day_item_day_week);
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
    }

    @Override
    public int getItemCount() {
        return mDaysMap.length;
    }

}
