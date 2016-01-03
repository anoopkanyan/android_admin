package recode360.spreeadminapp.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import recode360.spreeadminapp.R;
import recode360.spreeadminapp.adapter.LineItemsAdapter;
import recode360.spreeadminapp.adapter.SimpleRecyclerAdapter;
import recode360.spreeadminapp.app.AppController;
import recode360.spreeadminapp.app.Config;
import recode360.spreeadminapp.models.LineItems;
import recode360.spreeadminapp.models.Orders;
import recode360.spreeadminapp.models.sessions.AlertDialogManager;


public class OrdersActivity extends AppCompatActivity {

    CollapsingToolbarLayout collapsingToolbar;
    RecyclerView recyclerView;
    int mutedColor = R.attr.colorPrimary;
    SimpleRecyclerAdapter simpleRecyclerAdapter;

    private String order_no;   //order number passed by the calling activity
    private Toolbar toolbar;
    private JsonObjectRequest jsonObjReq;

    private Orders order;   //order header

    private List<LineItems> lineItemsList;
    private LineItemsAdapter adapter;

    private RecyclerView recList;


    AlertDialogManager alert;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get the order no
        Intent intent = getIntent();
        order_no = intent.getStringExtra("order_no");

        setContentView(R.layout.activity_orders);

        //get the toolbar and set order number as it's title
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(order_no);


        //to show status of the process of updating the product
        alert = new AlertDialogManager();


        recList = (RecyclerView) findViewById(R.id.cardLineItemsList);
        LinearLayoutManager llm = new LinearLayoutManager(getBaseContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        order = new Orders();
        lineItemsList = new ArrayList<LineItems>();

        String tag_json_obj = "json_obj_req";
        String url = Config.URL_STORE + "api/orders/" + order_no + ".json";
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

                            //parse Line item details
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

                            adapter = new LineItemsAdapter(order,lineItemsList, OrdersActivity.this);
                            recList.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

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


    }


    private void fillData(String url) {

        ImageLoader imageLoader = AppController.getInstance().getImageLoader();
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView thumbNail = (NetworkImageView) findViewById(R.id.img_thumbnail);


        if (url != null) {
            thumbNail.setImageUrl(Config.URL_STORE + url, imageLoader);
            thumbNail.setBackgroundColor(Color.BLACK);
        }
    }


    //not to show menu items yet
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    */


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
