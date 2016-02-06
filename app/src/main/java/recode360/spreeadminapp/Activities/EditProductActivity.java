package recode360.spreeadminapp.Activities;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.cocosw.bottomsheet.BottomSheet;
import com.github.clans.fab.FloatingActionMenu;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import recode360.spreeadminapp.R;
import recode360.spreeadminapp.adapter.SimpleRecyclerAdapter;
import recode360.spreeadminapp.app.AppController;
import recode360.spreeadminapp.app.Config;
import recode360.spreeadminapp.models.DetailedProduct;
import recode360.spreeadminapp.models.sessions.AlertDialogManager;
import recode360.spreeadminapp.utils.CustomRequest;
import recode360.spreeadminapp.utils.Utils;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class EditProductActivity extends AppCompatActivity {
    CollapsingToolbarLayout collapsingToolbar;
    RecyclerView recyclerView;
    int mutedColor = R.attr.colorPrimary;
    SimpleRecyclerAdapter simpleRecyclerAdapter;
    private Toolbar toolbar;

    private List<FloatingActionMenu> menus = new ArrayList<>();
    private Handler mUiHandler = new Handler();

    private int product_id;
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

    private int camera_int;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        product_id = intent.getIntExtra("product_id", 0);

        setContentView(R.layout.activity_edit_product);

        toolbar = (Toolbar) findViewById(R.id.anim_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        //to show status of the process of updating the product
        alert = new AlertDialogManager();


        //editable text-options
        editName = (EditText) findViewById(R.id.editName);
        editPrice = (EditText) findViewById(R.id.editPrice);
        editSku = (EditText) findViewById(R.id.editSku);
        editCostPrice = (EditText) findViewById(R.id.editCostPrice);
        editDescription = (EditText) findViewById(R.id.editDescription);
        editWeight = (EditText) findViewById(R.id.editWeight);
        editHeight = (EditText) findViewById(R.id.editHeight);
        editDepth = (EditText) findViewById(R.id.editDepth);
        editWidth = (EditText) findViewById(R.id.editWidth);


        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        if (  sp.getString("pref_barcode_camera", "0").equals("0")){

            camera_int = 0;
        }else{
            camera_int = 1;
        }


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

                        //save the JSONObject data in DetailedProduct Object
                        product = Utils.parseProduct(response);

                        //set the product name as the title of the product
                        collapsingToolbar.setTitle(product.getName());

                        //auto fill the editable details
                        fillData(product);
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


        imageButton = (FloatingActionButton) findViewById(R.id.imageUploadButton);


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new BottomSheet.Builder(EditProductActivity.this).title("Choose Action").sheet(R.menu.list).listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case R.id.scan:
                                //Barcode scanner activity launched here to get the SKU
                                IntentIntegrator integrator = new IntentIntegrator(EditProductActivity.this);
                                integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
                                integrator.setPrompt("Scan a barcode");
                                integrator.setBeepEnabled(true);
                                integrator.setOrientationLocked(true);
                                integrator.setCameraId(camera_int);  // Use a specific camera of the device
                                integrator.initiateScan();
                                break;

                            case R.id.upload:
                                //activity for image upload launched here
                                Intent intent = new Intent(EditProductActivity.this, ImageActivity.class);
                                intent.putExtra("product_id", Integer.toString(product_id));
                                startActivity(intent);
                        }
                    }
                }).show();


                Log.d("test", "Scan button works!");


            }
        });

        editProductButton = (Button) findViewById(R.id.btn_edit_product);
        editProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });

        presentShowcaseView(500);


    }


    private void fillData(DetailedProduct pro) {
        //fill in the product details which can be updated
        editName.setText(pro.getName());
        editPrice.setText(pro.getPrice().toString());

        editDescription.setText(Utils.stripHtml(pro.getDescription())); //strip HTML tags and put the data in description.
        editSku.setText(pro.getSku());

        try {
            editCostPrice.setText(pro.getCost_price().toString());
        } catch (Exception e) {
            editCostPrice.setText("0.0");
        }

        if (pro.getWeight() != null) {
            editWeight.setText(pro.getWeight().toString());
        } else {
            editWeight.setText("0.0");
        }

        if (pro.getHeight() != null) {
            editWeight.setText(pro.getWeight().toString());
        } else {
            editHeight.setText("0.0");
        }

        if (pro.getDepth() != null) {
            editWeight.setText(pro.getWeight().toString());
        } else {
            editDepth.setText("0.0");
        }

        if (pro.getWidth() != null) {
            editWeight.setText(pro.getWeight().toString());
        } else {
            editWidth.setText("0.0");
        }

        //set the image of the product at the top be extracting the image URL
        ImageLoader imageLoader = AppController.getInstance().getImageLoader();
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView thumbNail = (NetworkImageView) findViewById(R.id.header);

        //  Log.d("image", pro.getImages()[0].getMini_url());

        if (pro.getImages() != null) {
            if (URLUtil.isValidUrl(pro.getImages()[0].getLarge_url())) {
                thumbNail.setImageUrl(pro.getImages()[0].getLarge_url(), imageLoader);
                thumbNail.setBackgroundColor(Color.BLACK);
            } else {
                thumbNail.setImageUrl(Config.URL_STORE + pro.getImages()[0].getLarge_url(), imageLoader);
                thumbNail.setBackgroundColor(Color.BLACK);
            }
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
                        alert.showAlertDialog(EditProductActivity.this, "Success..", "Product edited successfully", true);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("OnSubmitError", "Error: " + error.getMessage());
                pDialog.hide();
                alert.showAlertDialog(EditProductActivity.this, "Error..", "Editing product did not succeed", false);
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


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //retrieve scan result
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        if (scanningResult != null) {
            //we have a result
            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();

            Toast toast = Toast.makeText(getApplicationContext(), scanContent, Toast.LENGTH_SHORT);
            toast.show();
            editSku.setText(scanContent);
            // display it on screen
            //  formatTxt.setText("FORMAT: " + scanFormat);
            //  contentTxt.setText("CONTENT: " + scanContent);

        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void presentShowcaseView(int withDelay) {
        // sequence example
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(EditProductActivity.this, "qas");

        sequence.setConfig(config);

        sequence.addSequenceItem(imageButton,
                "Use this button to upload a new image or scan a barcode ", "GOT IT");

        sequence.addSequenceItem(editProductButton,
                "Click this button once you have made changes to your product ", "GOT IT");


        sequence.start();
    }

}