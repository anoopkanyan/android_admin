package recode360.spreeadminapp.Activities.pos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import recode360.spreeadminapp.R;
import recode360.spreeadminapp.adapter.pos.OrderPricesAdapter;
import recode360.spreeadminapp.app.AppController;
import recode360.spreeadminapp.app.Config;
import recode360.spreeadminapp.models.Address;
import recode360.spreeadminapp.models.Product;
import recode360.spreeadminapp.models.State;

public class OrderPosAddressActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, OrderPricesAdapter.EditPlayerAdapterCallback {

    private ExpandableRelativeLayout expandableLayout1;
    private SwitchCompat swt;
    private ArrayList<Product> items;
    private RecyclerView recyclerView;
    private OrderPricesAdapter adapter;
    private Toolbar toolbar;
    private int done = 0;
    private TextView textPay;
    private TextView totalPriceView;
    private TextView totalQtyView;
    private TextView subTotalView;
    private TextView grandTotalView;
    private TextView taxTotalView;
    private TextView shipTotalView;

    private Float totalPrice = 0.00f;
    private int totalQuantity = 0;
    private Float tax = 0.00f;
    private Float shippingCost = 0.00f;


    private EditText firstName, lastName, addressLine1, addressLine2;
    private EditText city, pincode, phone;
    private ScrollView addOrEditContainer;
    private Button save;
    private Spinner stateSpinner;

    private String TAG = "States List Request";
    private String tag_json_obj = "states list request";
    private ArrayList<State> statesList = new ArrayList<State>();
    //specific id to get all the united states
    static String urlStates = "http://mystore-anupkanyan.cs50.io/api/countries/232.json";
    private String[] stateNames;


