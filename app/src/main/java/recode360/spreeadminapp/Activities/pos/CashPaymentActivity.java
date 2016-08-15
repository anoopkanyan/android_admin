package recode360.spreeadminapp.Activities.pos;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import java.util.HashMap;
import java.util.Map;

import recode360.spreeadminapp.R;
import recode360.spreeadminapp.app.AppController;
import recode360.spreeadminapp.app.Config;

public class CashPaymentActivity extends AppCompatActivity {

    private String order_no;
    private String order_state;
    private Float totalPrice;
    private EditText totalPriceView;
    private Float totalPaid, changeAmt;
    private Toolbar toolbar;
    private Button payButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_payment);

        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);


        totalPriceView = (EditText) findViewById(R.id.cash_price);


        Intent intent = this.getIntent();
        order_no = intent.getStringExtra("order_no");
        totalPrice = intent.getFloatExtra("price", 0.00f);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("PAY $" + totalPrice.toString());


        totalPriceView.setText("0.00");
        totalPriceView.setSelection(4);


        totalPriceView.addTextChangedListener(new TextWatcher() {

            int count = -1;

            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            public void afterTextChanged(Editable arg0) {
                if (arg0.length() > 0) {
                    String str = totalPriceView.getText().toString();
                    totalPriceView.setOnKeyListener(new View.OnKeyListener() {
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            if (keyCode == KeyEvent.KEYCODE_DEL) {
                                count--;
                                InputFilter[] fArray = new InputFilter[1];
                                fArray[0] = new InputFilter.LengthFilter(100);
                                totalPriceView.setFilters(fArray);
                                //change the edittext's maximum length to 100.
                                //If we didn't change this the edittext's maximum length will
                                //be number of digits we previously entered.
                            }
                            return false;
                        }
                    });
                    char t = str.charAt(arg0.length() - 1);
                    if (t == '.') {
                        count = 0;
                    }
                    if (count >= 0) {
                        if (count == 2) {
                            InputFilter[] fArray = new InputFilter[1];
                            fArray[0] = new InputFilter.LengthFilter(arg0.length());
                            totalPriceView.setFilters(fArray);
                            //prevent the edittext from accessing digits
                            //by setting maximum length as total number of digits we typed till now.
                        }
                        count++;
                    }
                }
            }
        });

    }

    //updates the Order and moves from the cart state to the Address state
    public void cartToAddress() {
        addresstoDelivery();
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

                            if (order_state.toString().equals("address")) {
                                addresstoDelivery();
                            } else if (order_state.toString().equals("complete")) {

                                MaterialDialog dialog = new MaterialDialog.Builder(CashPaymentActivity.this)
                                        .title(order_no.toString())
                                        .titleColor(getResources().getColor(R.color.colorPrimaryDark))
                                        .content("Sale recorded successfully.")
                                        .positiveColor(getResources().getColor(R.color.colorAccent))
                                        .positiveText("Ok")
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                                Intent intent = new Intent(CashPaymentActivity.this, AddCustomerActivity.class);
                                                intent.putExtra("order_no", order_no);
                                                startActivity(intent);

                                            }
                                        })
                                        .show();


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


                MaterialDialog dialog = new MaterialDialog.Builder(CashPaymentActivity.this)
                        .title(order_no.toString())
                        .titleColor(getResources().getColor(R.color.colorPrimaryDark))
                        .content("Sale recorded successfully.")
                        .positiveColor(getResources().getColor(R.color.colorAccent))
                        .positiveText("Ok")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                Intent intent = new Intent(CashPaymentActivity.this, AddCustomerActivity.class);
                                intent.putExtra("order_no", order_no);
                                startActivity(intent);

                            }
                        })
                        .show();



            }
        }) {

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req, tag_json_obj);
    }


    public void addresstoDelivery() {

        Log.d("Chal raha hai", "shayad chal hi raha hai");

        String details = "{\"order\": {\"payments_attributes\": [{\"payment_method_id\": \"2\"}]},\"payment_source\": {\"2\": {}}}";


        JSONObject jsonBody = null;
        try {
            jsonBody = new JSONObject(details);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        String tag_json_obj = "order_update_request";
        String url = Config.URL_STORE + "/api/orders/" + order_no + ".json?token=" + Config.API_KEY;


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
                            updateState();
                            pDialog.hide();
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
                totalPaid = Float.parseFloat(totalPriceView.getText().toString());

                changeAmt = totalPaid - totalPrice;
                MaterialDialog dialog = new MaterialDialog.Builder(CashPaymentActivity.this)
                        .title("Cash Payment")
                        .titleColor(getResources().getColor(R.color.colorPrimaryDark))
                        .content("Total Paid     $" + totalPaid + "\n" + "Return         $" + changeAmt.toString().substring(0, changeAmt.toString().indexOf(".") + 2))
                        .positiveText("CONFIRM")
                        .positiveColor(getResources().getColor(R.color.accent))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                Log.d("this is", "dailog clicked");
                                dialog.dismiss();
                                addresstoDelivery();

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

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


}


