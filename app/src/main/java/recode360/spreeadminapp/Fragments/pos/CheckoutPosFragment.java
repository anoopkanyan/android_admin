package recode360.spreeadminapp.Fragments.pos;

/*
    Renders a list of all the products, that can be bought using the POS.
    Products are added to the cart once they are clicked upon.
 */


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
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

import recode360.spreeadminapp.Activities.MainActivity;
import recode360.spreeadminapp.Activities.pos.OrderPosActivity;
import recode360.spreeadminapp.R;
import recode360.spreeadminapp.adapter.pos.ProductPosAdapter;
import recode360.spreeadminapp.app.AppController;
import recode360.spreeadminapp.app.Config;
import recode360.spreeadminapp.models.Product;
import recode360.spreeadminapp.utils.Utils;


public class CheckoutPosFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, ProductPosAdapter.EditPlayerAdapterCallback {

    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private ProductPosAdapter adapter;
    private MaterialDialog dialog;
    SwipeRefreshLayout swipeLayout;
    private TextView totalPriceView;
    private TextView totalQtyView;

    // To store all the products
    private List<Product> productsList;
    private List<Product> tempList;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_checkout_pos, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("POS");
        ((AppCompatActivity) getActivity()).getSupportActionBar().invalidateOptionsMenu();
        setHasOptionsMenu(true);

        totalPriceView = (TextView) rootView.findViewById(R.id.cart_amount);
        totalQtyView = (TextView) rootView.findViewById(R.id.cart_item_count);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.product_pos_recycler_view);
        productsList = new ArrayList<Product>();
        adapter = new ProductPosAdapter(getActivity(), productsList);
        adapter.setCallback(this);

        // use a linear layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeLayout.setOnRefreshListener(this);

        fetchProducts();

        RelativeLayout cartView = (RelativeLayout) rootView.findViewById(R.id.cart_view);

        cartView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Snackbar.make(v, "Going to cart_menu...", Snackbar.LENGTH_SHORT).show();

                launchCartActivity();
            }
        });

        cartView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Vibrator vb = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                vb.vibrate(100);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setMessage("Are you sure you want to clear the cart?");

                alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        clearCart();
                    }
                });

                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                return false;
            }
        });


        return rootView;
    }


    /**
     * Fetching the products from our server
     */
    private void fetchProducts() {

        if (!(swipeLayout.isRefreshing())) {

            dialog = new MaterialDialog.Builder(getContext())
                    .content("Fetching products")
                    .contentColor(getResources().getColor(R.color.colorPrimary))
                    .progress(true, 0)
                    .widgetColor(getResources().getColor(R.color.colorAccent))
                    .cancelable(false)
                    .show();
        }

        tempList = new ArrayList<Product>();

        // Making json object request
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                Config.URL_STORE + "/api/products.json?token=" + Config.API_KEY + "&per_page=200", null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                Log.d(TAG, response.toString());

                try {
                    JSONArray products = response.getJSONArray("products");

                    // looping through all product nodes and storing
                    // them in array list
                    for (int i = 0; i < products.length(); i++) {

                        JSONObject product = (JSONObject) products.get(i);

                        String id = product.getString("id");
                        String name = product.getString("name");
                        String description = "SKU: " + Utils.stripHtml(product.getJSONObject("master")
                                .getString("sku"));
                        description = description.replaceAll("(?s)<!--.*?-->", "");
                        BigDecimal price = new BigDecimal(product
                                .getString("price"));

                        JSONObject master = product.getJSONObject("master");
                        Product p = new Product();
                        JSONArray images = master.getJSONArray("images");
                        if (images.length() > 0) {
                            JSONObject image_object = images.getJSONObject(0);
                            String result = image_object.getString("product_url");

                            if (URLUtil.isValidUrl(result)) {
                                p.setImage(result);
                            } else {
                                p.setImage(Config.URL_STORE + result);
                            }
                        }
                        p.setId(Integer.parseInt(id));
                        p.setName(name);
                        p.setDescription(description);
                        p.setPrice(price);
                        p.setCart_qty(0);

                        //productsList.add(p);
                        tempList.add(p);
                    }

                    // notifying adapter about data changes
                    productsList.clear();
                    productsList.addAll(tempList);
                    adapter.notifyDataSetChanged();


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }

                dialog.hide();
                swipeLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getActivity(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                dialog.hide();
                swipeLayout.setRefreshing(false);
            }
        });


        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        jsonObjReq.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(jsonObjReq);

    }

    @Override
    public void onRefresh() {
        fetchProducts();
    }

    private void launchCartActivity() {

        Bundle information = new Bundle();

        ArrayList<Product> cart_products = (ArrayList<Product>) adapter.getPassList();


        information.putSerializable("products", cart_products);
        Intent intent = new Intent(getActivity(), OrderPosActivity.class);
        intent.putExtras(information);
        startActivity(intent);

    }

    private void removeProgressBar() {

    }


    private void clearCart() {

        /*
        user.cartItems.clear();
        user.cartCount = 0;
        user.cartAmount = 0.0f;
        user.cartProducts = new Products();
        updateCart();
        */

        Snackbar.make(getView(), "Cleared the cart", Snackbar.LENGTH_SHORT).show();
        Log.d("Cart", "Cleared the cart");
    }

    /*
    disabled temporarily

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.pos_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

        */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_add_product:
                /*
                Intent intent = new Intent(getActivity(), CreateProductActivity.class);
                startActivity(intent);
                */
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public void update() {

        totalPriceView.setText("$" + adapter.getPriceTotal().toString());
    }


    @Override
    public void deletePressed() {
        totalPriceView.setText("$" + adapter.getPriceTotal().toString());
        totalQtyView.setText(Integer.toString(adapter.getQtyTotal()));
    }
}



