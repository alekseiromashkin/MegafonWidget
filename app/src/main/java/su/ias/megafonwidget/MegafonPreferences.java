package su.ias.megafonwidget;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Aleksei Romashkin
 * on 24.12.15.
 */
public class MegafonPreferences {

    private static final String PREFERENCE_COOKIE = "preference.cookie";
    private static final String PREFERENCE_WIDGETKEY = "preference.widgetkey";

    private static MegafonPreferences ourInstance = new MegafonPreferences();
    private static Context CONTEXT;
    public static MegafonPreferences getInstance() {
        return ourInstance;
    }

    private MegafonPreferences() {
    }

    public static void initialize(Context context) {
        CONTEXT = context;
    }

    public void setCookie(String cookie) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(PREFERENCE_COOKIE, cookie);
        editor.apply();
    }

    public String getCookie() {
        return getPreferences().getString(PREFERENCE_COOKIE, null);
    }

    public void setWidgetkey(String widgetkey) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(PREFERENCE_WIDGETKEY, widgetkey);
        editor.apply();
    }

    public String getWidgetkey() {
        return getPreferences().getString(PREFERENCE_WIDGETKEY, null);
    }

    public String getLogin() {
        return getPreferences().getString(CONTEXT.getString(R.string.preference_key__username), null);
    }

    public String getPassword() {
        return getPreferences().getString(CONTEXT.getString(R.string.preference_key__password), null);
    }

    public int getUpdateMillis() {
        String stringInterval = this.getPreferences().getString(
                CONTEXT.getString(R.string.preference_key__update_interval),
                CONTEXT.getString(R.string.megafon_appwidget__updatePeriodMillis_value_5min));
        return Integer.parseInt(stringInterval);
    }

    private SharedPreferences getPreferences() {
        if (CONTEXT == null) {
            this.notInitializedException();
        }
        return PreferenceManager.getDefaultSharedPreferences(CONTEXT);
    }

    private void notInitializedException() {
        throw new IllegalStateException("MegafonPreferences not have been initialized!");
    }
}
