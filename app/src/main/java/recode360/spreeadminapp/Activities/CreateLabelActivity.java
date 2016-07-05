package recode360.spreeadminapp.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.shippo.Shippo;
import com.shippo.exception.ShippoException;
import com.shippo.model.Rate;
import com.shippo.model.Shipment;
import com.shippo.model.Transaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private String parcel_weight;
    private String parcel_template;
    private Address stockLocation;
    private Shipment shipment;
    private ImageButton carrierButton;
    private ImageButton boxesButton;
    private ArrayAdapter<String> arrayAdapter;
    private String shipment_number;
    private TextView carriersText;
    private TextView templateText;
    private TextView insuranceText;

    //JSONObject to send the user details
    private JSONObject jsonBody;
    private String url;
    public static final String TAG = AppController.class
            .getSimpleName();

    private boolean useTemplateName = true;
    private boolean isInsurance;
    private boolean isPickup;
    private boolean returnLabel;
    private boolean includeSignature;
    private String submission_type = "DROPOFF";
    private String signature_confirmation = "STANDARD";
    //if insured, then use insurance price and currency
    private String insurance_price;
    private String insurance_currency = "USD";


    private CheckBox chkCustomerSignature;
    private CheckBox chkReturnLabel;
    private CheckBox chkInsurance;
    private CheckBox chkPickupPackage;
    private CheckBox chkTemplate;

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

    private List<Rate> rates;
    private Rate rate;
    private Context yeh;


    //items to be updated on the store

    private String tracking;
    private String parcel_object_id;
    private String tracking_url;
    private String transaction_obj_id;
    private String shipment_object_id;
    private String label_url;
    private String return_shipment_obj_id;
    private String return_label_url;
    private String refund_object_id;
    private String label_cost;

    //is_label checks if GoShippo was used for label generation or not
    private boolean is_label;
    private Transaction transaction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_label);

        yeh = this;

        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new ItemSelectedListener());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Create Label");

        Intent i = getIntent();
        final Address shipAddress = (Address) i.getSerializableExtra("ShipAddress");
        shipment_number = i.getStringExtra("shipment_number");


        carriersText = (TextView) findViewById(R.id.carriers);
        templateText = (TextView) findViewById(R.id.template);
        insuranceText = (TextView) findViewById(R.id.insurancePrice);


        //checkboxes for preferences when generating a label and getting data from them
        chkCustomerSignature = (CheckBox) findViewById(R.id.chkCustomerSignature);
        chkReturnLabel = (CheckBox) findViewById(R.id.chkReturnLabel);
        chkInsurance = (CheckBox) findViewById(R.id.chkInsurance);
        chkPickupPackage = (CheckBox) findViewById(R.id.chkPickupPackage);
        chkTemplate = (CheckBox) findViewById(R.id.chkTemplate);


        chkCustomerSignature.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;


                if (cb.isChecked()) {
                    //Customer signature to be STANDARD or ADULT
                    includeSignature = true;
                    signature_confirmation = "ADULT";
                } else {
                    signature_confirmation = "STANDARD";
                }


            }
        });


        chkReturnLabel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;

                if (cb.isChecked()) {
                    //also include return label
                    returnLabel = true;
                }
            }
        });


        chkInsurance.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;

                if (cb.isChecked()) {
                    //shipment to be insured
                    //show a dialog and fetch necessary information required for insuring the shipment through goShippo
                    isInsurance = true;
                    new MaterialDialog.Builder(yeh)
                            .title("Insurance")
                            .titleColor(getResources().getColor(R.color.accent))
                            .content("Enter the insurance amount")
                            .inputType(InputType.TYPE_NUMBER_FLAG_DECIMAL)
                            .input("Amount", "0.00", new MaterialDialog.InputCallback() {
                                @Override
                                public void onInput(MaterialDialog dialog, CharSequence input) {
                                    Log.d("The insurance amount is", input.toString());
                                    insurance_price = input.toString();
                                    insuranceText.setText("USD " + insurance_price);
                                }
                            }).show();


                }

                else{

                    insuranceText.setText("USD 0.00" );


                }

            }
        });


        chkPickupPackage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;

                if (cb.isChecked()) {
                    //Customer pickup or to be deliverd
                    isPickup = true;
                    submission_type = "PICKUP";
                } else {
                    submission_type = "DROPOFF";
                }

            }
        });


        chkTemplate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;

                if (cb.isChecked()) {
                    useTemplateName = false;
                } else {
                    useTemplateName = true;
                }

            }
        });

        weight = (EditText) findViewById(R.id.packageWeight);
        submitbtn = (Button) findViewById(R.id.btn_create_label);

        parcel_weight = weight.getText().toString();

        carrierButton = (ImageButton) findViewById(R.id.imageButton);
        carrierButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CreateLabelActivity.this, CarriersActivity.class);
                startActivity(i);
            }
        });

        boxesButton = (ImageButton) findViewById(R.id.imageButton1);
        boxesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CreateLabelActivity.this, PackagesActivity.class);
                startActivityForResult(i, 1);
            }
        });


        spinner = (Spinner) findViewById(R.id.spinner);


        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                create_label(shipAddress, stockLocation);
            }

        });

        //get the Stock Location
        stockLocation = getStockLocation();

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

        //return if the data is malformed or some part of it is missing
        if (checkData() == true) {


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
            toAddressMap.put("email", "spree@example.com");
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


            //get required info from the parcel template, at present just pass in the name of the parcel_template
            parcelMap = new HashMap<String, Object>();
            try {

                SharedPreferences spppp = getSharedPreferences("package_details", 0);

                parcelMap.put("length", spppp.getString("length", ""));
                parcelMap.put("width", spppp.getString("width", ""));
                parcelMap.put("height", spppp.getString("height", ""));
                parcelMap.put("distance_unit", "in");


                if (useTemplateName == true) {

                    parcelMap.put("template", spppp.getString("template", ""));

                }

                parcelMap.put("weight", weight.getText().toString());
                parcelMap.put("mass_unit", weight_unit);

            } catch (Exception e) {
                Log.d("something went wrong", "Yea");
            }


            shipmentMap = new HashMap<String, Object>();
            shipmentMap.put("address_to", toAddressMap);
            shipmentMap.put("address_from", fromAddressMap);
            shipmentMap.put("parcel", parcelMap);
            shipmentMap.put("submission_type", submission_type);
            shipmentMap.put("object_purpose", "PURCHASE");

            if (isInsurance) {

                shipmentMap.put("insurance_currency", insurance_currency);
                shipmentMap.put("insurance_amount", insurance_price);

            }

            shipmentMap.put("async", false);

            AsyncTaskRunner1 runner = new AsyncTaskRunner1();
            runner.execute();
        } else {
            return;
        }


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
                transaction = Transaction.create(transParams);

                if (transaction.getObjectStatus().equals("SUCCESS")) {
                    System.out.println(String.format("Label url : %s", transaction.getLabelUrl()));
                    System.out.println(String.format("Tracking number : %s", transaction.getTrackingNumber()));
                    updateStore();

                    return transaction.getLabelUrl().toString();
                } else {
                    System.out.println(String.format("An Error has occured while generating you label. Messages : %s", transaction.getMessages()));
                    Log.d("PROBLEM IS::", transaction.getMessages().toString());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Log.e("data", data.toString());
            //Do something useful with data
        }

        SharedPreferences spppp = getSharedPreferences("package_details", 0);
        templateText.setText(spppp.getString("template", ""));


    }


    //checks all the data before making a request for generating a label
    protected boolean checkData() {


        String error_message = null;   //error message to be displayed to the user if one of the inputs is missing or is incorrect

        /*
        if (parcel_template == null) {

            error_message = "Please choose a parcel type for your shipment.";


        }*/


        //check if package_weight and units are set
        if (weight.getText().toString().length() == 0) {

            error_message = "Please enter the weight of your parcel";

        }


        //check insurance details, if the product is meant to be insured

        if (isInsurance == true && insurance_price.equals("0.00")) {

            error_message = "Please enter the insurance amount";


        }

        if (error_message != null) {

            MaterialDialog dialog = new MaterialDialog.Builder(yeh)
                    .title("Error")
                    .content(error_message)
                    .positiveText("OK")
                    .show();

            return false;

        }


        //check if a package is selected
        return true;

    }


    //update the shipment_attributes to our backend
    public void updateStore() {

        tracking = transaction.getTrackingUrlProvider().toString();
        tracking_url = transaction.getTrackingUrlProvider().toString();
        transaction_obj_id = transaction.getObjectId();
        label_url = transaction.getLabelUrl().toString();
        shipment_object_id = shipment.getObjectId();
        label_cost = rate.getAmount().toString();
        parcel_object_id = shipment.getParcel().toString();
        is_label = true;

        Log.d("tracking", tracking);
        Log.d("transobjid", transaction_obj_id);
        Log.d("labelurl", label_url);
        Log.d("label_cost is ", label_cost);
        Log.d("shipment_object_id is", shipment_object_id);




        /* return shipments not being created at present
          return_shipment_obj_id
          return_label_url
         */


        url = Config.URL_STORE + "/api/shipments/" + shipment_number + "/ship?token=" + Config.API_KEY;

        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        StringRequest jsonObjReq = new StringRequest(Request.Method.PUT,
                url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        Log.d("Response:", response);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();
                params.put("shipment[label_url]", label_url);
                params.put("shipment[tracking]", tracking);
                params.put("shipment[transaction_obj_id]", transaction_obj_id);
                params.put("shipment[label_cost]", label_cost);
                params.put("shipment[parcel_object_id]", parcel_object_id);
                params.put("shipment[is_label]", "true");
                params.put("shipment[object_id]", shipment_object_id);

                //shipment_object_id and return shipment ids are still to be updated
                //params.put("shipment[]", );

                return params;
            }

        };

// Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);


    }


}