    private Address newAddress;
    private String order_no;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_pos_address);

        Intent intent = this.getIntent();
        items = (ArrayList<Product>) intent.getSerializableExtra("products");
        totalPrice = intent.getFloatExtra("price", 0.00f);
        totalQuantity = intent.getIntExtra("quantity", 0);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        textPay = (TextView) findViewById(R.id.cart_checkout_text);
        totalPriceView = (TextView) findViewById(R.id.cart_item_price);
        totalQtyView = (TextView) findViewById(R.id.cart_item_no);
        subTotalView = (TextView) findViewById(R.id.subtotal);
        grandTotalView = (TextView) findViewById(R.id.total);
        taxTotalView = (TextView) findViewById(R.id.tax_total);
        shipTotalView = (TextView) findViewById(R.id.ship_total);


        recyclerView = (RecyclerView) findViewById(R.id.cart_pos_recycler_view);
        adapter = new OrderPricesAdapter(this, items);

        totalPriceView.setText("$" + Float.toString(totalPrice));
        totalQtyView.setText(Integer.toString(totalQuantity));
        subTotalView.setText("$" + Float.toString(totalPrice));
        grandTotalView.setText("$" + Float.toString(totalPrice));


        // use a linear layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.setCallback(this);


        expandableLayout1 = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout1);
        initAddressUI();
        createCartOrder();

        textPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                launchPaymentActivity();

            }

        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validateForm()) {
                    //editAddress();
                    updateOrder();
                }
            }

        });


    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {

            case R.id.expandableButton1:

                if (!isChecked) {

                    Toast.makeText(OrderPosAddressActivity.this, "Err Switch is off!!", Toast.LENGTH_SHORT).show();
                    //  expandableLayout1.toggle(); // toggle expand and collapse
                } else {
                    Toast.makeText(OrderPosAddressActivity.this, "Yes Switch is on!!", Toast.LENGTH_SHORT).show();
                    //  expandableLayout1.toggle(); // toggle expand and collapse

                }
                break;

            default:
                break;
        }
    }

    public void expandableButton1(View view) {
        expandableLayout1 = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout1);
        expandableLayout1.toggle(); // toggle expand and collapse
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void deletePressed() {

        //do not do anything here
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
        save = (Button) findViewById(R.id.fragment_address_save_btn);
        stateSpinner = (Spinner) findViewById(R.id.fragment_address_state_spinner);

        setStateSpinnerAdapter();


    }


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
        if (TextUtils.isEmpty(addressLine2.getText())) {
            addressLine2.setError("cannot be empty");
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
        newAddress.setFirstname(firstName.getText().toString());
        newAddress.setLastname(lastName.getText().toString());
        newAddress.setAddress1(addressLine1.getText().toString());
        newAddress.setAddress2(addressLine2.getText().toString());
        newAddress.setCity(city.getText().toString());
        //newAddress.setStateId(stateArrayList.get(stateSpinnerSelectedItem).getId());
        newAddress.setZipcode(Integer.parseInt(pincode.getText().toString()));
        newAddress.setPhone(phone.getText().toString());
        return true;
    }


    private void editAddress() {


    }


    private void setStateSpinnerAdapter() {

        getStates();


    }


    public void getStates() {


        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .content("loading")
                .progress(true, 0)
                .show();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlStates, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray states = response.getJSONArray("states");
                            stateNames = new String[states.length()];
                            for (int i = 0; i < states.length(); i++) {

                                State state = new State();
                                state.setAbbr(states.getJSONObject(i).getString("abbr"));
                                state.setCountry_id(states.getJSONObject(i).getInt("country_id"));
                                state.setId(states.getJSONObject(i).getInt("id"));
                                state.setName(states.getJSONObject(i).optString("name"));

                                statesList.add(state);
                                stateNames[i] = state.getName();

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(OrderPosAddressActivity.this, android.R.layout.simple_spinner_item, stateNames);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        stateSpinner.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                        dialog.dismiss();

                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.networkResponse);
                // hide the progress dialog

                dialog.dismiss();
            }
        });


        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);


    }


    public void createCartOrder() {

        String url = Config.URL_STORE + "/api/orders.json?token=" + Config.API_KEY;

        String tag_json_obj = "json_obj_req";
        // final String details = "{\"line_item\":{\"variant_id\":\"" + username + "\",\"quantity\":\"" + password + "\"}}";

        final String details_initial = "{\"order\": {\"line_items\": [";

        final String end = "]}}";

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

        final MaterialDialog dialog = new MaterialDialog.Builder(OrderPosAddressActivity.this)
                .content("Loading")
                .progress(true, 0)
                .titleColor(getResources().getColor(R.color.colorPrimaryDark))
                .widgetColor(getResources().getColor(R.color.colorAccent))
                .contentColor(getResources().getColor(R.color.colorPrimary))
                .autoDismiss(false)
                .show();

        Log.d("Y O HO __", details);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, jsonBody,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Add line items to yoyo", response.toString());

                        try {
                            order_no = response.getString("number");
                            //update the tax amounts
                            tax = Float.valueOf(response.getString("tax_total"));
                            taxTotalView.setText(response.getString("display_tax_total"));
                            grandTotalView.setText(response.getString("display_total"));
                            totalPriceView.setText(response.getString("display_total"));

                            dialog.hide();


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


    public void updateOrder() {


//        String details = "{\"order\": {\"payments_attributes\": [{\"payment_method_id\": \"2\"}]},\"payment_source\": {\"2\": {}}}";


        String ship_address = "\"ship_address\":{\"firstname\":\"" + newAddress.getFirstname() + "\",\"lastname\":\"" + newAddress.getLastname() + "\",\"address1\":\"" + newAddress.getAddress1() + "\",\"address2\":\"" + newAddress.getAddress2() + "\",\"country_id\": 232,\"state_id\":3535,\"city\":\"" + newAddress.getCity() + "\",\"zipcode\": " + newAddress.getZipcode() + ",\"phone\": \"" + newAddress.getPhone() + "\"}";

        Log.d("yhui", ship_address);
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


                                taxTotalView.setText(response.getString("display_tax_total"));
                                grandTotalView.setText(response.getString("display_total"));
                                totalPriceView.setText(response.getString("display_total"));
                                shipTotalView.setText(response.getString("display_ship_total"));

                                shippingCost = Float.valueOf(response.getString("ship_total"));

                                updateState();
                            }
                            pDialog.hide();
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


    public void updateState() {
        String tag_json_obj = "Checkouts";
        String url = Config.URL_STORE + "/api/checkouts/" + order_no + "/next.json?token=" + Config.API_KEY;


        String details = "{\"order\": {\"shipments_attributes\": {\"0\": {\"selected_shipping_rate_id\": 1,\"id\": 1}}}}";


        JSONObject jsonBody = null;
        try {
            jsonBody = new JSONObject(details);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        final MaterialDialog pDialog = new MaterialDialog.Builder(this)
                .content("Recording payment")
                .widgetColor(getResources().getColor(R.color.colorAccent))
                .contentColor(getResources().getColor(R.color.colorPrimary))
                .progress(true, 0)
                .show();

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.PUT,
                url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


                        try {
                            Log.d("The order state is", response.getString("state"));

                            taxTotalView.setText(response.getString("display_tax_total"));
                            grandTotalView.setText(response.getString("display_total"));
                            totalPriceView.setText(response.getString("display_total"));
                            shipTotalView.setText(response.getString("display_ship_total"));

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


    //launch PaymentPosActivity and pass on the necessary details
    private void launchPaymentActivity() {

        Bundle information = new Bundle();

        ArrayList<Product> cart_products = (ArrayList<Product>) adapter.getProductList();
        information.putSerializable("products", cart_products);
        Intent intent = new Intent(OrderPosAddressActivity.this, PaymentPosActivity.class);
        intent.putExtras(information);
        intent.putExtra("quantity", totalQuantity);
        intent.putExtra("price", totalPrice + shippingCost + tax);
        intent.putExtra("order_no", order_no);
        intent.putExtra("shipping_cost", shippingCost);
        intent.putExtra("tax", tax);

        startActivity(intent);

    }

}



