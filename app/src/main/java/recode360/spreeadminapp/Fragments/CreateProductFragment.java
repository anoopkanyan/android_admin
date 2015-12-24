package recode360.spreeadminapp.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.math.BigDecimal;

import recode360.spreeadminapp.R;
import recode360.spreeadminapp.app.AppController;
import recode360.spreeadminapp.app.Config;

/**
 * Created by ANOOP on 10/29/2015.
 * Fragment to handle creation of a product
 */


public class CreateProductFragment extends Fragment {

    private Button btnCreateProduct;
    private EditText inputName, inputPrice, inputId;
    private TextInputLayout inputLayoutName, inputLayoutPrice, inputLayoutId;
    private String productName;
    private int productId;
    private BigDecimal productPrice;
    private String TAG;
    private String url = "https://rails-tutorial-anoopkanyan.c9.io/api/products?token=" + Config.API_KEY;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        TAG = AppController.class
                .getSimpleName();
        View view = inflater.inflate(R.layout.product_create_layout, container, false);

        inputLayoutName = (TextInputLayout) view.findViewById(R.id.input_layout_product_name);
        inputName = (EditText) view.findViewById(R.id.input_product_name);

        inputLayoutPrice = (TextInputLayout) view.findViewById(R.id.input_layout_product_price);
        inputPrice = (EditText) view.findViewById(R.id.input_product_price);

        inputLayoutId = (TextInputLayout) view.findViewById(R.id.input_layout_product_shipping_category_id);
        inputId = (EditText) view.findViewById(R.id.input_product_shipping_category_id);

        inputName.addTextChangedListener(new MyTextWatcher(inputName));
        inputPrice.addTextChangedListener(new MyTextWatcher(inputPrice));
        inputId.addTextChangedListener(new MyTextWatcher(inputId));

        btnCreateProduct = (Button) view.findViewById(R.id.btn_create_product);

        btnCreateProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });

        return view;
        //   return inflater.inflate(R.layout.product_create_layout, null);


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

        url = url + "&product[name]=" + inputName.getText().toString() + "&product[price]=" + inputPrice.getText().toString() + "&product[shipping_category_id]=" + inputId.getText().toString();

        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading...");
        pDialog.show();


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        pDialog.hide();
                        Toast.makeText(getActivity().getApplicationContext(), "Product Created Successfully", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                pDialog.hide();
            }
        });


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
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
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


}













