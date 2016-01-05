package su.ias.megafonwidget.rest.response;

import java.math.BigDecimal;

/**
 * Created by Aleksei Romashkin
 * on 24.12.15.
 */
public class Info {
    public Alert[] alerts;
    public Reminder[] reminders;
    public BigDecimal balance;
    public BigDecimal bonusBalance;
}
