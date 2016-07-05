package recode360.spreeadminapp.Activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.CheckBox;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import recode360.spreeadminapp.R;
import recode360.spreeadminapp.adapter.PackagesRecyclerViewAdapter;
import recode360.spreeadminapp.app.AppController;
import recode360.spreeadminapp.app.Config;
import recode360.spreeadminapp.models.Packages;


public class PackagesActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ProgressDialog progressDialog;


    private GridLayoutManager gaggeredGridLayoutManager;
    private List<Packages> packageList;
    private ProgressDialog pDialog;
    private static String url = Config.URL_STORE + "/api/goshipments/package.json";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_packages);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Box Choice");

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        gaggeredGridLayoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(gaggeredGridLayoutManager);
        packageList = new ArrayList<Packages>();

        final PackagesRecyclerViewAdapter rcAdapter = new PackagesRecyclerViewAdapter(PackagesActivity.this, packageList);
        recyclerView.setAdapter(rcAdapter);

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        // Creating volley request obj
        JsonArrayRequest movieReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("PackagesList", response.toString());
                        hidePDialog();

                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject obj = response.getJSONObject(i);
                                Packages pack = new Packages();
                                pack.setName(obj.getString("parcel"));
                                pack.setDimension_height(obj.getString("dimension_height"));
                                pack.setDimension_length(obj.getString("dimension_length"));
                                pack.setDimension_width(obj.getString("dimension_width"));
                                pack.setDimension_unit(obj.getString("dimension_unit"));
                                String image_url = (obj.getString("image_url"));

                                if (image_url.equals("null")) {
                                    pack.setPhoto("http://www.mtdproducts.com/wcpics/MTDProducts/en_US/products/notavailable_product_listing.png");

                                } else {
                                    pack.setPhoto(image_url);
                                }

                                if (pack.getName().startsWith("USPS")) {
                                    // adding movie to movies array
                                    packageList.add(pack);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data
                        rcAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("ProductsListing", "Error: " + error.getMessage());
                hidePDialog();

            }
        }
        ) {
            /**
             * Passing the token to access the API
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Spree-Token", Config.API_KEY);
                return headers;
            }

        };


        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(movieReq);

    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }


}


