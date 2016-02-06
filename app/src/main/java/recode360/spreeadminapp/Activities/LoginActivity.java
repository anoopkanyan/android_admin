package recode360.spreeadminapp.Activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import recode360.spreeadminapp.R;
import recode360.spreeadminapp.app.AppController;
import recode360.spreeadminapp.models.sessions.AlertDialogManager;
import recode360.spreeadminapp.models.sessions.SessionManager;

public class LoginActivity extends Activity {

    public static final String TAG = AppController.class
            .getSimpleName();
    // Email, password edittext
    EditText txtEmail, txtPassword, txtStoreURL;

    // login button
    Button btnLogin;

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    // Session Manager Class
    SessionManager session;

    //JSONObject to send the user details
    JSONObject jsonBody;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Session Manager
        session = new SessionManager(getApplicationContext());

        // Email, Password input text
        txtEmail = (EditText) findViewById(R.id.inputEmail);
        txtPassword = (EditText) findViewById(R.id.inputPassword);
        txtStoreURL = (EditText) findViewById(R.id.inputURL);


        // Login button
        btnLogin = (Button) findViewById(R.id.btn_login);


        // Login button click event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Get username, password from EditText
                final String username = txtEmail.getText().toString();
                final String password = txtPassword.getText().toString();
                final String URL = txtStoreURL.getText().toString();


                // Check if username, password is filled
                if (username.trim().length() > 0 && password.trim().length() > 0 && URL.trim().length() > 0) {
                    // For testing puspose username, password is checked with sample data

                    String details = "{\"user\":{\"email\":" + username + ",\"password\":" + password + "}}";

                    try {
                        jsonBody = new JSONObject(details);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String tag_json_obj = "json_obj_req";

                    final String url = URL + "api/users/sign_in";


                    //  final ProgressDialog pDialog = new ProgressDialog(getBaseContext());
                    // pDialog.setMessage("Verfying");
                    // pDialog.show();

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
                                        session.createLoginSession(response.getJSONObject("bill_address").getString("full_name"), username, response.getString("spree_api_key"), URL);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    // Staring MainActivity
                                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(i);
                                    finish();
                                    //               pDialog.hide();
                                }
                            }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            VolleyLog.d(TAG, "Error: " + error.getMessage());
                            alert.showAlertDialog(LoginActivity.this, "Login failed..", "Shop URL/Username/Password is incorrect", false);
                            // hide the progress dialog
                            //pDialog.hide();
                        }
                    });

                    // Adding request to request queue
                    AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

                } else {
                    // user didn't entered username or password
                    // Show alert asking him to enter the details
                    alert.showAlertDialog(LoginActivity.this, "Login failed..", "Please enter URL, username and password", false);
                }

            }
        });
    }
}