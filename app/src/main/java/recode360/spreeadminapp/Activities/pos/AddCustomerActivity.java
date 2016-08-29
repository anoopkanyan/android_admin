package recode360.spreeadminapp.Activities.pos;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import recode360.spreeadminapp.Activities.MainActivity;
import recode360.spreeadminapp.R;
import recode360.spreeadminapp.app.AppController;
import recode360.spreeadminapp.app.Config;


public class AddCustomerActivity extends AppCompatActivity {

    private String order_no;

    private EditText txtFirstName;
    private EditText txtLastName;
    private EditText txtEmail;

    private String firstName;
    private String lastName;
    private String email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent intent = getIntent();
        order_no = intent.getStringExtra("order_no");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.bs_ic_clear);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtFirstName = (EditText) findViewById(R.id.textFirstName);
        txtLastName = (EditText) findViewById(R.id.textLastname);
        txtEmail = (EditText) findViewById(R.id.textEmail);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.next_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_add_product:
                updateCustomer();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    public void updateCustomer() {

        firstName = txtFirstName.getText().toString();
        lastName = txtLastName.getText().toString();
        email = txtEmail.getText().toString();

        // Tag used to cancel the request
        final String tag_json_obj = "customer_update_request";

        String url = Config.URL_STORE + "/api/pos/associate/" + order_no + "?token=" + Config.API_KEY+"&new_email="+email;

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(tag_json_obj, response.toString());
                        pDialog.hide();

                        Intent intent = new Intent(AddCustomerActivity.this,MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(tag_json_obj, "Error: " + error.getMessage());
                pDialog.hide();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }

}
