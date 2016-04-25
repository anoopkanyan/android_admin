package recode360.spreeadminapp.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.shippo.Shippo;
import com.shippo.exception.ShippoException;
import com.shippo.model.Rate;
import com.shippo.model.Shipment;
import com.shippo.model.Transaction;

import org.json.JSONArray;
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
import recode360.spreeadminapp.models.Country;
import recode360.spreeadminapp.models.State;
import recode360.spreeadminapp.models.sessions.AlertDialogManager;

/**
 * Creates parcels, gets rates and creates labels
 */
public class CreateLabelActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private AutoCompleteTextView autoCompleteTextView;
    private Button submitbtn;
    private String selectedname;
    private AlertDialogManager alert;
    private JSONArray resp;
    private int pos;
    private EditText weight;
    private Spinner spinner;
    private String weight_unit;
    private Address stockLocation;
    private Shipment shipment;

    private ArrayAdapter<String> arrayAdapter;

    private Map<String, Object> toAddressMap;
    private Map<String, Object> fromAddressMap;
    private Map<String, Object> parcelMap;
    private Map<String, Object> shipmentMap;

    //get goshippo id's and save them
    //get the default package types and put them in an array of Strings
    //get the stock location's address from the Spree Store
    //create a parcel, to and from addresses
    //get ids of the parcels and create a Shipment Object
    //get rates for the shipment object and the id of that Shipment object
    //finally make payment

    private List<String> PACKAGES;
    private List<Rate> rates;
    private Rate rate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_label);

        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new ItemSelectedListener());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Create Label");

        Intent i = getIntent();
        final Address shipAddress = (Address) i.getSerializableExtra("ShipAddress");

        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        weight = (EditText) findViewById(R.id.packageWeight);
        submitbtn = (Button) findViewById(R.id.btn_create_label);

        spinner = (Spinner) findViewById(R.id.spinner);

        PACKAGES = new ArrayList<String>();
        arrayAdapter = new ArrayAdapter<String>(CreateLabelActivity.this, android.R.layout.simple_dropdown_item_1line, PACKAGES);

        autoCompleteTextView.setThreshold(0);

        autoCompleteTextView.setAdapter(arrayAdapter);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pos = position;
                selectedname = (String) parent.getItemAtPosition(position);
            }
        });

        autoCompleteTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                autoCompleteTextView.showDropDown();
                return false;
            }
        });

        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                create_label(shipAddress, stockLocation);
            }

        });

        //get the Stock Location
        stockLocation = getStockLocation();

        //get all the default package types
        getPackages();

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


    private void create_label(Address shipAddress, Address stockLocation) {

        // replace with your Shippo Token
        // don't have one? get more info here (https://goshippo.com/docs/#overview)
        Shippo.setApiKey(Config.USER_SHIPPO_KEY);

        // Optional defaults to false
        Shippo.setDEBUG(true);

        // to address
        toAddressMap = new HashMap<String, Object>();
        toAddressMap.put("object_purpose", "PURCHASE");
        toAddressMap.put("name", shipAddress.getFull_name());
        toAddressMap.put("street1", shipAddress.getAddress2());
        toAddressMap.put("street2", shipAddress.getAddress1());
        toAddressMap.put("city", shipAddress.getCity());
        toAddressMap.put("state", shipAddress.getState().getAbbr());
        toAddressMap.put("zip", shipAddress.getZipcode());
        toAddressMap.put("country", shipAddress.getCountry().getIso());
        toAddressMap.put("email", "mrhippo@goshipppo.com");

        // we have to get the email from the order itself

        // from address, to be updated from the Store itself
        fromAddressMap = new HashMap<String, Object>();
        fromAddressMap.put("object_purpose", "PURCHASE");
        fromAddressMap.put("name", stockLocation.getFull_name());
        fromAddressMap.put("company", "Shippo");
        fromAddressMap.put("street1", stockLocation.getAddress1());
        // fromAddressMap.put("street2", stockLocation.getAddress1());
        fromAddressMap.put("city", stockLocation.getCity());
        fromAddressMap.put("state", stockLocation.getState().getAbbr());
        fromAddressMap.put("zip", stockLocation.getZipcode());
        fromAddressMap.put("country", stockLocation.getCountry().getIso());
        fromAddressMap.put("email", Config.USER_EMAIL);
        fromAddressMap.put("phone", stockLocation.getPhone());
        //fromAddressMap.put("metadata", "Customer ID 123456");


        //create  parcel based on User Input
        parcelMap = new HashMap<String, Object>();
        try {
            parcelMap.put("length", resp.getJSONObject(pos).getString("dimension_length"));
            parcelMap.put("width", resp.getJSONObject(pos).getString("dimension_width"));
            parcelMap.put("height", resp.getJSONObject(pos).getString("dimension_height"));
            parcelMap.put("distance_unit", resp.getJSONObject(pos).getString("dimension_unit"));
            parcelMap.put("weight", weight.getText().toString());
            parcelMap.put("mass_unit", weight_unit);

        } catch (Exception e) {
            Log.d("something went wrong", "Yea");
        }

        shipmentMap = new HashMap<String, Object>();
        shipmentMap.put("address_to", toAddressMap);
        shipmentMap.put("address_from", fromAddressMap);
        shipmentMap.put("parcel", parcelMap);
        shipmentMap.put("object_purpose", "PURCHASE");
        shipmentMap.put("async", false);

        AsyncTaskRunner1 runner = new AsyncTaskRunner1();
        runner.execute();

    }

    public class ItemSelectedListener implements AdapterView.OnItemSelectedListener {

        //get strings of first item
        String firstItem = String.valueOf(spinner.getSelectedItem());

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            if (firstItem.equals(String.valueOf(spinner.getSelectedItem()))) {
                weight_unit = firstItem;
                // ToDo when first item is selected
            } else {
                weight_unit = parent.getItemAtPosition(pos).toString();
                /*
                Toast.makeText(parent.getContext(),
                        "You have selected : " + parent.getItemAtPosition(pos).toString(),
                        Toast.LENGTH_LONG).show();
                // Todo when item is selected by the user
                */
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg) {

        }

    }


    private class AsyncTaskRunner1 extends AsyncTask<String, String, String> {

        ProgressDialog progressDialog = new ProgressDialog(CreateLabelActivity.this);

        @Override
        protected String doInBackground(String... params) {

            try {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);

                shipment = Shipment.create(shipmentMap);

                // select shipping rate according to your business logic
                // we select the first rate in this example
                rates = shipment.getRatesList();

            } catch (ShippoException e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            progressDialog.hide();
            // rates fetched successfully, render to the dialog to choose among the different rates
            showDialog();

        }


        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Getting Rates");
            progressDialog.show();

        }


        @Override
        protected void onProgressUpdate(String... text) {


        }
    }

    private void getPackages() {
        //get all the default package type from the Spree Store
        String tag_json_obj = "default_packages_request";

        String url = Config.URL_STORE + "/api/goshipments/package.json";

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        JsonArrayRequest req = new JsonArrayRequest(
                url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Package type responses", response.toString());
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                PACKAGES.add(response.getJSONObject(i).getString("parcel"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        resp = response;
                        arrayAdapter.notifyDataSetChanged();
                        pDialog.hide();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Package Type", "Error: " + error.getMessage());
                pDialog.hide();
            }
        }) {

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Spree-Token", Config.API_KEY);
                return headers;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req, tag_json_obj);
    }

    private Address getStockLocation() {
        //gets the stock location
        final Address stockLocation = new Address();
        String tag_json_obj = "stock_locations_request";

        String url = Config.URL_STORE + "/api/stock_locations";

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        JsonObjectRequest req = new JsonObjectRequest(
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        JSONArray locations = null;
                        try {
                            locations = response.getJSONArray("stock_locations");

                            JSONObject defaultLocation = locations.getJSONObject(0);

                            //Spree address or Stock model doesn't have any associated email,
                            // however for Shippo address we need to have an email

                            stockLocation.setFull_name(defaultLocation.getString("name"));
                            stockLocation.setAddress1(defaultLocation.getString("address1"));
                            stockLocation.setAddress2(defaultLocation.getString("address2"));
                            stockLocation.setZipcode(Integer.parseInt(defaultLocation.getString("zipcode")));
                            stockLocation.setPhone(defaultLocation.getString("phone"));
                            stockLocation.setCity(defaultLocation.getString("city"));

                            State locationState = new State();
                            locationState.setAbbr(defaultLocation.getJSONObject("state").getString("abbr"));

                            Country locationCountry = new Country();
                            locationCountry.setIso(defaultLocation.getJSONObject("country").getString("iso"));

                            stockLocation.setState(locationState);
                            stockLocation.setCountry(locationCountry);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        pDialog.hide();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Stock Location", "Error: " + error.getMessage());
                pDialog.hide();
            }
        }) {

            /**
             * Passing request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Spree-Token", Config.API_KEY);
                return headers;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req, tag_json_obj);

        return stockLocation;

    }

    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateLabelActivity.this);
        builder.setTitle("Choose a Rate");


        String[] items = new String[rates.size()];
        int index = 0;
        for (int i = 0; i < rates.size(); i++) {
            items[index] = rates.get(i).getProvider().toString().toUpperCase() + ":  " + rates.get(i).getCurrency().toString() + " " + rates.get(i).getAmount().toString();
            index++;
        }

        builder.setSingleChoiceItems(items, 1,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing here

                    }
                });

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();

                        // positive button logic
                        rate = rates.get(selectedPosition);
                        Log.e("Debug", rate.getAmount().toString());
                        AsyncTaskRunner2 runner = new AsyncTaskRunner2();
                        runner.execute();
                    }
                });

        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // negative button logic
                        //move back to the Orders activity
                    }
                });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }


    private class AsyncTaskRunner2 extends AsyncTask<String, String, String> {

        ProgressDialog progressDialog = new ProgressDialog(CreateLabelActivity.this);

        @Override
        protected String doInBackground(String... params) {

            try {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);

                //we already have the rates from the Shipment Object
                System.out.println("Getting shipping label..");
                Map<String, Object> transParams = new HashMap<String, Object>();
                transParams.put("rate", rate.getObjectId());
                transParams.put("async", false);
                Transaction transaction = Transaction.create(transParams);

                if (transaction.getObjectStatus().equals("SUCCESS")) {
                    System.out.println(String.format("Label url : %s", transaction.getLabelUrl()));
                    System.out.println(String.format("Tracking number : %s", transaction.getTrackingNumber()));
                    return transaction.getLabelUrl().toString();
                } else {
                    System.out.println(String.format("An Error has occured while generating you label. Messages : %s", transaction.getMessages()));
                }
            } catch (ShippoException e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(String result) {

            progressDialog.hide();

            //Open the bowser for the Label download
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(result));
            startActivity(browserIntent);

            AlertDialog.Builder builder = new AlertDialog.Builder(CreateLabelActivity.this);
            builder.setMessage("Label generated successfully.Check your downloads folder.  ")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            CreateLabelActivity.this.onBackPressed();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();

        }


        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Generating label");
            progressDialog.show();

        }


        @Override
        protected void onProgressUpdate(String... text) {

        }
    }

}
