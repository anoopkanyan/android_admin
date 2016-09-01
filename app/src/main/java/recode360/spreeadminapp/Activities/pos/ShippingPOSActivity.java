package recode360.spreeadminapp.Activities.pos;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;

import java.util.List;

import recode360.spreeadminapp.R;
import recode360.spreeadminapp.models.Address;
import recode360.spreeadminapp.models.State;
import recode360.spreeadminapp.utils.DatabaseHandler;


public class ShippingPOSActivity extends AppCompatActivity {

    private DatabaseHandler database;
    private List<State> states;
    private String[] stateNames;
    private Address newAddress;

    private Spinner stateSpinner;
    private EditText firstName, lastName, addressLine1, addressLine2;
    private EditText city, pincode, phone;
    private ScrollView addOrEditContainer;


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

        initAddressUI();

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
                validateForm();
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


    private void initAddressUI() {

        // ADD or EDIT mode
        addOrEditContainer = (ScrollView) findViewById(R.id.fragment_address_scroll_view);
        firstName = (EditText) findViewById(R.id.fragment_address_first_name_txt);
        lastName = (EditText) findViewById(R.id.fragment_address_last_name_txt);
        addressLine1 = (EditText) findViewById(R.id.fragment_address_line1_txt);
        addressLine2 = (EditText) findViewById(R.id.fragment_address_line2_txt);
        city = (EditText) findViewById(R.id.fragment_address_city_txt);
        pincode = (EditText) findViewById(R.id.fragment_address_pincode_txt);
        phone = (EditText) findViewById(R.id.fragment_address_phone_txt);
        stateSpinner = (Spinner) findViewById(R.id.fragment_address_state_spinner);

        setStateSpinnerAdapter();

    }


    //validates the address form
    private boolean validateForm() {

        if (TextUtils.isEmpty(firstName.getText())) {
            firstName.setError("cannot be empty");
            return false;
        }
        if (TextUtils.isEmpty(lastName.getText())) {
            lastName.setError("cannot be empty");
            return false;
        }
        if (TextUtils.isEmpty(addressLine1.getText())) {
            addressLine1.setError("cannot be empty");
            return false;
        }
        if (TextUtils.isEmpty(city.getText())) {
            city.setError("cannot be empty");
            return false;
        }
        if (TextUtils.isEmpty(pincode.getText())) {
            pincode.setError("cannot be empty");
            return false;
        }
        if (TextUtils.isEmpty(phone.getText())) {
            phone.setError("cannot be empty");
            return false;
        }

        newAddress = new Address();
        newAddress.setFirstname(firstName.getText().toString());
        newAddress.setLastname(lastName.getText().toString());
        newAddress.setAddress1(addressLine1.getText().toString());
        newAddress.setAddress2(addressLine2.getText().toString());
        newAddress.setCity(city.getText().toString());
        //newAddress.setStateId(stateArrayList.get(stateSpinnerSelectedItem).getId());
        newAddress.setZipcode(Integer.parseInt(pincode.getText().toString()));
        newAddress.setPhone(phone.getText().toString());
        return true;
    }

}
