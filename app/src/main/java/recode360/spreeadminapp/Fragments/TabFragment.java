package recode360.spreeadminapp.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import recode360.spreeadminapp.Activities.EditProductActivity;
import recode360.spreeadminapp.Activities.MainActivity;
import recode360.spreeadminapp.Activities.OrdersActivity;
import recode360.spreeadminapp.R;
import recode360.spreeadminapp.adapter.SearchAdapter;
import recode360.spreeadminapp.app.AppController;
import recode360.spreeadminapp.app.Config;
import recode360.spreeadminapp.models.Orders;
import recode360.spreeadminapp.models.sessions.AlertDialogManager;
import recode360.spreeadminapp.utils.MaterialSearchView;

/**
 *
 */

public class TabFragment extends Fragment {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static int int_items = 3;

    //implement order search based on order numbers
    private MaterialSearchView searchView;
    private MenuItem mSearchAction;
    private boolean isSearchOpened = false;
    private EditText edtSeach;

    private String[] order_numbers;
    // To store all the products
    private List<Orders> ordersList;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        /**
         *Inflate tab_layout and setup Views.
         */
        View x = inflater.inflate(R.layout.tab_layout, null);
        tabLayout = (TabLayout) x.findViewById(R.id.tabs);
        viewPager = (ViewPager) x.findViewById(R.id.viewpager);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
//        activity.setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.orders);
        ((AppCompatActivity) getActivity()).getSupportActionBar().invalidateOptionsMenu();

        /**
         *Set an Apater for the View Pager
         */
        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));

        /**
         * Now , this is a workaround ,
         * The setupWithViewPager dose't work without the runnable .
         * Maybe a Support Library Bug .
         */

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });


        //fetch orders inside a list first

        ordersList = new ArrayList<Orders>();
        fetchOrders();

        searchView = (MaterialSearchView) getActivity().findViewById(R.id.search_view);
        searchView.showVoice(false);

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (Arrays.asList(order_numbers).contains(query)) {

                    // pass the order id to start an intent to view detailed information about an order
                    Intent intent = new Intent(getActivity(), EditProductActivity.class);
                    intent.putExtra("order_no", ordersList.get(Arrays.asList(order_numbers).indexOf(query)).getNumber());
                    startActivity(intent);

                } else {

                    // Alert Dialog Manager
                    AlertDialogManager alert = new AlertDialogManager();
                    alert.showAlertDialog(getActivity(), "Search..", "Order not found, try again", false);

                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
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

                Intent intent = new Intent(getActivity(), OrdersActivity.class);
                intent.putExtra("order_no", ordersList.get(Arrays.asList(order_numbers).indexOf(query)).getNumber());
                intent.putExtra("shipment", ordersList.get(Arrays.asList(order_numbers).indexOf(query)).getShipment_state());
                startActivity(intent);

            }
        });


        return x;

    }

    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Return fragment with respect to Position .
         */

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new AllOrdersFragment();
                case 1:
                    return new OrdersPendingFragment();
                case 2:
                    return new OrdersShipFragment();
            }
            return null;
        }

        @Override
        public int getCount() {

            return int_items;

        }

        /**
         * This method returns the title of the tab according to the position.
         */

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0:
                    return "All orders";
                case 1:
                    return "Ship";
                case 2:
                    return "Completed";

            }
            return null;
        }
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


    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.getActivity().onBackPressed();
        }
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


    //this is to fetch the requires JSON response regarding all the orders
    private void fetchOrders() {


        // Making json object request
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                Config.URL_STORE + "/api/orders.json?q[s]=updated_at%20desc&per_page=200&token=" + Config.API_KEY, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    JSONArray orders = response
                            .getJSONArray("orders");
                    order_numbers = new String[orders.length()];

                    // looping through all order nodes and storing
                    // them in array list
                    for (int i = 0; i < orders.length(); i++) {

                        JSONObject order = (JSONObject) orders
                                .get(i);

                        String number = order.getString("number");
                        order_numbers[i] = number;
                        String state = order.getString("state");
                        String total_quantity = order.getString("total_quantity");
                        String payment_state = order.getString("payment_state");
                        String display_total = order.getString("display_total");
                        String shipment_state = order.getString("shipment_state");

                        Orders ord = new Orders();
                        ord.setNumber(number);
                        ord.setState(state);
                        ord.setTotal_quantity(Integer.parseInt(total_quantity));
                        ord.setPayment_state(payment_state);
                        ord.setDisplay_total(display_total);
                        ord.setShipment_state(shipment_state);


                        if (payment_state.equals("paid"))
                            //ordersList.add(ord);
                            ordersList.add(ord);
                    }

                    SearchAdapter adapter = new SearchAdapter(getActivity(), order_numbers);
                    searchView.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }

                // hiding the progress dialog

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getActivity(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        jsonObjReq.setShouldCache(false);
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);

    }

}
