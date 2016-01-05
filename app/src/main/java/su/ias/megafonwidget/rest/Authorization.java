package su.ias.megafonwidget.rest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import su.ias.megafonwidget.MegafonPreferences;
import su.ias.megafonwidget.R;
import su.ias.megafonwidget.rest.response.Account;
import su.ias.megafonwidget.rest.response.CaptchaRequired;
import su.ias.megafonwidget.rest.response.Error;

/**
 * Created by Aleksei Romashkin
 * on 18.12.15.
 */
public class Authorization {

    private Context context;
    private AuthorizationListener authorizationListener;
    private String login;
    private String password;

    public interface AuthorizationListener {
        void onAuthorized(Account account);
        void onFailed(Error error);
    }

    public Authorization(Context context) {
        this.context = context;
    }

    public void login(String login, String password, AuthorizationListener authorizationListener) {
        this.login = login;
        this.password = password;
        this.authorizationListener = authorizationListener;
        Connector.getHttpApi().getCaptchaRequired(new Callback<CaptchaRequired>(){
            public void success(CaptchaRequired captchaRequired, Response response) {
                if (captchaRequired.captchaNeeded) {
                    inputCaptcha();
                } else {
                    authorize();
                }
            }

            public void failure(RetrofitError error) {
                showMessage(error.getMessage());
            }
        });
        MegafonPreferences.getInstance().setCookie(null);
    }

    private void inputCaptcha() {
        Connector.getHttpsApi().getCaptchaImage(new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                try {
                    showCaptcha(response.getBody().in());
                } catch (IOException ignore) {

                }
            }

            @Override
            public void failure(RetrofitError error) {
                showMessage(error.getMessage());
            }
        });
    }

    private void showCaptcha(InputStream inputStream) {
        AlertDialog.Builder captchaDialog = new AlertDialog.Builder(this.context);
        LayoutInflater factory = LayoutInflater.from(this.context);
        @SuppressLint("InflateParams") View view = factory.inflate(R.layout.dialog_input_captcha, null);
        ImageView imageCaptcha = (ImageView) view.findViewById(R.id.image_captcha);
        final EditText inputCaptcha = (EditText) view.findViewById(R.id.input_captcha);
        imageCaptcha.setImageBitmap(BitmapFactory.decodeStream(inputStream));
        captchaDialog.setView(view);
        captchaDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dlg, int sumthin) {
                authorize(inputCaptcha.getText().toString());
            }
        });
        captchaDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dlg, int sumthin) {

            }
        });
        captchaDialog.show();
    }

    private void authorize() {
        Connector.getHttpsApi().login(this.login, this.password, new Callback<Account>() {
            @Override
            public void success(Account account, Response response) {
                authorizationComplete(account);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                authorizationFailed(retrofitError);
            }
        });
    }

    private void authorize(String captcha) {
        Connector.getHttpsApi().login(this.login, this.password, captcha,
                new Callback<Account>() {
            @Override
            public void success(Account account, Response response) {
                authorizationComplete(account);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                authorizationFailed(retrofitError);
            }
        });
    }

    private void authorizationComplete(Account account) {
        if (this.authorizationListener != null) {
            this.authorizationListener.onAuthorized(account);
        }
    }

    private void authorizationFailed(RetrofitError retrofitError) {
        if (this.authorizationListener != null) {
            Error error = null;
            if (retrofitError.getResponse() != null) {
                error = (Error) retrofitError.getBodyAs(Error.class);
                showMessage(error.message);
            }
            this.authorizationListener.onFailed(error);
        }
    }

    private void showMessage(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
