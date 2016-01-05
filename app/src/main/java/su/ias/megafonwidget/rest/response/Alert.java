package su.ias.megafonwidget.rest.response;

import com.google.gson.JsonObject;

import java.util.Map;

/**
 * Created by Aleksei Romashkin
 * on 24.12.15.
 */
public class Alert {
    public String level;
    public String action;
    public String text;
    public Map<String, JsonObject> params;
}
