package recode360.spreeadminapp.Activities.pos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalItem;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalPaymentDetails;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import recode360.spreeadminapp.R;
import recode360.spreeadminapp.app.AppController;
import recode360.spreeadminapp.app.Config;
import recode360.spreeadminapp.models.Product;


public class PaymentPosActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CardView creditCard;
    private CardView terminalCard;
    private CardView cashCard;
    private TextView cart_total;
    private ArrayList<Product> items;
    private BigDecimal totalPrice = BigDecimal.ZERO;
    private int totalQuantity = 0;
    private String order_no;
    private String order_state;

    private Boolean isShipment;

    private String url;
    private String details;
    MaterialDialog dialog;
    BigDecimal amount;


    // To store the products those are added to cart
    private List<PayPalItem> productsInCart = new ArrayList<PayPalItem>();

    // PayPal configuration
    private static PayPalConfiguration paypalConfig = new PayPalConfiguration()
            .environment(Config.PAYPAL_ENVIRONMENT).clientId(
                    Config.PAYPAL_CLIENT_ID);

    private static final int REQUEST_CODE_PAYMENT = 1;
    public static final String TAG = AppController.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Intent intent = this.getIntent();
        items = (ArrayList<Product>) intent.getSerializableExtra("products");
        totalPrice = new BigDecimal(intent.getStringExtra("price"));
        totalQuantity = intent.getIntExtra("quantity", 0);
        order_no = intent.getStringExtra("order_no");
        isShipment = intent.getBooleanExtra("isShipment", false);


        for (int i = 0; i < items.size(); i++) {

            Product product = items.get(i);
            PayPalItem item = new PayPalItem(product.getName(), product.getCart_qty(),
                    product.getPrice(), Config.DEFAULT_CURRENCY, "87965544554");
            productsInCart.add(item);
        }

        // Starting PayPal service
        Intent intent1 = new Intent(this, PayPalService.class);
        intent1.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);
        startService(intent1);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        creditCard = (CardView) findViewById(R.id.credit_card);
        cashCard = (CardView) findViewById(R.id.cash_card);
        terminalCard = (CardView) findViewById(R.id.terminal_card);
        cart_total = (TextView) findViewById(R.id.cart_item_price);
        cart_total.setText("$" + totalPrice.toString());


        creditCard.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                startCardPayment();
            }

        });


        cashCard.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                startCashPayment();

            }

        });

    }


    public void startCashPayment() {

        Log.d("TOTAL PRICE IS", totalPrice.toString());
        Intent intent = new Intent(PaymentPosActivity.this, CashPaymentActivity.class);
        intent.putExtra("price", totalPrice.toString());
        intent.putExtra("order_no", order_no);
        intent.putExtra("isShipment", isShipment);
        startActivity(intent);

    }

    public void startCardPayment() {

        launchPayPalPayment();

    }


    //creates a cart for PayPal setting the cart total, tax and shipping costs
    private PayPalPayment prepareFinalCart() {

        PayPalItem[] items = new PayPalItem[productsInCart.size()];
        items = productsInCart.toArray(items);

        // Total amount
        BigDecimal subtotal = PayPalItem.getItemTotal(items);

        // If you have shipping cost, add it here
        BigDecimal shipping = new BigDecimal("0.0");

        // If you have tax, add it here
        BigDecimal tax = new BigDecimal("0.0");

        PayPalPaymentDetails paymentDetails = new PayPalPaymentDetails(
                shipping, subtotal, tax);

        BigDecimal amount = subtotal.add(shipping).add(tax);
        this.amount = amount;

        PayPalPayment payment = new PayPalPayment(
                amount,
                Config.DEFAULT_CURRENCY,
                "Description about transaction. This will be displayed to the user.",
                Config.PAYMENT_INTENT);

        payment.items(items).paymentDetails(paymentDetails);

        // Custom field like invoice_number etc.,
        payment.custom("This is text that will be associated with the payment that the app can use.");

        return payment;
    }


    private void launchPayPalPayment() {

        PayPalPayment thingsToBuy = prepareFinalCart();

        Intent intent = new Intent(PaymentPosActivity.this, PaymentActivity.class);

        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);

        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingsToBuy);

        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }


    /**
     * Receiving the PalPay payment response
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data
                        .getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        Log.e(TAG, confirm.toJSONObject().toString(4));
                        Log.e(TAG, confirm.getPayment().toJSONObject()
                                .toString(4));

                        String paymentId = confirm.toJSONObject()
                                .getJSONObject("response").getString("id");

                        String payment_client = confirm.getPayment()
                                .toJSONObject().toString();

                        Log.e(TAG, "paymentId: " + paymentId
                                + ", payment_json: " + payment_client);

                        // Now verify the payment on the server side
                        //verifyPaymentOnServer(paymentId, payment_client);


                        //update backend about the payment and show activity for user

                        if (isShipment) {
                            updateStore();

                        } else {
                            updateState();
                        }

                    } catch (JSONException e) {
                        Log.e(TAG, "an extremely unlikely failure occurred: ",
                                e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.e(TAG, "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.e(TAG,
                        "An invalid Payment or PayPalConfiguration was submitted.");
            }
        }
    }


    private void updateStore() {

        String details = "{\"order\": {\"payments_attributes\": [{\"payment_method_id\": \"3\"}]},\"payment_source\": {\"3\": {}}}";

        JSONObject jsonBody = null;
        try {
            jsonBody = new JSONObject(details);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        String tag_json_obj = "order_update_request";
        String url = Config.URL_STORE + "/api/checkouts/" + order_no + ".json?token=" + Config.API_KEY;


        final MaterialDialog pDialog = new MaterialDialog.Builder(this)
                .content("Recording payment")
                .widgetColor(getResources().getColor(R.color.colorAccent))
                .contentColor(getResources().getColor(R.color.colorPrimary))
                .progress(true, 0)
                .show();


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT,
                url, jsonBody,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Adding payment", response.toString());

                        try {
                            order_state = response.getString("state");
                            String payment_id = response.getJSONArray("payments").getJSONObject(0).getString("id");
                            pDialog.hide();

                            capturePayments(payment_id);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // Log.e(url, details);

                // hide the progress dialog
                //pDialog.hide();
                pDialog.cancel();
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


    public void updateState() {
        String tag_json_obj = "Checkouts";
        String url = Config.URL_STORE + "/api/checkouts/" + order_no + "/next.json?token=" + Config.API_KEY;

        final MaterialDialog pDialog = new MaterialDialog.Builder(this)
                .content("Recording payment")
                .widgetColor(getResources().getColor(R.color.colorAccent))
                .contentColor(getResources().getColor(R.color.colorPrimary))
                .progress(true, 0)
                .show();

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.PUT,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            order_state = response.getString("state");

                            Log.d(order_state, response.toString());

                            if (order_state.toString().equals("payment")) {
                                updateStore();

                            } else if (order_state.toString().equals("complete")) {


                            } else {
                                //keep on updating the state with default values
                                updateState();

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        pDialog.hide();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Cash Payment Activity", "Error: " + error.getMessage());
                pDialog.hide();


            }
        }) {

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req, tag_json_obj);
    }


    //makes a request to capture a Cash Payment, so that the order appears in the list of Orders
    public void capturePayments(String payment_id) {

        String tag_json_obj = "Payment Capture";
        String url = Config.URL_STORE + "/api/orders/" + order_no + "/payments/" + payment_id + "/capture?token=" + Config.API_KEY;

        final MaterialDialog pDialog = new MaterialDialog.Builder(this)
                .content("Recording payment")
                .widgetColor(getResources().getColor(R.color.colorAccent))
                .contentColor(getResources().getColor(R.color.colorPrimary))
                .progress(true, 0)
                .show();

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.PUT,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        pDialog.hide();
                        //show dialog if the payment is successful
                        Log.d(order_state, response.toString());

                        MaterialDialog dialog = new MaterialDialog.Builder(PaymentPosActivity.this)
                                .title(order_no.toString())
                                .titleColor(getResources().getColor(R.color.colorPrimaryDark))
                                .content("Sale recorded successfully.")
                                .positiveColor(getResources().getColor(R.color.colorAccent))
                                .positiveText("Ok")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                        Intent intent = new Intent(PaymentPosActivity.this, AddCustomerActivity.class);
                                        intent.putExtra("order_no", order_no);
                                        startActivity(intent);

                                    }
                                })
                                .show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Cash Payment Activity", "Error: " + error.getMessage());
                pDialog.hide();


            }
        }) {

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req, tag_json_obj);


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




