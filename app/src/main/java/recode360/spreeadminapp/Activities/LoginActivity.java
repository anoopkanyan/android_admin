package recode360.spreeadminapp.Activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import recode360.spreeadminapp.R;
import recode360.spreeadminapp.app.AppController;
import recode360.spreeadminapp.models.sessions.AlertDialogManager;
import recode360.spreeadminapp.models.sessions.SessionManager;

public class LoginActivity extends Activity {

    public static final String TAG = AppController.class
            .getSimpleName();

    private TextInputLayout inputLayoutUrl, inputLayoutEmail, inputLayoutPassword;

    // Email, password edittext
    private EditText txtEmail, txtPassword, txtStoreURL;


    // login button
    Button btnLogin;

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();
    private MaterialDialog dialog;

    // Session Manager Class
    SessionManager session;

    //JSONObject to send the user details
    private JSONObject jsonBody;
    private String URL;
    private String url;
    private String token;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);


        // Session Manager
        session = new SessionManager(getApplicationContext());


        inputLayoutUrl = (TextInputLayout) findViewById(R.id.input_layout_url);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);


        // Email, Password input text
        txtEmail = (EditText) findViewById(R.id.inputEmail);
        txtPassword = (EditText) findViewById(R.id.inputPassword);
        txtStoreURL = (EditText) findViewById(R.id.inputURL);


        txtStoreURL.addTextChangedListener(new MyTextWatcher(txtStoreURL));
        txtEmail.addTextChangedListener(new MyTextWatcher(txtEmail));
        txtPassword.addTextChangedListener(new MyTextWatcher(txtPassword));


        // Login button
        btnLogin = (Button) findViewById(R.id.btn_login);


        // Login button click event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {


                if (!validateEmail()) {
                    return;
                }

                if (!validatePassword()) {
                    return;
                }


                // Get username, password from EditText
                final String username = txtEmail.getText().toString().trim();
                final String password = txtPassword.getText().toString().trim();
                URL = txtStoreURL.getText().toString().trim();

                if (!URL.matches("^(https?|www)://.*$")) {
                    URL = "https://" + URL;
                }


                // Check if username, password is filled
                if (username.trim().length() > 0 && password.trim().length() > 0 && URL.trim().length() > 0) {

                    final String details = "{\"user\":{\"email\":\"" + username + "\",\"password\":\"" + password + "\"}}";

                    try {
                        jsonBody = new JSONObject(details);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String tag_json_obj = "json_obj_req";


                    if (URL.endsWith("/")) {
                        url = URL + "api/users/sign_in";
                    } else {
                        url = URL + "/api/users/sign_in";
                    }

                    //  final ProgressDialog pDialog = new ProgressDialog(getBaseContext());
                    // pDialog.setMessage("Verfying");
                    // pDialog.show();


                    dialog = new MaterialDialog.Builder(LoginActivity.this)
                            .title("Sign In")
                            .content("Reaching your store")
                            .progress(true, 0)
                            .progressIndeterminateStyle(true)
                            .titleColor(getResources().getColor(R.color.colorPrimaryDark))
                            .widgetColor(getResources().getColor(R.color.colorAccent))
                            .contentColor(getResources().getColor(R.color.colorPrimary))
                            .autoDismiss(false)
                            .show();

                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                            url, jsonBody,
                            new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.d(TAG, response.toString());

                                    // Creating user login session
                                    // For testing i am stroing name, email as follow
                                    // Use user real data
                                    try {

                                        String name;
                                        try {
                                            name = response.getJSONObject("bill_address").getString("full_name");
                                        } catch (Exception e) {

                                            name = "Admin User";
                                        }

                                        session.createLoginSession(name, username, response.getString("spree_api_key"), URL, password);
                                        token = response.getString("spree_api_key");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    getStoreAttributes();

                                }
                            }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            VolleyLog.d(TAG, "Error: " + error.getMessage());
                            Log.e(url, details);
                            alert.showAlertDialog(LoginActivity.this, "Login failed..", "Shop URL/Username/Password is incorrect", false);

                            // hide the progress dialog
                            //pDialog.hide();
                            dialog.cancel();
                        }
                    }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            HashMap<String, String> headers = new HashMap<String, String>();
                            headers.put("Accept", "application/json");
                            headers.put("Content-Type", "application/json; charset=utf-8");
                            return headers;
                        }

                        @Override
                        public String getBodyContentType() {
                            return "application/json; charset=utf-8";
                        }

                    };

                    // Adding request to request queue
                    AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

                } else {
                    dialog.cancel();
                    // user didn't enter username or password
                    // Show alert asking him to enter the details
                    alert.showAlertDialog(LoginActivity.this, "Login failed..", "Please enter URL, username and password", false);
                }

            }
        });
    }


    @Override
    public void onBackPressed() {

        //don't go back to the previous activity, rather minimize the app


    }


    private boolean validatePassword() {
        if (txtPassword.getText().toString().trim().isEmpty()) {
            inputLayoutPassword.setError(("Password is empty"));
            requestFocus(txtPassword);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
            if (txtPassword.getText().toString().length() > 4) {
                btnLogin.setBackgroundColor(getResources().getColor(R.color.primary));
                btnLogin.setTextColor(getResources().getColor(R.color.white));
            } else {
                btnLogin.setBackgroundColor(getResources().getColor(R.color.white));
                btnLogin.setTextColor(getResources().getColor(R.color.primary));
            }
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.inputEmail:
                    validateEmail();
                    break;
                case R.id.inputPassword:
                    validatePassword();
                    break;
            }
        }
    }


    private boolean validateEmail() {
        String email = txtEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmail.setError("Email not valid");
            requestFocus(txtEmail);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }


    //get store attributes such as store name, goshippo api tokens, and mail address

    public void getStoreAttributes() {

        String url = URL + "/api/stores";
        final String TAG = "Store attributes request";


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());

                        try {
                            String token = response.getJSONArray("stores").getJSONObject(0).getString("goshippo_api");
                            SessionManager session = new SessionManager(getApplicationContext());
                            session.addGoshippoKey(token);


                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(i);
                            finish();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("X-Spree-Token", token);
                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, "store_attributes request");

    }


}