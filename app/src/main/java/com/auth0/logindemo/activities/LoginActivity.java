package com.auth0.logindemo.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.result.UserProfile;
import com.auth0.logindemo.R;
import com.auth0.logindemo.application.App;
import com.auth0.logindemo.utils.CredentialsManager;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.lock.AuthenticationCallback;
import com.auth0.android.lock.Lock;
import com.auth0.android.lock.LockCallback;
import com.auth0.android.lock.utils.LockException;
import com.auth0.android.result.Credentials;
import com.auth0.android.result.UserProfile;
import com.auth0.logindemo.utils.CredentialsManager;

import java.util.HashMap;
import java.util.Map;

import static com.auth0.logindemo.utils.CredentialsManager.saveCredentials;

public class LoginActivity extends Activity {

    private Lock mLock;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("login","mainfirst");
        Auth0 auth0 = new Auth0(getString(R.string.auth0_client_id), getString(R.string.auth0_domain));
        //Request a refresh token along with the id token.
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("scope", "openid offline_access");

        mLock = Lock.newBuilder(auth0, mCallback)
                //Add parameters to the build
                .build(this);
        if (CredentialsManager.getCredentials(this).getIdToken() == null) {
            Log.d("login","cred man null");
            startActivity(mLock.newIntent(this));
            return;
        }
        Log.d("login","beforeaclient");
        AuthenticationAPIClient aClient = new AuthenticationAPIClient(auth0);
        Log.d("login","afteraclient");
        aClient.tokenInfo(CredentialsManager.getCredentials(this).getIdToken()).start(new BaseCallback<UserProfile, AuthenticationException>() {


            @Override
            public void onSuccess(final UserProfile payload) {
                Log.d("loginSuccess","afteraclient");
                LoginActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(LoginActivity.this, "Automatic Login Success", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.d("c", payload.getId());
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("TOKEN", payload.getId());
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(AuthenticationException error) {
                Log.d("loginFail","afteraclient");
                LoginActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(LoginActivity.this, "Session Expired, please Log In", Toast.LENGTH_SHORT).show();
                    }
                });
                CredentialsManager.deleteCredentials(getApplicationContext());
                startActivity(mLock.newIntent(LoginActivity.this));
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Your own Activity code
        mLock.onDestroy(this);
        mLock = null;
    }

    private final LockCallback mCallback = new AuthenticationCallback() {
        @Override
        public void onAuthentication(Credentials credentials) {
            Toast.makeText(LoginActivity.this, "Log In - Success", Toast.LENGTH_SHORT).show();
            App newapp = new App();
            Log.d("OnAuthfuck",String.valueOf(credentials));
            saveCredentials(LoginActivity.this,credentials);
            //  newapp.getInstance().setUserCredentials(credentials);
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            Log.d("OnAuthfuck","Bout2FinishCB");
            finish();
        }

        @Override
        public void onCanceled() {
            Toast.makeText(LoginActivity.this, "Log In - Cancelled", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(LockException error) {
            Toast.makeText(LoginActivity.this, "Log In - Error Occurred", Toast.LENGTH_SHORT).show();
        }
    };


}


