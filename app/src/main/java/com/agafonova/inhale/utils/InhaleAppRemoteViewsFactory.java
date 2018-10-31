package com.agafonova.inhale.utils;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import com.agafonova.inhale.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Olga Agafonova on 10/28/18.
 */

public class InhaleAppRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private static final String APP_NAME = "Inhale";
    private static final String EXERCISE_STRING = "EXERCISE_STRING";

    List<String> mCollection = new ArrayList<>();
    Context mContext = null;
    private int mWidgetId;

    public InhaleAppRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mCollection = new ArrayList<String>();
        mWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
        initData();
    }

    @Override
    public void onDataSetChanged() {
        initData();
    }

    @Override
    public void onDestroy() {
        mCollection.clear();
    }

    @Override
    public int getCount() {
        if(mCollection != null) {
            return mCollection.size();
        }
        else {
            return 0;
        }
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews view = new RemoteViews(mContext.getPackageName(),
                R.layout.listview_item);
        view.setTextViewText(R.id.list_content, mCollection.get(position));
        return view;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private void initData() {
        mCollection.clear();

        SharedPreferences sharedPreferences = mContext.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        String exerciseString = sharedPreferences.getString(EXERCISE_STRING,"");

        mCollection.add(exerciseString);
    }
}