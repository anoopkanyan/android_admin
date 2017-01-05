package recode360.spreeadminapp.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import recode360.spreeadminapp.R;
import recode360.spreeadminapp.adapter.ShippedShipmentsAdapter;
import recode360.spreeadminapp.app.AppController;
import recode360.spreeadminapp.app.Config;
import recode360.spreeadminapp.models.Shipments;

public class AllShipmentsActivity extends AppCompatActivity {

    private static final String TAG = AllShipmentsActivity.class.getSimpleName();
    private MaterialDialog dialog;

    private List<Shipments> shipmentsList;
    private ShippedShipmentsAdapter shipmentsAdapter;
    private RecyclerView recyclerView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_shipments);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Shipments");


        recyclerView = (RecyclerView) findViewById(R.id.shipped_shipments_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        shipmentsList = new ArrayList<Shipments>();

        getOrderStats();
    }


    private void getOrderStats() {

        dialog = new MaterialDialog.Builder(this)
                .content("Fetching Shipments")
                .progress(true, 0)
                .titleColor(getResources().getColor(R.color.colorPrimaryDark))
                .widgetColor(getResources().getColor(R.color.colorAccent))
                .contentColor(getResources().getColor(R.color.colorPrimary))
                .cancelable(false)
                .autoDismiss(false)
                .show();

        // Making json object request
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                Config.URL_STORE + "/api/orders.json?q[s]=updated_at%20desc&per_page=200&token=" + Config.API_KEY, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    JSONArray orders = response
                            .getJSONArray("orders");

                    // looping through all order nodes and storing
                    // them in array list
                    for (int j = 0; j < orders.length(); j++) {

                        JSONObject order = (JSONObject) orders
                                .get(j);

                        String payment_state = order.getString("payment_state");
                        if (payment_state.equals("paid")) {

                            JSONArray shipments = order.getJSONArray("shipments");
                            for (int i = 0; i < shipments.length(); i++) {
                                Shipments shipment = new Shipments();
                                shipment.setNumber(shipments.getJSONObject(i).getString("number"));
                                shipment.setState(shipments.getJSONObject(i).getString("state"));
                                shipment.setStock_location_name("");    //stock location name to be updated from the stock location id
                                shipment.setOrder_id(order.getString("id"));


                                //if GoShippo is used then present the GoShippo attributes to the user
                                if (shipments.getJSONObject(i).getString("label_url").length() > 0) {
                                    shipment.setLabel_url(shipments.getJSONObject(i).getString("label_url"));
                                    shipment.setCost(shipments.getJSONObject(i).getString("label_cost"));
                                    shipment.setTracking(shipments.getJSONObject(i).getString("tracking"));
                                    shipment.setParcel_object_id(shipments.getJSONObject(i).getString("parcel_object_id"));
                                    // shipment.setTransaction_obj_id(shipments.getJSONObject(i).getString("transaction_object_id"));
                                }

                                shipmentsList.add(shipment);

                            }


                            shipmentsAdapter = new ShippedShipmentsAdapter(shipmentsList, AllShipmentsActivity.this);
                            recyclerView.setAdapter(shipmentsAdapter);
                            shipmentsAdapter.notifyDataSetChanged();

                        }

                    }

                    dialog.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AllShipmentsActivity.this,
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(AllShipmentsActivity.this,
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
                dialog.dismiss();

            }
        });


        jsonObjReq.setRetryPolicy(new
                DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        );

        jsonObjReq.setShouldCache(false);
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);

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


}
