package su.ias.megafonwidget.rest;

import android.content.Context;

import java.net.CookieHandler;

import retrofit.RestAdapter;
import retrofit.android.AndroidLog;

/**
 * Created by Aleksei Romashkin
 * on 23.12.15.
 */
public class Connector {

    public static void initialize() {
        MegafonCookieManager megafonCookieManager = new MegafonCookieManager();
        CookieHandler.setDefault(megafonCookieManager);
    }

    public static API getHttpApi() {
        return getApi(API.ENDPOINT_HTTP);
    }

    public static API getHttpsApi() {
        return getApi(API.ENDPOINT_HTTPS);
    }

    private static API getApi(String endpoint) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(endpoint)
                .setRequestInterceptor(new MegafonRequestInterceptor())
                .setLogLevel(RestAdapter.LogLevel.FULL).setLog(new AndroidLog("MEGAFON_HTTP_LOG"))
                .build();
        return restAdapter.create(API.class);
    }
}
