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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import recode360.spreeadminapp.R;
import recode360.spreeadminapp.app.AppController;
import recode360.spreeadminapp.app.Config;
import recode360.spreeadminapp.models.Address;
import recode360.spreeadminapp.models.Product;
import recode360.spreeadminapp.models.State;
import recode360.spreeadminapp.utils.DatabaseHandler;


public class ShippingPOSActivity extends AppCompatActivity {

    private String order_no;
    private String order_token; //token for the guest user
    private String order_state;

    private ArrayList<Product> items;

    private DatabaseHandler database;
    private List<State> states;
    private String[] stateNames;
    private Address newAddress;
    private State newState;

    private Spinner stateSpinner;
    private EditText email;
    private EditText firstName, lastName, addressLine1, addressLine2;
    private EditText city, pincode, phone;
    private ScrollView addOrEditContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent intent = this.getIntent();
        items = (ArrayList<Product>) intent.getSerializableExtra("products");

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

                addLineItems();


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
        email = (EditText) findViewById(R.id.fragment_address_email_txt);
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

        if (TextUtils.isEmpty(email.getText())) {
            email.setError("cannot be empty");
        }

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


    public void addAddress() {

        String details = "{\n" +
                "  \"order\": {\n" +
                "    \"bill_address_attributes\": {\n" +
                "      \"firstname\": \"" + newAddress.getFirstname() + "\",\n" +
                "      \"lastname\": \"" + newAddress.getLastname() + "\",\n" +
                "      \"address1\": \"" + newAddress.getAddress1() + "\",\n" +
                "      \"address2\": \"" + newAddress.getAddress2() + "\",\n" +
                "      \"city\": \"" + newAddress.getCity() + "\",\n" +
                "      \"phone\": \"" + newAddress.getPhone() + "\",\n" +
                "      \"zipcode\": \"" + newAddress.getZipcode() + "\",\n" +
                "      \"state_id\": " + newAddress.getState().getId() + ",\n" +
                "      \"country_id\": 232 \n" +
                "    },\n" +
                "    \"ship_address_attributes\": {\n" +
                "      \"firstname\": \"" + newAddress.getFirstname() + "\",\n" +
                "      \"lastname\": \"" + newAddress.getLastname() + "\",\n" +
                "      \"address1\": \"" + newAddress.getAddress1() + "\",\n" +
                "      \"address2\": \"" + newAddress.getAddress2() + "\",\n" +
                "      \"city\": \"" + newAddress.getCity() + "\",\n" +
                "      \"phone\": \"" + newAddress.getPhone() + "\",\n" +
                "      \"zipcode\": \"" + newAddress.getZipcode() + "\",\n" +
                "      \"state_id\":" + newAddress.getState().getId() + ",\n" +
                "      \"country_id\": 232\n" +
                "    }\n" +
                "  }\n" +
                "}";


        JSONObject jsonBody = null;
        try {
            jsonBody = new JSONObject(details);
            Log.d("ADDRESS IS", jsonBody.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String tag_json_obj = "order_update_request";
        String url = Config.URL_STORE + "/api/checkouts/" + order_no + ".json?order_token=" + order_token;

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
                        Log.d("Adding address", response.toString());

                        try {
                            response.getString("state");

                            if (response.getString("state").equals("address")) {

                                response.getString("display_tax_total");
                                response.getString("display_total");
                                response.getString("display_total");
                                response.getString("display_ship_total");

                                updateState();

                                //finish();

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


    //adds line items to the newly created blank Order.
    public void addLineItems() {

        String url = Config.URL_STORE + "/api/orders.json?token=" + Config.API_KEY;

        String tag_json_obj = "json_obj_req";
        // final String details = "{\"line_item\":{\"variant_id\":\"" + username + "\",\"quantity\":\"" + password + "\"}}";

        final String details_initial = "{\"order\": {\"line_items\": [";

        final String end = "],\"email\":\"" + email.getText().toString() + "\"}}";

        String prev = "";

        for (int i = 0; i < items.size(); i++) {

            prev = prev + "{\"variant_id\":" + items.get(i).getId() + ", \"quantity\":" + items.get(i).getCart_qty() + "}";

            if (i != (items.size() - 1)) {
                prev = prev + ",";
            }
        }

        String details = details_initial + prev + end;

        JSONObject jsonBody = null;
        try {
            jsonBody = new JSONObject(details);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final MaterialDialog dialog = new MaterialDialog.Builder(ShippingPOSActivity.this)
                .content("Loading")
                .progress(true, 0)
                .titleColor(getResources().getColor(R.color.colorPrimaryDark))
                .widgetColor(getResources().getColor(R.color.colorAccent))
                .contentColor(getResources().getColor(R.color.colorPrimary))
                .autoDismiss(false)
                .cancelable(false)
                .show();

        Log.d("LINE_ITEMS", details);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, jsonBody,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("LINE ITEMS RESPONSE", response.toString());

                        try {
                            order_no = response.getString("number");
                            order_token = response.getString("token");

                            //next get the shipping rates and choose the default shipping method
                            dialog.hide();

                            //validate address and move to the delivery state
                            if (validateForm()) {
                                addAddress();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Add line items to cart", "Error: " + error.getMessage());
                // Log.e(url, details);

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

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        jsonObjReq.setShouldCache(false);

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }


    public void updateState() {

        String tag_json_obj = "Checkouts";
        String url = Config.URL_STORE + "/api/checkouts/" + order_no + "/next.json?order_token=" + order_token;

        final MaterialDialog pDialog = new MaterialDialog.Builder(this)
                .content("Recording payment")
                .widgetColor(getResources().getColor(R.color.colorAccent))
                .contentColor(getResources().getColor(R.color.colorPrimary))
                .progress(true, 0)
                .show();

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.PUT,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            order_state = response.getString("state");
                            Log.d(order_state, response.toString());

                            if (order_state.toString().equals("delivery")) {
                                //keep on updating the state with default values
                                updateState();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        pDialog.hide();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Cash Payment Activity", "Error: " + error.getMessage());
                pDialog.hide();


            }
        }) {

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req, tag_json_obj);
    }


}
