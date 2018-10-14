package com.agafonova.inhale.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.agafonova.inhale.R;
import com.agafonova.inhale.model.TimerData;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Updated by Olga Agafonova on 10/13/18.
 *
 * Based on http://joshskeen.com/building-a-radiogroup-recyclerview/
 */

public class TimerDataAdapter extends RecyclerView.Adapter<TimerDataAdapter.TimerDataAdapterViewHolder> {

    private List<TimerData> mTimerDataList;
    private TimerDataAdapter.ExerciseClickListener mOnClickListener;
    private Context mContext;
    public int mSelectedItem = -1;

    public interface ExerciseClickListener {
        void onExerciseClick(TimerData selectedTimerData) throws ExecutionException, InterruptedException;
    }

    public TimerDataAdapter(Context iContext, TimerDataAdapter.ExerciseClickListener listener) {
        mOnClickListener = listener;
        mContext = iContext;
    }

    @Override
    public TimerDataAdapter.TimerDataAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.rv_length, parent, false);
        TimerDataAdapter.TimerDataAdapterViewHolder viewHolder = new TimerDataAdapter.TimerDataAdapterViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final TimerDataAdapter.TimerDataAdapterViewHolder holder, int position) {

        try {
            final TimerData someData = mTimerDataList.get(position);

            holder.radioButton.setChecked(position == mSelectedItem);
            holder.radioButton.setText(someData.getExhale()+ " : " + someData.getInhale());
            holder.radioButton.setTextColor(mContext.getColor(R.color.white));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if(mTimerDataList == null) {
            return 0;
        }
        return mTimerDataList.size();
    }

    public void setData(List<TimerData> results) {
        mTimerDataList = results;
        notifyDataSetChanged();
    }

    public class TimerDataAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private RadioButton radioButton;

        public TimerDataAdapterViewHolder(View itemView) {
            super(itemView);

            radioButton = itemView.findViewById(R.id.radio_button);
            radioButton.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            mSelectedItem = getAdapterPosition();
            notifyDataSetChanged();

            TimerData selectedData = mTimerDataList.get(mSelectedItem);

            try {
                mOnClickListener.onExerciseClick(selectedData);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}