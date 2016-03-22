package recode360.spreeadminapp.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

import recode360.spreeadminapp.Activities.MainActivity;
import recode360.spreeadminapp.R;
import recode360.spreeadminapp.adapter.OrderAdapter;
import recode360.spreeadminapp.app.AppController;
import recode360.spreeadminapp.app.Config;
import recode360.spreeadminapp.models.Orders;


public class AllOrdersFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Toolbar toolbar;
    private SwipeRefreshLayout swipeLayout;

    // To store all the orders
    private List<Orders> ordersList;
    private List<Orders> tempList;

    // Progress dialog
    private ProgressDialog pDialog;

    private OrderAdapter adapter;
    private RecyclerView recList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.all_orders_fragment, container, false);
        recList = (RecyclerView) view.findViewById(R.id.cardList);
        // recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);


        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.orders);

        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeLayout.setOnRefreshListener(this);

        ordersList = new ArrayList<Orders>();

        fetchOrders();
        //  adapter= new OrderAdapter(ordersList);
        recList.setAdapter(adapter);

        return view;
    }

    //this is to fetch the requires JSON response regarding all the orders
    private void fetchOrders() {

        if (!(swipeLayout.isRefreshing())) {
            // Showing progress dialog before making request
            pDialog.setMessage("Fetching Orders...");
            showpDialog();
        }

        // ordersList.clear();
        tempList = new ArrayList<Orders>();

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
                    for (int i = 0; i < orders.length(); i++) {

                        JSONObject order = (JSONObject) orders
                                .get(i);

                        String number = order.getString("number");
                        String state = order.getString("state");
                        String total_quantity = order.getString("total_quantity");
                        String payment_state = order.getString("payment_state");
                        String display_total = order.getString("display_total");
                        String shipment_state = order.getString("shipment_state");

                        Orders ord = new Orders();
                        ord.setNumber(number);
                        ord.setState(state);
                        ord.setTotal_quantity(Integer.parseInt(total_quantity));
                        ord.setPayment_state(payment_state);
                        ord.setDisplay_total(display_total);
                        ord.setShipment_state(shipment_state);


                        if (payment_state.equals("paid"))
                            //ordersList.add(ord);
                            tempList.add(ord);
                    }

                    // notifying adapter about data changes, so that the
                    // list renders with new data
                    ordersList.clear();
                    ordersList.addAll(tempList);
                    adapter = new OrderAdapter(ordersList, getActivity());
                    recList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }

                // hiding the progress dialog
                hidepDialog();
                swipeLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getActivity(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
                hidepDialog();
                swipeLayout.setRefreshing(false);
            }
        });


        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        jsonObjReq.setShouldCache(false);
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);

    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void onRefresh() {
        fetchOrders();
    }

}
