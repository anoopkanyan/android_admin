package recode360.spreeadminapp.Activities.pos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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
    private Float totalPrice = 0.00f;
    private int totalQuantity = 0;
    private String order_no;

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
        totalPrice = intent.getFloatExtra("price", 0.00f);
        totalQuantity = intent.getIntExtra("quantity", 0);
        order_no = intent.getStringExtra("order_no");


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
        intent.putExtra("price", totalPrice);
        intent.putExtra("order_no", order_no);
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

                        updateStore();


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


        url = Config.URL_STORE + "/api/orders/" + order_no + "/payments?payment[payment_method_id]=1&payment[amount]=" + amount.toString() +
                "&token=" + Config.API_KEY;

        String tag_json_obj = "json_obj_req";


        final MaterialDialog dialog = new MaterialDialog.Builder(PaymentPosActivity.this)
                .content("Processing")
                .progress(true, 0)
                .titleColor(getResources().getColor(R.color.colorPrimaryDark))
                .widgetColor(getResources().getColor(R.color.colorAccent))
                .contentColor(getResources().getColor(R.color.colorPrimary))
                .autoDismiss(false)
                .cancelable(false)
                .show();


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("PAYMENT CREATED:", response.toString());
                        Intent intent = new Intent(PaymentPosActivity.this, AddCustomerActivity.class);
                        startActivity(intent);
                        dialog.cancel();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Paypal Payment", "Error: " + error.getMessage());
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

