package su.ias.megafonwidget.rest;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import su.ias.megafonwidget.rest.response.Account;
import su.ias.megafonwidget.rest.response.CaptchaRequired;
import su.ias.megafonwidget.rest.response.Info;

/**
 * Created by Aleksei Romashkin
 * on 18.12.15.
 */
public interface API {

    String ENDPOINT_HTTP = "http://api.megafon.ru";
    String ENDPOINT_HTTPS = "https://api.megafon.ru";

    String FIELD_LOGIN = "login";
    String FIELD_PASSWORD = "password";
    String FIELD_CAPTCHA = "captcha";

    @GET("/mlk/auth/check")
    void getCaptchaRequired(Callback<CaptchaRequired> cb);

    @GET("/mlk/auth/captcha")
    void getCaptchaImage(Callback<Response> cb);

    @FormUrlEncoded
    @POST("/mlk/login")
    void login(
            @Field(FIELD_LOGIN) String login,
            @Field(FIELD_PASSWORD) String password,
            Callback<Account> cb);

    @FormUrlEncoded
    @POST("/mlk/login")
    void login(
            @Field(FIELD_LOGIN) String login,
            @Field(FIELD_PASSWORD) String password,
            @Field(FIELD_CAPTCHA) String captcha,
            Callback<Account> cb);

    @POST("/mlk/api/widget/info")
    void getInfo(@Header("X-Widget-Key") String widgetKey, Callback<Info> cb);
}
