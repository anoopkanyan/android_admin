package recode360.spreeadminapp.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import recode360.spreeadminapp.R;
import recode360.spreeadminapp.adapter.LicensesAdapter;
import recode360.spreeadminapp.app.AppController;
import recode360.spreeadminapp.models.Licenses;
import recode360.spreeadminapp.utils.DividerItemDecoration;

/**
 * Third party software and libraries used.
 */
public class LicensesActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private String TAG;

    private List<Licenses> licensesList = new ArrayList<Licenses>();
    private RecyclerView recyclerView;
    private LicensesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = AppController.class
                .getSimpleName();

        setContentView(R.layout.activity_licenses);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Licenses");

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new LicensesAdapter(licensesList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);
        prepareLicenceData();

    }

    private void prepareLicenceData() {
        Licenses licence = new Licenses("ZXING", "\nCopyright 2008 ZXING authors\n" + getString(R.string.licence_apache), "");
        licensesList.add(licence);

        licence = new Licenses("TextDrawable", "\nCopyright (c) 2014 Amulya Khare\n" + getString(R.string.licence_mit), "");
        licensesList.add(licence);

        licence = new Licenses("FloatingActionButton", "\nCopyright (c) 2014 Oleksandr Melnykov\n" + getString(R.string.licence_mit), "");
        licensesList.add(licence);

        licence = new Licenses("BottomSheet", "\nCopyright 2011, 2015 Kai Liao\n" + getString(R.string.licence_apache), "");
        licensesList.add(licence);

        licence = new Licenses("MaterialShowcaseView", "\nCopyright 2015 Dean Wild\n" + getString(R.string.licence_apache), "");
        licensesList.add(licence);

        licence = new Licenses("FloatingActionButton", "\nCopyright 2015 Dmytro Tarianyk\n" + getString(R.string.licence_apache), "");
        licensesList.add(licence);


        mAdapter.notifyDataSetChanged();
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


}
