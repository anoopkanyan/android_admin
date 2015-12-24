package recode360.spreeadminapp.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import recode360.spreeadminapp.R;
import recode360.spreeadminapp.adapter.SimpleRecyclerAdapter;
import recode360.spreeadminapp.app.AppController;
import recode360.spreeadminapp.app.Config;
import recode360.spreeadminapp.models.DetailedProduct;
import recode360.spreeadminapp.models.sessions.AlertDialogManager;
import recode360.spreeadminapp.utils.CustomRequest;

/**
 * Created by ANOOP on 12/21/2015.
 */
public class OrdersActivity extends AppCompatActivity {

    CollapsingToolbarLayout collapsingToolbar;
    RecyclerView recyclerView;
    int mutedColor = R.attr.colorPrimary;
    SimpleRecyclerAdapter simpleRecyclerAdapter;

    private int product_id;
    private Toolbar toolbar;
    private JsonObjectRequest jsonObjReq;
    private DetailedProduct product;


    private EditText editName;
    private EditText editPrice;
    private EditText editSku;
    private EditText editCostPrice;
    private EditText editDescription;
    private EditText editWeight;
    private EditText editHeight;
    private EditText editDepth;
    private EditText editWidth;
    private ImageView header;

    private Button editProductButton;
    private FloatingActionButton imageButton;
    AlertDialogManager alert;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        product_id = intent.getIntExtra("product_id", 0);

        setContentView(R.layout.activity_orders);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //to show status of the process of updating the product
        alert = new AlertDialogManager();


        String tag_json_obj = "json_obj_req";

        String url = Config.URL_STORE + "/api/products/" + product_id + ".json";
        Log.d("the url of the store is", url);

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();


        //Request product details
        jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(getApplicationContext().toString(), response.toString());


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

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);



    }


    private void fillData(DetailedProduct pro) {

        //set the image of the product at the top be extracting the image URL
        ImageLoader imageLoader = AppController.getInstance().getImageLoader();
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView thumbNail = (NetworkImageView) findViewById(R.id.header);

        //  Log.d("image", pro.getImages()[0].getMini_url());

        if (pro.getImages() != null) {
            thumbNail.setImageUrl(Config.URL_STORE + pro.getImages()[0].getLarge_url(), imageLoader);
            thumbNail.setBackgroundColor(Color.BLACK);
        }
    }


    //get the details of the edited fields and make a PUT request to save them
    private void submitForm() {


        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        String url = Config.URL_STORE + "api/products/" + product_id;

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        CustomRequest jsonObjReq = new CustomRequest(Request.Method.PUT,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("OnSubmitResponse", response.toString());
                        pDialog.hide();
                        alert.showAlertDialog(OrdersActivity.this, "Success..", "Product edited successfully", true);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("OnSubmitError", "Error: " + error.getMessage());
                pDialog.hide();
                alert.showAlertDialog(OrdersActivity.this, "Error..", "Editing product did not succeed", false);
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

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("product[name]", editName.getText().toString());
                params.put("product[price]", editPrice.getText().toString());
                params.put("product[sku]", editSku.getText().toString());
                params.put("product[cost_price]", editCostPrice.getText().toString());
                params.put("product[description]", editDescription.getText().toString());
                params.put("product[weight]", editWeight.getText().toString());
                params.put("product[height]", editHeight.getText().toString());
                params.put("product[depth]", editDepth.getText().toString());
                params.put("product[width]", editWidth.getText().toString());

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);


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
