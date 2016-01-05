package su.ias.megafonwidget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.widget.Toast;

import java.util.Calendar;

import su.ias.megafonwidget.rest.Authorization;
import su.ias.megafonwidget.rest.response.Account;
import su.ias.megafonwidget.rest.response.Error;

/**
 * Created by Aleksei Romashkin
 * on 18.12.15.
 */
public class MegafonAppWidgetConfigure extends PreferenceActivity {

    private int mAppWidgetId = 0 ;
    private ProgressDialog progressDialog;

    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.addPreferencesFromResource(R.xml.megafon_appwidget_preferences);

        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            this.mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        this.findPreference(this.getString(R.string.preference_key__apply))
                .setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        applySettings();
                        return false;
                    }
                }
        );

        this.progressDialog = new ProgressDialog(this);
        this.progressDialog.setTitle(R.string.auth_in_progress);
        this.progressDialog.setMessage(this.getString(R.string.wait_message));
        this.progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    private void applySettings() {
        this.progressDialog.show();
        Authorization auth = new Authorization(this);
        auth.login(
                MegafonPreferences.getInstance().getLogin(),
                MegafonPreferences.getInstance().getPassword(),
                new Authorization.AuthorizationListener() {
                    @Override
                    public void onAuthorized(Account account) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        MegafonPreferences.getInstance().setWidgetkey(account.widgetKey);

                        Intent intent = new Intent(MegafonAppWidgetProvider.REFRESH_BUTTON);
                        PendingIntent pendingIntent = PendingIntent
                                .getBroadcast(MegafonAppWidgetConfigure.this, 0, intent, 0);
                        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(System.currentTimeMillis());
                        calendar.add(Calendar.SECOND, 10);
                        alarmManager.setRepeating(
                                AlarmManager.RTC_WAKEUP,
                                calendar.getTimeInMillis(),
                                MegafonPreferences.getInstance().getUpdateMillis(),
                                pendingIntent);

                        Intent resultValue = new Intent();
                        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                        setResult(RESULT_OK, resultValue);

                        finish();
                    }

                    @Override
                    public void onFailed(Error error) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        if (error != null) {
                            Toast.makeText(MegafonAppWidgetConfigure.this, error.message,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }
}
