package recode360.spreeadminapp.Activities.pos;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
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

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import recode360.spreeadminapp.Activities.MainActivity;
import recode360.spreeadminapp.R;
import recode360.spreeadminapp.app.AppController;
import recode360.spreeadminapp.app.Config;

public class CashPaymentActivity extends AppCompatActivity {

    private String order_no;
    private Boolean isShipment;
    private String order_state;
    private BigDecimal totalPrice;
    private EditText totalPriceView;
    private BigDecimal totalPaid, changeAmt;
    private Toolbar toolbar;
    private Button payButton;
    private double parsed;
    private boolean changed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_payment);

        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);


        totalPriceView = (EditText) findViewById(R.id.cash_price);
        totalPriceView.setRawInputType(Configuration.KEYBOARD_12KEY);


        Intent intent = this.getIntent();
        order_no = intent.getStringExtra("order_no");
        totalPrice = new BigDecimal(intent.getStringExtra("price"));
        isShipment = intent.getBooleanExtra("isShipment", false);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("PAY $" + totalPrice.toString());


        totalPriceView.setText("$" + totalPrice.toString());
        totalPriceView.setSelection(totalPriceView.getText().length());


        totalPriceView.addTextChangedListener(new TextWatcher() {

            int count = -1;
            private String current = "";

            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }


            @Override
            public void afterTextChanged(Editable s) {

                changed = true;

                if (!s.toString().equals(current)) {
                    totalPriceView.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[$,.]", "");

                    parsed = Double.parseDouble(cleanString);
                    String formatted = NumberFormat.getCurrencyInstance().format((parsed / 100));

                    current = formatted;
                    totalPriceView.setText(formatted);
                    totalPriceView.setSelection(formatted.length());

                    totalPriceView.addTextChangedListener(this);
                }

            }


        });

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
                                addresstoDelivery();

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


    public void addresstoDelivery() {

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_payment, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_proceed:

                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(totalPriceView.getWindowToken(), 0);

                if (changed) {
                    totalPaid = new BigDecimal(parsed);
                    totalPaid = totalPaid.divide(new BigDecimal(100));
                } else {
                    totalPaid = totalPrice;

                }

                changeAmt = totalPaid.subtract(totalPrice);

                if (changeAmt.compareTo(BigDecimal.ZERO) != -1) {

                    MaterialDialog dialog = new MaterialDialog.Builder(CashPaymentActivity.this)
                            .title("Cash Payment")
                            .titleColor(getResources().getColor(R.color.colorPrimaryDark))
                            .content("Total Paid     $" + totalPaid + "\n" + "Return           $" + changeAmt.toString().substring(0, changeAmt.toString().indexOf(".") + 2))
                            .positiveText("CONFIRM")
                            .positiveColor(getResources().getColor(R.color.accent))
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    dialog.dismiss();

                                    if (isShipment) {
                                        addresstoDelivery();
                                    } else {
                                        updateState();
                                    }

                                }
                            })
                            .negativeText("CANCEL")
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    dialog.dismiss();
                                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                                }
                            })
                            .show();
                } else {
                    MaterialDialog dialog = new MaterialDialog.Builder(this)
                            .title("Stop")
                            .content("Cash paid must be greater than or equal to the total bill.")
                            .positiveText("Ok")
                            .positiveColor(getResources().getColor(R.color.accent))
                            .titleColor(getResources().getColor(R.color.accent))
                            .show();

                }

                return true;

            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                super.onBackPressed();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
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

                        MaterialDialog dialog = new MaterialDialog.Builder(CashPaymentActivity.this)
                                .title(order_no.toString())
                                .titleColor(getResources().getColor(R.color.colorPrimaryDark))
                                .content("Sale recorded successfully.")
                                .positiveColor(getResources().getColor(R.color.colorAccent))
                                .positiveText("Ok")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                        if (isShipment) {
                                            Intent intent = new Intent(CashPaymentActivity.this, MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                        } else {

                                            Intent intent = new Intent(CashPaymentActivity.this, AddCustomerActivity.class);
                                            intent.putExtra("order_no", order_no);
                                            startActivity(intent);

                                        }

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


}


