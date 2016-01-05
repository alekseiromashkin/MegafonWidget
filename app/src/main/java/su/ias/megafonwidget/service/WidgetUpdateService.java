package su.ias.megafonwidget.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneNumberUtils;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import su.ias.megafonwidget.MegafonAppWidgetConfigure;
import su.ias.megafonwidget.MegafonAppWidgetProvider;
import su.ias.megafonwidget.MegafonPreferences;
import su.ias.megafonwidget.R;
import su.ias.megafonwidget.rest.Connector;
import su.ias.megafonwidget.rest.response.Error;
import su.ias.megafonwidget.rest.response.Info;
import su.ias.megafonwidget.rest.response.Remainder;
import su.ias.megafonwidget.rest.response.Reminder;

/**
 * Created by Aleksei Romashkin
 * on 27/12/15.
 */
public class WidgetUpdateService extends Service {

    @Override
    public void onStart(Intent intent, int startId) {
        this.initialize(this);

        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] allWidgetIds = new int[0];
        Bundle bundle = intent.getExtras();
        if (bundle != null
                && bundle.containsKey(AppWidgetManager.EXTRA_APPWIDGET_IDS)) {
            allWidgetIds = bundle.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS);
        }

        for (final int appWidgetId : allWidgetIds) {
            String widgetKey = MegafonPreferences.getInstance().getWidgetkey();
            if (widgetKey != null) {
                final RemoteViews remoteViews = new RemoteViews(this.getPackageName(),
                        R.layout.megafon_appwidget);
                remoteViews.setViewVisibility(R.id.button_refresh, View.INVISIBLE);
                remoteViews.setViewVisibility(R.id.progress_spinner, View.VISIBLE);
                appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
                Connector.getHttpsApi().getInfo(widgetKey, new Callback<Info>() {
                    @Override
                    public void success(Info info, Response response) {
                        draw(WidgetUpdateService.this, appWidgetManager, appWidgetId, info);
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        if (retrofitError.getResponse() != null) {
                            remoteViews.setViewVisibility(R.id.button_refresh, View.VISIBLE);
                            remoteViews.setViewVisibility(R.id.progress_spinner, View.INVISIBLE);
                            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
                            Error error = (Error) retrofitError.getBodyAs(Error.class);
                            Toast.makeText(WidgetUpdateService.this, error.message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void initialize(Context context) {
        Connector.initialize();
        MegafonPreferences.initialize(context);
    }

    private void draw(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Info info) {
        appWidgetManager.updateAppWidget(appWidgetId, getRemoteViews(context, appWidgetId, info));
    }

    private static int getNumber(String string) {
        return Integer.parseInt(string.split("[^0-9]")[0]);
    }

    @SuppressWarnings("deprecation")
    public RemoteViews getRemoteViews(Context context, int appWidgetId, Info info) {
        boolean isEmpty = true;
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.megafon_appwidget);
        remoteViews.setTextViewText(R.id.text_available, String.format("%.2f Р", info.balance));
        remoteViews.setTextViewText(R.id.text_bonuses, String.format("%.2f", info.bonusBalance));
        remoteViews.setTextViewText(R.id.text_phone,
                PhoneNumberUtils.formatNumber(MegafonPreferences.getInstance().getLogin()));
        remoteViews.setViewVisibility(R.id.button_refresh, View.VISIBLE);
        remoteViews.setViewVisibility(R.id.progress_spinner, View.INVISIBLE);

        remoteViews.removeAllViews(R.id.list_reminders);
        for (Reminder reminder : info.reminders) {
            for (Remainder remainder : reminder.remainders) {
                if (isEmpty) {
                    isEmpty = false;
                    remoteViews.setViewVisibility(R.id.list_reminders, View.VISIBLE);
                    remoteViews.setViewVisibility(R.id.empty_view, View.GONE);
                }
                RemoteViews remainderView = new RemoteViews(context.getPackageName(),
                        R.layout.remainder);
                remainderView.setTextViewText(R.id.name, remainder.name);
                remainderView.setTextViewText(R.id.description,
                        String.format("%s / %s %s",
                                remainder.available, remainder.total, remainder.unit));

                int total;
                int available;
                try {
                    total = Integer.parseInt(remainder.total);
                } catch (NumberFormatException e) {
                    total = getNumber(remainder.total);
                }
                try {
                    available = Integer.parseInt(remainder.available);
                } catch (NumberFormatException e) {
                    available = getNumber(remainder.available);
                }

                remainderView.setProgressBar(R.id.progress, total, available, false);
                remoteViews.addView(R.id.list_reminders, remainderView);
            }
        }

        Intent intent = new Intent(MegafonAppWidgetProvider.REFRESH_BUTTON);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.button_refresh, pendingIntent );

        Intent settingsIntent = new Intent(context, MegafonAppWidgetConfigure.class);
        settingsIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent settingsPendingIntent = PendingIntent
                .getActivity(context, 0, settingsIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.widget_layout, settingsPendingIntent);
        return remoteViews;
    }
}
