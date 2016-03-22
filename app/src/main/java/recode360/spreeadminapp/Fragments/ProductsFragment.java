package recode360.spreeadminapp.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.melnykov.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import recode360.spreeadminapp.Activities.CreateProductActivity;
import recode360.spreeadminapp.Activities.EditProductActivity;
import recode360.spreeadminapp.Activities.MainActivity;
import recode360.spreeadminapp.R;
import recode360.spreeadminapp.adapter.ProductListAdapter;
import recode360.spreeadminapp.adapter.ProductListAdapter.ProductListAdapterListener;
import recode360.spreeadminapp.adapter.SearchAdapter;
import recode360.spreeadminapp.app.AppController;
import recode360.spreeadminapp.app.Config;
import recode360.spreeadminapp.models.Product;
import recode360.spreeadminapp.models.sessions.AlertDialogManager;
import recode360.spreeadminapp.utils.MaterialSearchView;
import recode360.spreeadminapp.utils.Utils;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

/**
 * products Fragment
 */

public class ProductsFragment extends Fragment implements ProductListAdapterListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ListView listView;

    private Toolbar toolbar;

    private MenuItem mSearchAction;
    private boolean isSearchOpened = false;
    private EditText edtSeach;
    private MaterialSearchView searchView;


    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;

    // To store all the products
    private List<Product> productsList;
    private List<Product> tempList;

    private String[] product_names;

    private ProductListAdapter adapter;

    // Progress dialog
    private ProgressDialog pDialog;
    private FloatingActionButton fab;

    SwipeRefreshLayout swipeLayout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.products_layout, container, false);

        //   toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);

        fab.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateProductActivity.class);
                startActivity(intent);
            }
        });


        AppCompatActivity activity = (AppCompatActivity) getActivity();
//        activity.setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.product);
        ((AppCompatActivity) getActivity()).getSupportActionBar().invalidateOptionsMenu();
        listView = (ListView) view.findViewById(R.id.list);

        // attach floating acttion button to the ListView
        fab.attachToListView(listView);

        productsList = new ArrayList<Product>();
        adapter = new ProductListAdapter(getActivity(), productsList, this);

        listView.setAdapter(adapter);

        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeLayout.setOnRefreshListener(this);

        fetchProducts();


        searchView = (MaterialSearchView) getActivity().findViewById(R.id.search_view);
        searchView.showVoice(true);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (Arrays.asList(product_names).contains(query)) {


                    Intent intent = new Intent(getActivity(), EditProductActivity.class);
                    intent.putExtra("product_id", productsList.get(Arrays.asList(product_names).indexOf(query)).getId());
                    startActivity(intent);

                } else {

                    // Alert Dialog Manager
                    AlertDialogManager alert = new AlertDialogManager();
                    alert.showAlertDialog(getActivity(), "Search..", "Product not found, try again", false);

                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.d("Hat", "BC");
                Product product = productsList.get(position);
                onAddToCartPressed(product);

            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {

            }
        });

        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String query = parent.getAdapter().getItem(position).toString();

                Intent intent = new Intent(getActivity(), EditProductActivity.class);
                intent.putExtra("product_id", productsList.get(Arrays.asList(product_names).indexOf(query)).getId());
                startActivity(intent);

            }
        });


        presentShowcaseView(100); // one second delay

        return view;

    }

    /**
     * Fetching the products from our server
     */
    private void fetchProducts() {

        if (!(swipeLayout.isRefreshing())) {
            // Showing progress dialog before making request
            pDialog.setMessage("Fetching products...");
            showpDialog();
        }
        //productsList.clear();
        tempList = new ArrayList<Product>();

        // Making json object request
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                Config.URL_STORE + "/api/products.json?token=" + Config.API_KEY+"&per_page=200", null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                Log.d(TAG, response.toString());

                try {
                    JSONArray products = response
                            .getJSONArray("products");
                    product_names = new String[products.length()];

                    // looping through all product nodes and storing
                    // them in array list
                    for (int i = 0; i < products.length(); i++) {

                        JSONObject product = (JSONObject) products
                                .get(i);

                        String id = product.getString("id");
                        String name = product.getString("name");
                        product_names[i] = name;
                        String description = Utils.stripHtml(product
                                .getString("description"));     //removes HTML tags if any in the response
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

                        //productsList.add(p);
                        tempList.add(p);
                    }

                    // notifying adapter about data changes, so that the
                    // list renders with new data
                    productsList.clear();
                    productsList.addAll(tempList);
                    adapter.notifyDataSetChanged();
                    SearchAdapter adapter = new SearchAdapter(getActivity(), product_names);
                    searchView.setAdapter(adapter);


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }

                // hiding the progress dialog
                hidepDialog();
                swipeLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getActivity(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
                hidepDialog();
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
        mSearchAction.setVisible(true);
        searchView.setMenuItem(mSearchAction);

        super.onCreateOptionsMenu(menu, inflater);
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
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.getActivity().onBackPressed();
        }
    }

    private void doSearch(String query) {
        pDialog.setMessage("Searching");
        showpDialog();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("It's called", "From the fragment");
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == Activity.RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    searchView.setQuery(searchWrd, false);
                }
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    Activity getAct() {
        Context context = getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }


    private void presentShowcaseView(int withDelay) {
        // to show button usage upon app boarding
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(getAct(), "pscd");
        sequence.setConfig(config);
        sequence.addSequenceItem(fab,
                "Use this button to create a new listing ", "GOT IT");
        sequence.addSequenceItem((Toolbar) getActivity().findViewById(R.id.toolbar),
                "Easily search for the products listed in your store", "GOT IT");
        sequence.start();
    }


    private ImageButton getNavButtonView(Toolbar toolbar) {
        for (int i = 1; i < toolbar.getChildCount(); i++)
            if (toolbar.getChildAt(i) instanceof ImageButton)
                return (ImageButton) toolbar.getChildAt(i);

        return null;
    }

    @Override
    public void onRefresh() {
        fetchProducts();
    }

}
