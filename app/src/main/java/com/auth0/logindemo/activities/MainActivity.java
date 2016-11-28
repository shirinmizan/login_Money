package com.auth0.logindemo.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.result.UserProfile;
import com.auth0.logindemo.R;
import com.auth0.logindemo.application.App;
import com.auth0.logindemo.utils.CredentialsManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private AuthenticationAPIClient mAuthenticationClient;
    public  String frikkenID;
    private Button mEditProfileButton;
    private Button mCancelEditionButton;
    private TextView mUsernameTextView;
    private TextView mUsermailTextView;
    private TextView mUserCountryTextView;
    private EditText mUpdateCountryEditext;
    private UserProfile mUserProfile;
    private Auth0 mAuth0;

    private static final String TAG_TYPE = "type";
    private static final String TAG_AMOUNT = "amount";
    private static final String TAG_CATEGORY = "category";
    private static final String TAG_DESC = "desc";
    private static final String TAG_DATE = "date";
    private static final String TAG_SUCCESS = "success";
    static final String FETCH_URL = "https://moneymoney.zapto.org/user/getData";

    String amount = null;
    String desc = null;
    String type = null;
    String date = null;
    String category = null;
    ArrayList<HashMap<String, String>> arraylist;

    public String thefuckingtoken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("main","mainfirst");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuthenticationClient = new AuthenticationAPIClient(new Auth0(getString(R.string.auth0_client_id), getString(R.string.auth0_domain)));
        //get the credentials from applocation App singleton
       /* mAuthenticationClient.tokenInfo(App.getInstance().getUserCredentials().getIdToken())
                .start(new BaseCallback<UserProfile, AuthenticationException>() {

                    @Override
                    public void onFailure(AuthenticationException error) {

                    }
                    WHY DID YOU TRY TO DO THIS OVER AGAIN? THIS IS DONE ON THE LOGIN ACTIVITY THATS HOW YOU GOT HERE

                  // ok I understand now? How about u get rid of what needs to get rid of
                  // I really cannot research now. I don't wanna learn now. If u fix
                  this I will understand what happend

                    @Override
                    public void onSuccess(final UserProfile payload) {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                mUserProfile = payload;
                            }
                        });
                    }
                });
                */

        Intent intent = getIntent();
        intent.getStringExtra("TOKEN");
        //Log.d("TOKEN");
        thefuckingtoken = intent.getStringExtra("TOKEN");
        Log.d("THEGODDAMNTOKEN",thefuckingtoken);

        mEditProfileButton = (Button) findViewById(R.id.editButton);
        mCancelEditionButton = (Button) findViewById(R.id.cancelEditionButton);
        mUsernameTextView = (TextView) findViewById(R.id.userNameTitle);
        mUsermailTextView = (TextView) findViewById(R.id.userEmailTitle);
        mUserCountryTextView = (TextView) findViewById(R.id.userCountryTitle);
        mUpdateCountryEditext = (EditText) findViewById(R.id.updateCountryEdittext);

        /**mEditProfileButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        editProfile();
        }
        });
         mCancelEditionButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        editModeOn(false);
        }
        });**/

        /**Button refreshTokenButton = (Button) findViewById(refreshTokenButton);
         Button idTokenButton = (Button) findViewById(R.id.tokenIDButton);
         Button logoutButton = (Button) findViewById(R.id.logout);

         refreshTokenButton.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
        getNewIDWithRefreshToken();
        }
        });**/
        //idTokenButton.setOnClickListener(new View.OnClickListener() {
        //@Override
        // public void onClick(View v) {
        //  getNewIDWithOldIDToken();
        // }
        //  });
        // logoutButton.setOnClickListener(new View.OnClickListener() {
        // @Override
        // public void onClick(View v) {
        //  logout();
        //  }
        //  });
        //   }

        new GetTransactionTask().execute();
    }
    public class GetTransactionTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                Log.d("inback","inback");
                URL url = new URL(FETCH_URL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("POST");
                urlConnection.connect();

                int responsecode = urlConnection.getResponseCode();

                Log.d("inbackRESPCode",String.valueOf(responsecode));
                if (responsecode == HttpURLConnection.HTTP_OK) {

                    Log.d("inback","ok code");
                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    br.close();
                    return sb.toString();
                }
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("post","post");
            Log.d("postS",String.valueOf(s));
            double sum1 = 0.0;
            double sum2 = 0.0;
            double amt = 0.0;
            String result1 = null;
            String result2 = null;
            double totalMoney;
            double balanceLeft;

            try {
                //initialize a JSONArray of Strings
                JSONArray jsonArray = null;
                try {

                    Log.d("postbeforejsonArray",String.valueOf(s));
                    jsonArray = new JSONArray(s);
                    Log.d("afterJsonsArray",String.valueOf(s));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //JSONObject to hold ojects from JSONArray
                JSONObject jsonObject = null;
                //loop through the array to get object and strings
                JSONObject newJSON = new JSONObject();

                for (int i = 0; i < jsonArray.length(); i++) {
                    //get JSONObject from the JSONArray. For each jsonobject in jsonarray
                    jsonObject = jsonArray.getJSONObject(i);
                    //get all types
                    type = jsonObject.getString(TAG_TYPE);
                    //Log.d("type ", type);
                    //get all amounts
                    amount = jsonObject.getString(TAG_AMOUNT);
                    amount = amount.replaceAll(",", "");
                    //to see data is coming
                    Log.d("Haha", type);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private void logout() {
        CredentialsManager.deleteCredentials(this);
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
