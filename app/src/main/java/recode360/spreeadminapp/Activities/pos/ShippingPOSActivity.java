package recode360.spreeadminapp.Activities.pos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import recode360.spreeadminapp.R;
import recode360.spreeadminapp.app.AppController;
import recode360.spreeadminapp.app.Config;
import recode360.spreeadminapp.models.Address;
import recode360.spreeadminapp.models.State;
import recode360.spreeadminapp.utils.DatabaseHandler;


public class ShippingPOSActivity extends AppCompatActivity {

    private String order_no;
    private DatabaseHandler database;
    private List<State> states;
    private String[] stateNames;
    private Address newAddress;
    private State newState;

    private Spinner stateSpinner;
    private EditText firstName, lastName, addressLine1, addressLine2;
    private EditText city, pincode, phone;
    private ScrollView addOrEditContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent intent = this.getIntent();
        order_no = intent.getStringExtra("order_no");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping_pos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.bs_ic_clear);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Address");

        stateSpinner = (Spinner) findViewById(R.id.fragment_address_state_spinner);

        initAddressUI();

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
                //add the Shipping address and get the shipping prices

                createBlankOrder();


                //if (validateForm()) {
                //editAddress();
                //  updateOrder();
                //}
                return true;

            case android.R.id.home:
                //shipping disabled
                //move back to the previous activity
                super.onBackPressed();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    private void setStateSpinnerAdapter() {

        database = new DatabaseHandler(this);
        states = database.getAllStates();
        stateNames = new String[states.size()];

        int i = 0;
        for (State state : states) {
            stateNames[i] = state.getName();
            i++;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ShippingPOSActivity.this, android.R.layout.simple_spinner_item, stateNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }


    private void initAddressUI() {

        // ADD or EDIT mode
        addOrEditContainer = (ScrollView) findViewById(R.id.fragment_address_scroll_view);
        firstName = (EditText) findViewById(R.id.fragment_address_first_name_txt);
        lastName = (EditText) findViewById(R.id.fragment_address_last_name_txt);
        addressLine1 = (EditText) findViewById(R.id.fragment_address_line1_txt);
        addressLine2 = (EditText) findViewById(R.id.fragment_address_line2_txt);
        city = (EditText) findViewById(R.id.fragment_address_city_txt);
        pincode = (EditText) findViewById(R.id.fragment_address_pincode_txt);
        phone = (EditText) findViewById(R.id.fragment_address_phone_txt);
        stateSpinner = (Spinner) findViewById(R.id.fragment_address_state_spinner);

        setStateSpinnerAdapter();

    }


    //validates the address form
    private boolean validateForm() {

        if (TextUtils.isEmpty(firstName.getText())) {
            firstName.setError("cannot be empty");
            return false;
        }
        if (TextUtils.isEmpty(lastName.getText())) {
            lastName.setError("cannot be empty");
            return false;
        }
        if (TextUtils.isEmpty(addressLine1.getText())) {
            addressLine1.setError("cannot be empty");
            return false;
        }
        if (TextUtils.isEmpty(city.getText())) {
            city.setError("cannot be empty");
            return false;
        }
        if (TextUtils.isEmpty(pincode.getText())) {
            pincode.setError("cannot be empty");
            return false;
        }
        if (TextUtils.isEmpty(phone.getText())) {
            phone.setError("cannot be empty");
            return false;
        }

        newAddress = new Address();
        newState = new State();
        newState.setId(states.get(stateSpinner.getSelectedItemPosition()).getId());

        newAddress.setFirstname(firstName.getText().toString());
        newAddress.setLastname(lastName.getText().toString());
        newAddress.setAddress1(addressLine1.getText().toString());
        newAddress.setAddress2(addressLine2.getText().toString());
        newAddress.setCity(city.getText().toString());
        newAddress.setState(newState);
        newAddress.setZipcode(Integer.parseInt(pincode.getText().toString()));
        newAddress.setPhone(phone.getText().toString());
        return true;
    }


    public void updateOrder() {

        String ship_address = "\"ship_address\":{\"firstname\":\"" + newAddress.getFirstname() + "\",\"lastname\":\"" + newAddress.getLastname() + "\",\"address1\":\"" + newAddress.getAddress1() + "\",\"address2\":\"" + newAddress.getAddress2() + "\",\"country_id\": 232,\"state_id\":3535,\"city\":\"" + newAddress.getCity() + "\",\"zipcode\": " + newAddress.getZipcode() + ",\"phone\": \"" + newAddress.getPhone() + "\"}";

        Log.d("THE SHIP ADDRESS IS", ship_address);
        String bill_address = "\"bill_address\":{\"firstname\": \"Test\",\"lastname\": \"User\",\"address1\": \"Unit \",\"address2\": \"1 Test Lane\",\"country_id\": 232,\"state_id\": 3535,\"city\": \"Bethesda\",\"zipcode\": \"20814\",\"phone\": \"(555) 555-5555\"}";


        String details = "{\"order\": {" + ship_address + "," + bill_address + ",email:\"anoop123@gmail.com\"}}";


        JSONObject jsonBody = null;
        try {
            jsonBody = new JSONObject(details);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        String tag_json_obj = "order_update_request";
        String url = Config.URL_STORE + "/api/checkouts/" + order_no + ".json?token=" + Config.API_KEY;


        final MaterialDialog pDialog = new MaterialDialog.Builder(this)
                .content("Updating address")
                .widgetColor(getResources().getColor(R.color.colorAccent))
                .contentColor(getResources().getColor(R.color.colorPrimary))
                .progress(true, 0)
                .show();


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT,
                url, jsonBody,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Adding payment", response.toString());


                        try {
                            response.getString("state");

                            if (response.getString("state").equals("address")) {

                                response.getString("display_tax_total");
                                response.getString("display_total");
                                response.getString("display_total");
                                response.getString("display_ship_total");

                                BigDecimal shippingCost = new BigDecimal(response.getString("ship_total"));

                                finish();

                            }
                            pDialog.hide();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Adding new Address", "Error: " + error.getMessage());

                pDialog.cancel();
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

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        jsonObjReq.setShouldCache(false);

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }


    public void createBlankOrder() {


        String url = Config.URL_STORE + "/api/orders.json?token=" + Config.API_KEY + "&order[email]=test@example.com";

        String tag_json_obj = "blank_order_request";

        final MaterialDialog dialog = new MaterialDialog.Builder(ShippingPOSActivity.this)
                .content("Loading")
                .progress(true, 0)
                .titleColor(getResources().getColor(R.color.colorPrimaryDark))
                .widgetColor(getResources().getColor(R.color.colorAccent))
                .contentColor(getResources().getColor(R.color.colorPrimary))
                .autoDismiss(false)
                .cancelable(false)
                .show();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Created blank order", response.toString());

                        try {
                            order_no = response.getString("number");
                            Log.d("EMAIL IS", response.getString("email"));


                            dialog.hide();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Add line items to cart", "Error: " + error.getMessage());

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

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        jsonObjReq.setShouldCache(false);

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);


    }

}
