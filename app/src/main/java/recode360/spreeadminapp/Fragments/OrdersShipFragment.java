package recode360.spreeadminapp.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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


public class OrdersShipFragment extends Fragment {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Toolbar toolbar;

    // To store the pending orders,i.e. the payment is pending
    private List<Orders> ordersList;

    // Progress dialog
    private ProgressDialog pDialog;

    private OrderAdapter adapter;

    private RecyclerView recList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.orders_ship_fragment, container, false);
        recList = (RecyclerView) view.findViewById(R.id.cardList2);
        //recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);



        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

        ordersList = new ArrayList<Orders>();


        fetchOrders();
        //  adapter= new OrderAdapter(ordersList);
        recList.setAdapter(adapter);

        return view;
    }

    //this is to fetch the requires JSON response regarding all the orders
    private void fetchOrders(){

        // Showing progress dialog before making request
        pDialog.setMessage("Fetching Orders...");
        showpDialog();

        // Making json object request
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                "https://rails-tutorial-anoopkanyan.c9.io/api/orders.json?token=" + Config.API_KEY, null, new Response.Listener<JSONObject>() {

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


                        String number=order.getString("number");
                        String state=order.getString("state");
                        String total_quantity=order.getString("total_quantity");
                        String payment_state=order.getString("payment_state");
                        String display_total=order.getString("display_total");
                        String shipment_state=order.getString("shipment_state");

                        Orders ord= new Orders();
                        ord.setNumber(number);
                        ord.setState(state);
                        ord.setTotal_quantity(Integer.parseInt(total_quantity));
                        ord.setPayment_state(payment_state);
                        ord.setDisplay_total(display_total);
                        ord.setShipment_state(shipment_state);

                        //add only those orders whose payment is pending
                        if(payment_state.equals("paid")&&shipment_state.equals("ready")){
                            ordersList.add(ord);}
                    }

                    // notifying adapter about data changes, so that the
                    // list renders with new data
                    adapter= new OrderAdapter(ordersList,getActivity());
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
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getActivity(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
                hidepDialog();
            }
        });


        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


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

}
