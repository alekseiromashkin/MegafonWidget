package su.ias.megafonwidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import su.ias.megafonwidget.service.WidgetUpdateService;

/**
 * Created by Aleksei Romashkin
 * on 18.12.15.
 */
public class MegafonAppWidgetProvider extends AppWidgetProvider {

    public static String REFRESH_BUTTON = "android.appwidget.action.REFRESH_BUTTON";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (REFRESH_BUTTON.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName thisWidget = new ComponentName(context, MegafonAppWidgetProvider.class);
            int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
            this.onUpdate(context, appWidgetManager, allWidgetIds);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Intent intent = new Intent(context.getApplicationContext(),
                WidgetUpdateService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        context.startService(intent);
    }
}
