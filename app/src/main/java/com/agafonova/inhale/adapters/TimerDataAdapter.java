package com.agafonova.inhale.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import com.agafonova.inhale.R;
import com.agafonova.inhale.model.TimerData;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Olga Agafonova on 10/7/18.
 */

public class TimerDataAdapter extends RecyclerView.Adapter<TimerDataAdapter.TimerDataAdapterViewHolder> {

    private List<TimerData> mTimerDataList;
    private TimerDataAdapter.ExerciseClickListener mOnClickListener;
    private Context mContext;
    private int lastSelectedPosition = -1;

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

        final TimerData someData = mTimerDataList.get(position);

        try {
            holder.textViewExhale.setText(someData.getExhale());
            holder.textViewInhale.setText(someData.getInhale());
            holder.radioButton.setChecked(lastSelectedPosition == position);
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
        private TextView textViewExhale;
        private TextView textViewInhale;

        public TimerDataAdapterViewHolder(View itemView) {
            super(itemView);
            radioButton = itemView.findViewById(R.id.radio_length);
            textViewExhale = itemView.findViewById(R.id.tv_length_exhale);
            textViewInhale = itemView.findViewById(R.id.tv_length_inhale);

            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lastSelectedPosition = getAdapterPosition();
                    notifyDataSetChanged();
            }
            });

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            TimerData selectedData = mTimerDataList.get(clickedPosition);

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
