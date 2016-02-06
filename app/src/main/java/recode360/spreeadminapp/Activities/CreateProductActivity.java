package recode360.spreeadminapp.Activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import recode360.spreeadminapp.R;
import recode360.spreeadminapp.app.AppController;
import recode360.spreeadminapp.app.Config;
import recode360.spreeadminapp.utils.CustomRequest;

/**
 * Activity for creating a new product
 */
public class CreateProductActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Button btnCreateProduct;
    private EditText inputName, inputPrice, inputId;
    private TextInputLayout inputLayoutName, inputLayoutPrice, inputLayoutId;
    private String productName;
    private int productId;
    private BigDecimal productPrice;
    private String TAG;
    private String url = Config.URL_STORE + "/api/products" ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TAG = AppController.class
                .getSimpleName();

        setContentView(R.layout.activity_create_product);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New Product");

        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_product_name);
        inputName = (EditText) findViewById(R.id.input_product_name);

        inputLayoutPrice = (TextInputLayout) findViewById(R.id.input_layout_product_price);
        inputPrice = (EditText) findViewById(R.id.input_product_price);

        inputLayoutId = (TextInputLayout) findViewById(R.id.input_layout_product_shipping_category_id);
        inputId = (EditText) findViewById(R.id.input_product_shipping_category_id);

        inputName.addTextChangedListener(new MyTextWatcher(inputName));
        inputPrice.addTextChangedListener(new MyTextWatcher(inputPrice));
        inputId.addTextChangedListener(new MyTextWatcher(inputId));

        btnCreateProduct = (Button) findViewById(R.id.btn_create_product);

        btnCreateProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });

    }


    void submitForm() {


        if (!validateName()) {
            return;
        }

        if (!validatePrice()) {
            return;
        }

        if (!validateId()) {
            return;
        }

        String tag_json_obj = "json_obj_req";



        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();


        CustomRequest jsonObjReq = new CustomRequest(Request.Method.POST,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        pDialog.hide();
                        Toast.makeText(getApplicationContext(), "Product Created Successfully", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                pDialog.hide();
            }
        })
        {

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("X-Spree-Token", Config.API_KEY);
            return headers;
        }

        @Override
        protected Map<String, String> getParams() {
            Map<String, String> params = new HashMap<String, String>();
            params.put("product[name]", inputName.getText().toString());
            params.put("product[price]", inputPrice.getText().toString());
            params.put("product[shipping_category_id]", inputId.getText().toString());

            return params;
        }
        };






// Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);


    }


    private boolean validateName() {
        if (inputName.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError(getString(R.string.err_msg_name_product));
            requestFocus(inputName);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePrice() {
        if (inputPrice.getText().toString().trim().isEmpty()) {
            inputLayoutPrice.setError(getString(R.string.err_msg_product_price));
            requestFocus(inputPrice);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateId() {
        if (inputId.getText().toString().trim().isEmpty()) {
            inputLayoutId.setError(getString(R.string.err_msg_product_id));
            requestFocus(inputId);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_product_name:
                    validateName();
                    break;
                case R.id.input_product_price:
                    validatePrice();
                    break;
                case R.id.input_product_shipping_category_id:
                    validateId();
                    break;
            }
        }
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
