package recode360.spreeadminapp.Activities.pos;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.List;

import recode360.spreeadminapp.R;
import recode360.spreeadminapp.models.State;
import recode360.spreeadminapp.utils.DatabaseHandler;


public class ShippingPOSActivity extends AppCompatActivity {

    private DatabaseHandler database;
    private List<State> states;
    private String[] stateNames;

    private Spinner stateSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping_pos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.bs_ic_clear);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Address");

        stateSpinner = (Spinner) findViewById(R.id.fragment_address_state_spinner);

        setStateSpinnerAdapter();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.next_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_add_product:
                //add the Shipping address and get the shipping prices
                return true;

            case android.R.id.home:
                //shipping disabled
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    private void setStateSpinnerAdapter() {

        database = new DatabaseHandler(this);
        states = database.getAllStates();
        stateNames = new String[states.size()];

        int i = 0;
        for (State state : states) {
            stateNames[i] = state.getName();
            i++;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ShippingPOSActivity.this, android.R.layout.simple_spinner_item, stateNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

}
