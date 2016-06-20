package recode360.spreeadminapp.Activities;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import recode360.spreeadminapp.R;
import recode360.spreeadminapp.adapter.ShipmentsAdapter;
import recode360.spreeadminapp.app.AppController;
import recode360.spreeadminapp.app.Config;
import recode360.spreeadminapp.models.Address;
import recode360.spreeadminapp.models.Country;
import recode360.spreeadminapp.models.Orders;
import recode360.spreeadminapp.models.Shipments;
import recode360.spreeadminapp.models.State;

public class ShipmentsActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private ArrayList<String> countries;
    private String order_no;   //order number passed by the calling activity, will give us all the shipments relating to that order
    private List<Shipments> shipmentsList;
    private Orders order;
    private JsonObjectRequest jsonObjReq;
    private ShipmentsAdapter shipmentsAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get the order no
        Intent intent = getIntent();
        order_no = intent.getStringExtra("order_no");

        setContentView(R.layout.activity_shipments);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Shipments");


        initViews();
    }


    private void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.shipments_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        //this is store details related to the whole order
        order = new Orders();

        //this is to store details related to all the line items(products) in a particular order
        shipmentsList = new ArrayList<Shipments>();

        String tag_json_obj = "json_obj_req";
        String url = Config.URL_STORE + "/api/orders/" + order_no + ".json";
        Log.d("the url of the store is", url + Config.API_KEY);

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();


        //Request the line items inside the order response
        jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(getApplicationContext().toString(), response.toString());
                        try {
                            //parse order details
                            order.setId(response.getInt("id"));
                            order.setDisplay_total(response.getString("display_total"));
                            order.setDisplay_item_total(response.getString("display_item_total"));
                            order.setTotal_quantity(response.getInt("total_quantity"));
                            order.setNumber(response.getString("number"));

                            //set payment details(bill address)
                            JSONObject bill_address = response.getJSONObject("bill_address");
                            Address bill_addr = new Address();
                            bill_addr.setId(bill_address.getInt("id"));
                            bill_addr.setFull_name(bill_address.getString("full_name"));
                            bill_addr.setAddress1(bill_address.getString("address1"));

                            try {
                                bill_addr.setAddress2(bill_address.getString("address2"));
                            } catch (Exception e) {
                                // do nothing as the owner simply has no data in the field for address2
                            }
                            bill_addr.setCity(bill_address.getString("city"));
                            bill_addr.setPhone(bill_address.getString("phone"));

                            JSONObject bill_country = bill_address.getJSONObject("country"); //country details for the bill address
                            Country bill_ctry = new Country();
                            bill_ctry.setName(bill_country.getString("name"));
                            bill_ctry.setIso(bill_country.getString("iso"));
                            bill_addr.setCountry(bill_ctry);    //put country object to the bill object

                            JSONObject bill_state = bill_address.getJSONObject("state");     //state details for the bill address
                            State bill_st = new State();
                            bill_st.setName(bill_state.getString("name"));
                            bill_st.setAbbr(bill_state.getString("abbr"));
                            bill_addr.setState(bill_st);    //put state object to the bill object

                            order.setBill_address(bill_addr);   //finally put bill address in the order object

                            //set shipment details
                            //set shipment details(ship address)
                            JSONObject ship_address = response.getJSONObject("ship_address");
                            Address ship_addr = new Address();
                            ship_addr.setId(bill_address.getInt("id"));
                            ship_addr.setFull_name(bill_address.getString("full_name"));
                            ship_addr.setCompany(bill_address.getString("company"));
                            ship_addr.setAddress1(bill_address.getString("address1"));
                            try {
                                ship_addr.setAddress2(bill_address.getString("address2"));
                            } catch (Exception e) {
                                // do nothing again, the second street field may be empty
                            }
                            ship_addr.setCity(bill_address.getString("city"));
                            ship_addr.setZipcode(bill_address.getInt("zipcode"));

                            JSONObject ship_country = bill_address.getJSONObject("country"); //country details for the ship address
                            Country ship_ctry = new Country();
                            ship_ctry.setName(bill_country.getString("name"));
                            ship_ctry.setIso(bill_country.getString("iso"));
                            ship_addr.setCountry(bill_ctry);    //put country object to the ship object

                            JSONObject ship_state = bill_address.getJSONObject("state");     //state details for the ship address
                            State ship_st = new State();
                            ship_st.setName(bill_state.getString("name"));
                            ship_st.setAbbr(bill_state.getString("abbr"));
                            ship_addr.setState(bill_st);    //put state object to the bill object

                            order.setShip_address(ship_addr);   //finally put ship address in the order object


                            //parse Line item details
                            /** line items not need here for the time being
                             JSONArray items = response.getJSONArray("line_items");
                             for (int i = 0; i < items.length(); i++) {
                             LineItems item = new LineItems();
                             item.setId(items.getJSONObject(i).getInt("id"));
                             item.setName(items.getJSONObject(i).getJSONObject("variant").getString("name"));
                             item.setQuantity(items.getJSONObject(i).getInt("quantity"));
                             item.setDisplay_price(items.getJSONObject(i).getString("display_amount"));
                             item.setSingle_display_amount(items.getJSONObject(i).getString("single_display_amount"));
                             item.setSku((items.getJSONObject(i).getJSONObject("variant").getString("sku")));

                             String image_url = items.getJSONObject(i).getJSONObject("variant").getJSONArray("images").getJSONObject(0).getString("product_url");


                             item.setTemp_img(image_url);

                             lineItemsList.add(item);
                             }
                             **/

                            JSONArray shipments = response.getJSONArray("shipments");
                            for (int i = 0; i < shipments.length(); i++) {
                                Shipments shipment = new Shipments();
                                shipment.setNumber(shipments.getJSONObject(i).getString("number"));
                                shipment.setState(shipments.getJSONObject(i).getString("state"));
                                shipment.setStock_location_name(shipments.getJSONObject(i).getString("stock_location_name"));
                                shipmentsList.add(shipment);

                            }


                            shipmentsAdapter = new ShipmentsAdapter(shipmentsList, ship_addr, ShipmentsActivity.this);
                            recyclerView.setAdapter(shipmentsAdapter);
                            shipmentsAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        pDialog.hide();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(getApplicationContext().toString(), "Error: " + error.getMessage());
                pDialog.hide();
            }
        }) {

            /**
             * Passing request headers such as the Spree API Token
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Spree-Token", Config.API_KEY);
                return headers;
            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);


        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

            });

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && gestureDetector.onTouchEvent(e)) {
                    int position = rv.getChildAdapterPosition(child);
                    // Toast.makeText(getApplicationContext(), countries.get(position), Toast.LENGTH_SHORT).show();
                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }
}