package recode360.spreeadminapp.Activities.pos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import recode360.spreeadminapp.R;
import recode360.spreeadminapp.app.AppController;
import recode360.spreeadminapp.app.Config;
import recode360.spreeadminapp.models.Product;


public class PaymentActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CardView creditCard;
    private CardView terminalCard;
    private CardView cashCard;
    private ArrayList<Product> items;
    private Float totalPrice = 0.00f;
    private int totalQuantity = 0;
    private String order_no;

    private String url;
    private String details;
    MaterialDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);


        Intent intent = this.getIntent();
        items = (ArrayList<Product>) intent.getSerializableExtra("products");
        totalPrice = intent.getFloatExtra("price", 0.00f);
        totalQuantity = intent.getIntExtra("quantity", 0);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Choose a Payment Method");


        creditCard = (CardView) findViewById(R.id.credit_card);
        cashCard = (CardView) findViewById(R.id.cash_card);
        terminalCard = (CardView) findViewById(R.id.terminal_card);


        cashCard.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                createCartOrder();
            }

        });

    }


    public void createCartOrder() {

        String username = "1";
        String password = "2";
        url = Config.URL_STORE + "/api/orders.json?token=" + Config.API_KEY;

        String tag_json_obj = "json_obj_req";
        // final String details = "{\"line_item\":{\"variant_id\":\"" + username + "\",\"quantity\":\"" + password + "\"}}";

        final String details_initial = "{\"order\": {\"line_items\": [";

        final String end = "]}}";

        String prev = "";

        for (int i = 0; i < items.size(); i++) {

            prev = prev + "{\"variant_id\":" + items.get(i).getId() + ", \"quantity\":" + items.get(i).getCart_qty() + "}";

            if (i != (items.size() - 1)) {
                prev = prev + ",";
            }
        }

        String details = details_initial + prev + end;

        JSONObject jsonBody = null;
        try {
            jsonBody = new JSONObject(details);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        dialog = new MaterialDialog.Builder(PaymentActivity.this)
                .title("New Order")
                .content("Creating your Order")
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .titleColor(getResources().getColor(R.color.colorPrimaryDark))
                .widgetColor(getResources().getColor(R.color.colorAccent))
                .contentColor(getResources().getColor(R.color.colorPrimary))
                .autoDismiss(false)
                .show();

        Log.d("Y O HO __", details);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, jsonBody,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Add line items to yoyo", response.toString());

                        try {
                            order_no = response.getString("number");

                            Intent intent = new Intent(PaymentActivity.this, CashPaymentActivity.class);
                            intent.putExtra("price", totalPrice);
                            intent.putExtra("order_no", order_no);
                            startActivity(intent);

                            dialog.hide();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Add line items to cart", "Error: " + error.getMessage());
                // Log.e(url, details);

                // hide the progress dialog
                //pDialog.hide();
                dialog.cancel();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        jsonObjReq.setShouldCache(false);

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);


    }

}

