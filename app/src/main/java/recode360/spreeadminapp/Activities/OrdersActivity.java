package recode360.spreeadminapp.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import recode360.spreeadminapp.R;
import recode360.spreeadminapp.adapter.LineItemsAdapter;
import recode360.spreeadminapp.app.AppController;
import recode360.spreeadminapp.app.Config;
import recode360.spreeadminapp.models.Address;
import recode360.spreeadminapp.models.Country;
import recode360.spreeadminapp.models.LineItems;
import recode360.spreeadminapp.models.Orders;
import recode360.spreeadminapp.models.State;
import recode360.spreeadminapp.models.sessions.AlertDialogManager;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;


//works with orders which have shipment and payment details
public class OrdersActivity extends AppCompatActivity {


    private String order_no;   //order number passed by the calling activity
    private String shipment;    //to make a distinction between shipped and pending products

    private Toolbar toolbar;
    private JsonObjectRequest jsonObjReq;

    FloatingActionMenu menu1;

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
        shipment = intent.getStringExtra("shipment");

        setContentView(R.layout.activity_orders);

        //get the toolbar and set order number as it's title
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(order_no);


        alert = new AlertDialogManager();


        recList = (RecyclerView) findViewById(R.id.cardLineItemsList);
        LinearLayoutManager llm = new LinearLayoutManager(getBaseContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        menu1 = (FloatingActionMenu) findViewById(R.id.menu1);
        menu1.hideMenuButton(false);
        menu1.showMenuButton(true);

        final FloatingActionMenu fab = (FloatingActionMenu) findViewById(R.id.menu1);
        fab.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                int drawableId;
                if (opened) {
                    drawableId = R.drawable.ic_add;
                } else {
                    drawableId = R.drawable.ic_menu;
                }
                Drawable drawable = getResources().getDrawable(drawableId);
                fab.getMenuIconView().setImageDrawable(drawable);
            }
        });


        final FloatingActionButton programFab1 = new FloatingActionButton(this);
        final FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        final FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);

        programFab1.setButtonSize(FloatingActionButton.SIZE_MINI);
        programFab1.setLabelText("Mark Shipped");
        programFab1.setImageResource(R.drawable.ic_done);
        programFab1.setColorNormalResId(R.color.colorPrimary);
        programFab1.setColorPressedResId(R.color.colorPrimaryDark);
        programFab1.setColorRippleResId(R.color.colorPrimaryDark);


        //this is store details related to the whole order
        order = new Orders();

        //this is to store details related to all the line items(products) in a particular order
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

                            //set payment details(bill address)
                            JSONObject bill_address = response.getJSONObject("bill_address");
                            Address bill_addr = new Address();
                            bill_addr.setId(bill_address.getInt("id"));
                            bill_addr.setFull_name(bill_address.getString("full_name"));
                            bill_addr.setAddress1(bill_address.getString("address1"));
                            bill_addr.setAddress2(bill_address.getString("address2"));
                            bill_addr.setCity(bill_address.getString("city"));

                            JSONObject bill_country = bill_address.getJSONObject("country"); //country details for the bill address
                            Country bill_ctry = new Country();
                            bill_ctry.setName(bill_country.getString("name"));
                            bill_addr.setCountry(bill_ctry);    //put country object to the bill object

                            JSONObject bill_state = bill_address.getJSONObject("state");     //state details for the bill address
                            State bill_st = new State();
                            bill_st.setName(bill_state.getString("name"));
                            bill_addr.setState(bill_st);    //put state object to the bill object

                            order.setBill_address(bill_addr);   //finally put bill address in the order object


                            //set shipment details
                            //set shipment details(ship address)
                            JSONObject ship_address = response.getJSONObject("ship_address");
                            Address ship_addr = new Address();
                            ship_addr.setId(bill_address.getInt("id"));
                            ship_addr.setFull_name(bill_address.getString("full_name"));
                            ship_addr.setAddress1(bill_address.getString("address1"));
                            ship_addr.setAddress2(bill_address.getString("address2"));
                            ship_addr.setCity(bill_address.getString("city"));

                            JSONObject ship_country = bill_address.getJSONObject("country"); //country details for the ship address
                            Country ship_ctry = new Country();
                            ship_ctry.setName(bill_country.getString("name"));
                            ship_addr.setCountry(bill_ctry);    //put country object to the ship object

                            JSONObject ship_state = bill_address.getJSONObject("state");     //state details for the ship address
                            State ship_st = new State();
                            ship_st.setName(bill_state.getString("name"));
                            ship_addr.setState(bill_st);    //put state object to the bill object

                            order.setShip_address(ship_addr);   //finally put ship address in the order object


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

                            adapter = new LineItemsAdapter(order, lineItemsList, OrdersActivity.this);
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


        //set button if shipment is pending, otherwise not

        if (shipment.equals("ready")) {

            menu1.addMenuButton(programFab1);
            programFab1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.showAlertDialog(OrdersActivity.this, "Bill Details", "Name:  " + order.getBill_address().getFull_name() + "\nAddress:  " + order.getBill_address().getAddress1() + " " + order.getBill_address().getAddress2()
                        + "\nCity:  " + order.getBill_address().getCity() + "\nState:  " + order.getBill_address().getState().getName() + "\nCountry:  " + order.getBill_address().getCountry().getName(), true);
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.showAlertDialog(OrdersActivity.this, "Shipment Details", "Name:  " + order.getShip_address().getFull_name() + "\nAddress:  " + order.getShip_address().getAddress1() + " " + order.getShip_address().getAddress2()
                        + "\nCity:  " + order.getBill_address().getCity() + "\nState:  " + order.getShip_address().getState().getName() + "\nCountry:  " + order.getShip_address().getCountry().getName(), true);
            }
        });


        menu1.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (menu1.isOpened()) {
                    //nothing to do here yet
                    //    Toast.makeText(OrdersActivity.this, menu1.getMenuButtonLabelText(), Toast.LENGTH_SHORT).show();
                }

                menu1.toggle(true);
            }
        });

        //   presentShowcaseView(500);

    }


    private void fillData(String url) {

        ImageLoader imageLoader = AppController.getInstance().getImageLoader();
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView thumbNail = (NetworkImageView) findViewById(R.id.img_thumbnail);


        if (URLUtil.isValidUrl(url)) {
            thumbNail.setImageUrl(url, imageLoader);
            thumbNail.setBackgroundColor(Color.BLACK);
        } else {
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

    private void presentShowcaseView(int withDelay) {
        // sequence example
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(OrdersActivity.this, "qwe");

        sequence.setConfig(config);

        sequence.addSequenceItem(menu1,
                "Use this button to get Sipping and Billing Details", "GOT IT");


        sequence.start();
    }


}
