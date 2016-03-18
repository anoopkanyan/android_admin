package recode360.spreeadminapp.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.List;

import recode360.spreeadminapp.Activities.CreateTaxonomyActivity;
import recode360.spreeadminapp.Activities.MainActivity;
import recode360.spreeadminapp.R;
import recode360.spreeadminapp.adapter.TaxonomiesAdapter;
import recode360.spreeadminapp.app.AppController;
import recode360.spreeadminapp.app.Config;
import recode360.spreeadminapp.models.Taxonomies;
import recode360.spreeadminapp.utils.DividerItemDecoration;


public class TaxonomyFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Toolbar toolbar;
    private SwipeRefreshLayout swipeLayout;

    // To store all the orders
    private List<Taxonomies> taxonomiesList;
    private List<Taxonomies> tempList;

    // Progress dialog
    private ProgressDialog pDialog;
    private FloatingActionButton fab;

    private TaxonomiesAdapter adapter;
    private RecyclerView recList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.taxonomy_fragment, container, false);
        recList = (RecyclerView) view.findViewById(R.id.taxonomies_recycler_view);
        // recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.taxonomies);

        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

        fab = (FloatingActionButton) view.findViewById(R.id.fab);

        fab.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateTaxonomyActivity.class);
                startActivity(intent);
            }
        });


        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeLayout.setOnRefreshListener(this);

        taxonomiesList = new ArrayList<Taxonomies>();

        fetchTaxonomies();
        //  adapter= new OrderAdapter(ordersList);
        //add the line separator
        recList.addItemDecoration(new DividerItemDecoration(activity, LinearLayoutManager.VERTICAL));
        recList.setAdapter(adapter);
        fab.attachToRecyclerView(recList);

        return view;
    }

    //fetch the JSON responses regarding all the taxonomies in our store
    private void fetchTaxonomies() {

        if (!(swipeLayout.isRefreshing())) {
            // Showing progress dialog before making request
            pDialog.setMessage("Fetching taxonomies");
            showpDialog();
        }

        // ordersList.clear();
        tempList = new ArrayList<Taxonomies>();

        // Making json object request
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                Config.URL_STORE + "/api/taxonomies.json?token=" + Config.API_KEY, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    JSONArray taxonomies = response
                            .getJSONArray("taxonomies");

                    // looping through all taxonomies nodes and storing
                    // them in array list
                    for (int i = 0; i < taxonomies.length(); i++) {

                        JSONObject taxonomy = (JSONObject) taxonomies
                                .get(i);

                        String name = taxonomy.getString("name");
                        Integer id = taxonomy.getInt("id");

                        Taxonomies taxonomies_obj = new Taxonomies();
                        taxonomies_obj.setName(name);
                        taxonomies_obj.setId(id);

                        tempList.add(taxonomies_obj);
                    }

                    // notifying adapter about data changes, so that the
                    // list renders with new data
                    taxonomiesList.clear();
                    taxonomiesList.addAll(tempList);
                    adapter = new TaxonomiesAdapter(taxonomiesList);
                    recList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

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

        jsonObjReq.setShouldCache(false);
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
    public void onRefresh() {
        fetchTaxonomies();
    }

}
