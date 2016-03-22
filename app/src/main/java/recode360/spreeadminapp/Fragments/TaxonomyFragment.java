package recode360.spreeadminapp.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.melnykov.fab.FloatingActionButton;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import recode360.spreeadminapp.Activities.CreateTaxonomyActivity;
import recode360.spreeadminapp.Activities.MainActivity;
import recode360.spreeadminapp.R;
import recode360.spreeadminapp.adapter.IconTreeItemHolder;
import recode360.spreeadminapp.app.AppController;
import recode360.spreeadminapp.app.Config;
import recode360.spreeadminapp.models.Taxonomies;

public class TaxonomyFragment extends Fragment {
    private TextView statusBar;
    private AndroidTreeView tView;
    private static final String TAG = MainActivity.class.getSimpleName();
    private Toolbar toolbar;

    // To store all the orders
    private List<Taxonomies> taxonomiesList;

    // Progress dialog
    private ProgressDialog pDialog;
    private FloatingActionButton fab;

    private TreeNode root;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.taxonomy_fragment, null, false);
        final ViewGroup containerView = (ViewGroup) rootView.findViewById(R.id.container);

        //set the title for the fragment
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.taxonomies);

        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);

        fab.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateTaxonomyActivity.class);
                startActivity(intent);
            }
        });


        statusBar = (TextView) rootView.findViewById(R.id.status_bar);

        root = TreeNode.root();

        taxonomiesList = new ArrayList<Taxonomies>();
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);
        // Showing progress dialog before making request
        pDialog.setMessage("Fetching taxonomies");
        showpDialog();

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
                        taxonomiesList.add(taxonomies_obj);

                        //create the root for taxonomy
                        TreeNode taxonRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder, taxonomies_obj.getName()));

                        //create further nodes for taxons
                        JSONArray taxon_array = taxonomy.getJSONObject("root").getJSONArray("taxons");
                        JSONObject taxon = taxonomy.getJSONObject("root");

                        try {

                            if (taxon.has("taxons")) {
                                taxon_array = taxon.getJSONArray("taxons");
                                for (int j = 0; j < taxon_array.length(); j++) {
                                    taxon = (JSONObject) taxon_array.get(j);
                                    //get name and id
                                    String taxon_name = taxon.getString("name");
                                    String taxon_id = taxon.getString("id");
                                    TreeNode taxonNode = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_label_outline, taxon_name));
                                    taxonRoot.addChildren(taxonNode);

                                }

                            }
                        } catch (Exception e) {
                            Log.d("Taxonomies error", e.getMessage().toString());
                        }

                        root.addChildren(taxonRoot);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }

                tView = new AndroidTreeView(getActivity(), root);
                tView.setDefaultAnimation(true);
                tView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom);
                tView.setDefaultViewHolder(IconTreeItemHolder.class);
                tView.setDefaultNodeClickListener(nodeClickListener);
                tView.setDefaultNodeLongClickListener(nodeLongClickListener);

                containerView.addView(tView.getView());
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

        jsonObjReq.setShouldCache(false);
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);


        if (savedInstanceState != null)

        {
            String state = savedInstanceState.getString("tState");
            if (!TextUtils.isEmpty(state)) {
                tView.restoreState(state);
            }
        }

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.expandAll:
                tView.expandAll();
                break;

            case R.id.collapseAll:
                tView.collapseAll();
                break;
        }
        return true;
    }

    private int counter = 0;

    private TreeNode.TreeNodeClickListener nodeClickListener = new TreeNode.TreeNodeClickListener() {
        @Override
        public void onClick(TreeNode node, Object value) {
            IconTreeItemHolder.IconTreeItem item = (IconTreeItemHolder.IconTreeItem) value;
            statusBar.setText("Last clicked: " + item.text);
        }
    };

    private TreeNode.TreeNodeLongClickListener nodeLongClickListener = new TreeNode.TreeNodeLongClickListener() {
        @Override
        public boolean onLongClick(TreeNode node, Object value) {
            IconTreeItemHolder.IconTreeItem item = (IconTreeItemHolder.IconTreeItem) value;
            Toast.makeText(getActivity(), "Long click: " + item.text, Toast.LENGTH_SHORT).show();
            return true;
        }
    };

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tState", tView.getSaveState());
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}