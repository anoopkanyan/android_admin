package recode360.spreeadminapp.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import recode360.spreeadminapp.Activities.EditProductActivity;
import recode360.spreeadminapp.Activities.MainActivity;
import recode360.spreeadminapp.R;
import recode360.spreeadminapp.adapter.ProductListAdapter;
import recode360.spreeadminapp.adapter.ProductListAdapter.ProductListAdapterListener;
import recode360.spreeadminapp.app.AppController;
import recode360.spreeadminapp.app.Config;
import recode360.spreeadminapp.models.Product;

/**
 * Products Fragment
 */
public class ProductsFragment extends Fragment implements ProductListAdapterListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ListView listView;

    private Toolbar toolbar;

    private MenuItem mSearchAction;
    private boolean isSearchOpened = false;
    private EditText edtSeach;

    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;

    // To store all the products
    private List<Product> productsList;

    private ProductListAdapter adapter;

    // Progress dialog
    private ProgressDialog pDialog;
    private FloatingActionButton fab;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.products_layout, container, false);

        //   toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);

        fab.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                mFragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.containerView, new CreateProductFragment()).commit();


            }
        });


        AppCompatActivity activity = (AppCompatActivity) getActivity();
//        activity.setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.product);

        listView = (ListView) view.findViewById(R.id.list);


        productsList = new ArrayList<Product>();
        adapter = new ProductListAdapter(getActivity(), productsList, this);

        listView.setAdapter(adapter);

        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);


        fetchProducts();

        return view;

    }

    /**
     * Fetching the products from our server
     */
    private void fetchProducts() {

        // Showing progress dialog before making request
        pDialog.setMessage("Fetching products...");
        showpDialog();

        // Making json object request
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                "https://rails-tutorial-anoopkanyan.c9.io/api/products.json?token=" + Config.API_KEY, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    JSONArray products = response
                            .getJSONArray("products");

                    // looping through all product nodes and storing
                    // them in array list
                    for (int i = 0; i < products.length(); i++) {

                        JSONObject product = (JSONObject) products
                                .get(i);

                        String id = product.getString("id");
                        String name = product.getString("name");
                        String description = product
                                .getString("description");
                        //    String image = product.getString("image");
                        BigDecimal price = new BigDecimal(product
                                .getString("price"));
                        //  String sku = product.getString("sku");

                        JSONObject master = product.getJSONObject("master");
                        Product p = new Product();
                        JSONArray images = master.getJSONArray("images");
                        if (images.length() > 0) {
                            JSONObject image_object = images.getJSONObject(0);

                            String result = image_object.getString("product_url");

                            System.out.println(result);
                            p.setImage("https://rails-tutorial-anoopkanyan.c9.io" + result);
                        }

                        p.setId(Integer.parseInt(id));
                        p.setName(name);
                        p.setDescription(description);
                        p.setPrice(price);

                        productsList.add(p);
                    }

                    // notifying adapter about data changes, so that the
                    // list renders with new data
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }

                // hiding the progress dialog
                hidepDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getActivity(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
                hidepDialog();
            }
        });


        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


    @Override
    public void onAddToCartPressed(Product product) {

        Intent intent = new Intent(getActivity(), EditProductActivity.class);
        intent.putExtra("product_id", product.getId());
        startActivity(intent);
    }


    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        mSearchAction = menu.findItem(R.id.action_search);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_search:
                handleMenuSearch();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void handleMenuSearch() {

        ActionBar action = ((AppCompatActivity) getActivity()).getSupportActionBar();


        if (isSearchOpened) { //test if the search is open

            action.setDisplayShowCustomEnabled(false); //disable a custom view inside the actionbar
            action.setDisplayShowTitleEnabled(true); //show the title in the action bar

            //hides the keyboard
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(edtSeach.getWindowToken(), 0);

            //add the search icon in the action bar
            mSearchAction.setIcon(getResources().getDrawable(R.drawable.abc_ic_search_api_mtrl_alpha));

            isSearchOpened = false;
        } else { //open the search entry

            action.setDisplayShowCustomEnabled(true); //enable it to display a
            // custom view in the action bar.
            action.setCustomView(R.layout.search_bar);//add the custom view
            action.setDisplayShowTitleEnabled(false); //hide the title

            edtSeach = (EditText) action.getCustomView().findViewById(R.id.edtSearch); //the text editor


            //this is a listener to do a search when the user clicks on search button
            edtSeach.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        doSearch(edtSeach.getText().toString());
                        return true;
                    }
                    return false;
                }
            });


            edtSeach.requestFocus();

            //open the keyboard focused in the edtSearch
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(edtSeach, InputMethodManager.SHOW_IMPLICIT);


            //add the close icon
            mSearchAction.setIcon(getResources().getDrawable(R.drawable.abc_ic_clear_mtrl_alpha));

            isSearchOpened = true;
        }
    }

    public void onBackPressed() {
        if (isSearchOpened) {
            handleMenuSearch();
            return;
        }
        super.getActivity().onBackPressed();
    }

    private void doSearch(String query) {

    pDialog.setMessage("Searching");
    showpDialog();



    }


}
