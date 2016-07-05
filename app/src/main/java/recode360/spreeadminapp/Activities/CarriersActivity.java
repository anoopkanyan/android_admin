package recode360.spreeadminapp.Activities;

/**
 * The activity updates a user's preferences regarding the carriers on the GoShippo backend. Only those
 * rates will be shown which belong to the carriers selected by the user.
 **/


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.shippo.Shippo;
import com.shippo.exception.APIConnectionException;
import com.shippo.exception.APIException;
import com.shippo.exception.AuthenticationException;
import com.shippo.exception.InvalidRequestException;
import com.shippo.model.CarrierAccount;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import recode360.spreeadminapp.R;
import recode360.spreeadminapp.adapter.CarrierAdapter;
import recode360.spreeadminapp.app.Config;
import recode360.spreeadminapp.models.Carrier;


public class CarriersActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Button btnSelection;

    private List<Carrier> carrierList;

    //collection of carrier accounts from Shippo
    private List<CarrierAccount> col;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carriers);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Choose Carriers");

        btnSelection = (Button) findViewById(R.id.btnShow);

        carrierList = new ArrayList<Carrier>();

        AsyncTaskRunner1 runner = new AsyncTaskRunner1();
        runner.execute();

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // create an Object for Adapter
        mAdapter = new CarrierAdapter(carrierList);

        // set the adapter object to the Recyclerview
        mRecyclerView.setAdapter(mAdapter);

        btnSelection.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                AsyncTaskRunner2 runner2 = new AsyncTaskRunner2();
                runner2.execute();

            }
        });

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


    private class AsyncTaskRunner1 extends AsyncTask<String, String, String> {

        ProgressDialog progressDialog = new ProgressDialog(CarriersActivity.this);

        @Override
        protected String doInBackground(String... params) {

            try {

                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);

                Shippo.setDEBUG(true);

                col = CarrierAccount.all(Config.USER_SHIPPO_KEY).getData();


            } catch (AuthenticationException e) {
                e.printStackTrace();
            } catch (InvalidRequestException e) {
                e.printStackTrace();
            } catch (APIConnectionException e) {
                e.printStackTrace();
            } catch (APIException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < col.size(); i++) {
                Carrier st = new Carrier(col.get(i).getCarrier().toString(), col.get(i).getObjectId().toString()
                        , col.get(i).getActive());

                carrierList.add(st);
            }


            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            mAdapter.notifyDataSetChanged();
            progressDialog.hide();
        }


        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Getting Carriers");
            progressDialog.show();

        }


        @Override
        protected void onProgressUpdate(String... text) {


        }
    }

    private class AsyncTaskRunner2 extends AsyncTask<String, String, String> {

        ProgressDialog progressDialog = new ProgressDialog(CarriersActivity.this);

        @Override
        protected String doInBackground(String... params) {

            try {

                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);

                //getb the selected checkboxes and update them to the Shippo servvers

                Shippo.setDEBUG(true);

                String data = "";
                List<Carrier> stList = ((CarrierAdapter) mAdapter)
                        .getCarriertList();

                for (int i = 0; i < stList.size(); i++) {
                    Carrier singleStudent = stList.get(i);
                    if (singleStudent.isSelected() == true) {
                        Map<String, Object> update = new HashMap<String, Object>();
                        update.put("active", true);
                        CarrierAccount.update(col.get(i).getObjectId(), update, Config.USER_SHIPPO_KEY);

                    } else {
                        Map<String, Object> update = new HashMap<String, Object>();
                        update.put("active", false);
                        CarrierAccount.update(col.get(i).getObjectId(), update, Config.USER_SHIPPO_KEY);

                    }
                }

                CarrierAccount.all(Config.USER_SHIPPO_KEY).setData(col);


            } catch (AuthenticationException e) {
                e.printStackTrace();
            } catch (InvalidRequestException e) {
                e.printStackTrace();
            } catch (APIConnectionException e) {
                e.printStackTrace();
            } catch (APIException e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            progressDialog.hide();

            AlertDialog.Builder builder = new AlertDialog.Builder(CarriersActivity.this);
            builder.setTitle("Carriers");

            builder.setMessage("Your preferences have been updated successfully");
            String positiveText = getString(android.R.string.ok);
            builder.setPositiveButton(positiveText,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                            finish();
                        }
                    });

            AlertDialog dialog = builder.create();
            // display dialog
            dialog.show();
        }


        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Updating your preferences");
            progressDialog.show();

        }


        @Override
        protected void onProgressUpdate(String... text) {


        }
    }


}