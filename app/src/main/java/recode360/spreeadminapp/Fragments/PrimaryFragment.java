package recode360.spreeadminapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import recode360.spreeadminapp.Activities.AllShipmentsActivity;
import recode360.spreeadminapp.Activities.MainActivity;
import recode360.spreeadminapp.R;
import recode360.spreeadminapp.app.AppController;
import recode360.spreeadminapp.app.Config;


//Fragnent for the dashboard, displays all the relevant information on the store
public class PrimaryFragment extends Fragment {

    private static final String TAG = PrimaryFragment.class.getSimpleName();

    private Button ordersButton;
    private Button shipButton;
    private Button listingsButton;
    private MaterialDialog dialog;

    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;


    private String total_listings;
    private String out_of_stock;    //listings which are currently out of stock
    private int count;
    private TextView in_stock;
    private TextView out_stock;

    private Double orders_revenue;
    private int orders_count;
    private TextView orders_count_text;
    private TextView orders_revenue_text;


    private Double shipments_revenue;
    private Double shipments_expenditure;
    private TextView shipments_revenue_text;
    private TextView shipments_expenditure_text;

    String result;

    public PrimaryFragment() {
        //nothing here yet
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.primary_layout, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.dashboard);

        try {


            ordersButton = (Button) rootView.findViewById(R.id.ordersButton);
            shipButton = (Button) rootView.findViewById(R.id.shippingsButton);
            listingsButton = (Button) rootView.findViewById(R.id.listingsButton);

            getListingStats();
            getOrderStats();

            in_stock = (TextView) rootView.findViewById(R.id.listingsStock);
            out_stock = (TextView) rootView.findViewById(R.id.listingsNoStock);

            orders_count_text = (TextView) rootView.findViewById(R.id.ordersQty);
            orders_revenue_text = (TextView) rootView.findViewById(R.id.ordersRevenue);

            shipments_revenue_text = (TextView) rootView.findViewById(R.id.shippingsRevenue);
            shipments_expenditure_text = (TextView) rootView.findViewById(R.id.shippingsExpenditure);


            mFragmentManager = getActivity().getSupportFragmentManager();
            mFragmentTransaction = mFragmentManager.beginTransaction();

            ordersButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // launch Orders fragment
                    mFragmentTransaction.replace(R.id.containerView, new TabFragment()).commit();

                }
            });

            shipButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), AllShipmentsActivity.class);
                    getActivity().startActivity(intent);
                }
            });

            listingsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //launch shipments fragment
                    MainActivity act = (MainActivity) getActivity();
                    act.setOutofStock(true);
                    mFragmentTransaction.replace(R.id.containerView, new ProductsFragment()).commit();

                }
            });


        }
        catch (Exception e){

            //screw you
        }

        return rootView;
    }


    //get the stats for all the listings, i.e products listed on the store
    private void getListingStats() {


        dialog = new MaterialDialog.Builder(getContext())
                .content("Updating Dashboard")
                .progress(true, 0)
                .titleColor(getResources().getColor(R.color.colorPrimaryDark))
                .widgetColor(getResources().getColor(R.color.colorAccent))
                .contentColor(getResources().getColor(R.color.colorPrimary))
                .cancelable(false)
                .autoDismiss(false)
                .show();


        //request for total number of stock items
        String url = Config.URL_STORE + "/api/stock_locations/1/stock_items";
        final String TAG = "Stock items request";

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());

                        try {
                            total_listings = response.getString("count");
                            Log.d("Total Listings are", total_listings);
                            in_stock.setText(total_listings);

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
                headers.put("X-Spree-Token", Config.API_KEY);
                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, "goshippo_api request");


        //request for stock items which are out of stock
        url = Config.URL_STORE + "/api/stock_locations/1/stock_items?q[s]=count_on_hand%20asc";
        count = 0;
        jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());

                        try {
                            JSONArray stock_items = response.getJSONArray("stock_items");

                            for (int i = 0; i < stock_items.length(); i++) {

                                if (stock_items.getJSONObject(i).getInt("count_on_hand") == 0) {
                                    count++;
                                } else {
                                    break;
                                }

                            }

                            Log.d("Out of Stokck", Integer.toString(count));
                            out_stock.setText(Integer.toString(count));

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
                headers.put("X-Spree-Token", Config.API_KEY);
                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, "stock_tems request");


    }


    private void getOrderStats() {


        // Making json object request
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                Config.URL_STORE + "/api/orders.json?q[s]=updated_at%20desc&per_page=200&token=" + Config.API_KEY, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                orders_revenue = 0.0;
                shipments_revenue = 0.0;
                orders_count = 0;
                shipments_expenditure = 0.0;

                try {
                    orders_count = response.getInt("count");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    JSONArray orders = response
                            .getJSONArray("orders");

                    // looping through all order nodes and storing
                    // them in array list
                    for (int i = 0; i < orders.length(); i++) {

                        JSONObject order = (JSONObject) orders
                                .get(i);


                        String payment_state = order.getString("payment_state");
                        if (payment_state.equals("paid")) {
                            orders_revenue = orders_revenue + order.getDouble("total");
                            Log.d("Hellaleuah", orders_count + " $" + Double.toString(orders_revenue));

                            try {
                                shipments_revenue = shipments_revenue + order.getDouble("ship_total");
                                shipments_expenditure = shipments_expenditure + order.getJSONArray("shipments").getJSONObject(0).getDouble("label_cost");
                            } catch (Exception e) {

                                Log.d("Goshippo", "No fields preset for this shipment");


                            }
                        }

                    }

                    dialog.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }

                orders_revenue_text.setText("$" + String.format("%.2f", orders_revenue));
                orders_count_text.setText(Integer.toString(orders_count));
                shipments_revenue_text.setText("$" + String.format("%.2f", shipments_revenue));
                shipments_expenditure_text.setText(("$") + String.format("%.2f", shipments_expenditure));

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getActivity(),
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
        AppController.getInstance().

                addToRequestQueue(jsonObjReq);

    }

    public static void onBackPressed() {


    }
}