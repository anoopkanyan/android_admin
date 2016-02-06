package recode360.spreeadminapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import recode360.spreeadminapp.Fragments.MainFragment;
import recode360.spreeadminapp.R;

/**
 * Created by ANOOP on 1/11/2016.
 */
public class ImageActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private String product_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        Intent intent = getIntent();
        product_id = intent.getStringExtra("product_id");


        //get the toolbar and set order number as it's title
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Image Upload");

        if (savedInstanceState == null) {
            Log.v(TAG, "MainFragment Creation");
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new MainFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //  getMenuInflater().inflate(R.menu.main, menu);
        return true;
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

    public String getProduct_Id() {
        return product_id;
    }

}
