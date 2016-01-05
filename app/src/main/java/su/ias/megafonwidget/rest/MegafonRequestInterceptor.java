package su.ias.megafonwidget.rest;

import android.content.Context;

import retrofit.RequestInterceptor;
import su.ias.megafonwidget.MegafonPreferences;

/**
 * Created by Aleksei Romashkin
 * on 23.12.15.
 */
public class MegafonRequestInterceptor implements RequestInterceptor {

    @Override
    public void intercept(RequestFacade request) {
        request.addHeader("User-Agent", "MLK test test 0");
        //request.addHeader("User-Agent", "MLK iOS Phone 1.0.1");
        String sessionId = MegafonPreferences.getInstance().getCookie();
        if (sessionId != null) {
            request.addHeader(MegafonCookieManager.COOKIE, sessionId);
        }
    }
}
