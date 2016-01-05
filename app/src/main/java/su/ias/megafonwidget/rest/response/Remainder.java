package su.ias.megafonwidget.rest.response;

import java.util.Date;

/**
 * Created by Aleksei Romashkin
 * on 24.12.15.
 */
public class Remainder {
    public String total;
    public String unit;
    public String remainderType;
    public String name;
    public String available;
    public String dateFrom;
    public String dateTo;
    public String optionId;
    public boolean monthly;
    public boolean isFuture;

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(Object a) {
        if (a == null
                || !(a instanceof Remainder)
                || this.name == null
                || ((Remainder) a).name == null) {
            return false;
        }
        return this.name.equals(((Remainder) a).name);
    }
}
