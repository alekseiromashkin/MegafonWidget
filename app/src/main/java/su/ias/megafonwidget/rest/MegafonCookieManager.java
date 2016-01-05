package su.ias.megafonwidget.rest;

import java.io.IOException;

import java.net.CookieManager;
import java.net.URI;
import java.util.List;
import java.util.Map;

import su.ias.megafonwidget.MegafonPreferences;

/**
 * Created by Aleksei Romashkin
 * on 23.12.15.
 */
public class MegafonCookieManager extends CookieManager {

    public static final String SET_COOKIE = "Set-Cookie";
    public static final String COOKIE = "Cookie";
    public static final String JSESSIONID = "JSESSIONID";

    @Override
    public void put(URI uri, Map<String, List<String>> stringListMap) throws IOException {
        super.put(uri, stringListMap);
        if (stringListMap != null && stringListMap.get(SET_COOKIE) != null)
            for (String string : stringListMap.get(SET_COOKIE)) {
                if (string.contains(JSESSIONID)) {
                    MegafonPreferences.getInstance().setCookie(string);
                }
            }
    }
}
