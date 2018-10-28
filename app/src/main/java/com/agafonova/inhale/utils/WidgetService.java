package com.agafonova.inhale.utils;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by Olga Agafonova on 10/28/18.
 */

public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new InhaleAppRemoteViewsFactory(this, intent);
    }
}