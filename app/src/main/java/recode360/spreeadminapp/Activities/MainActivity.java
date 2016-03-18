package recode360.spreeadminapp.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import recode360.spreeadminapp.Fragments.PrimaryFragment;
import recode360.spreeadminapp.Fragments.ProductsFragment;
import recode360.spreeadminapp.Fragments.TabFragment;
import recode360.spreeadminapp.Fragments.TaxonomyFragment;
import recode360.spreeadminapp.R;
import recode360.spreeadminapp.app.Config;
import recode360.spreeadminapp.models.sessions.AlertDialogManager;
import recode360.spreeadminapp.models.sessions.SessionManager;
import recode360.spreeadminapp.utils.Utils;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class MainActivity extends AppCompatActivity {
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
    ActionBarDrawerToggle mDrawerToggle;

    private Toolbar toolbar;

    private ProductsFragment frg;
    //for display of  name and email inside the navigation drawer header
    private TextView name_header;
    private TextView email_header;
    private String nameChar;

    AlertDialogManager alert = new AlertDialogManager();

    // Session Manager Class
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Session class instance
        session = new SessionManager(getApplicationContext());
        session.checkLogin();

        setContentView(R.layout.activity_main);

        /**
         *Setup the DrawerLayout and NavigationView
         */

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.shitstuff);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        name_header = (TextView) findViewById(R.id.name_header);
        email_header = (TextView) findViewById(R.id.email_header);

        name_header.setText(Config.USER_FULL_NAME);
        email_header.setText(Config.USER_EMAIL);

        nameChar = Utils.parseName(Config.USER_FULL_NAME);

        TextDrawable drawable = TextDrawable.builder()
                .buildRound(nameChar, Color.parseColor("#FF4081"));

        ImageView image = (ImageView) findViewById(R.id.image_view_name);
        image.setImageDrawable(drawable);

        /**
         * Lets inflate the very first fragment
         * Here , we are inflating the TabFragment as the first Fragment
         */

        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.containerView, new PrimaryFragment()).commit();


        /**
         * Setup click events on the Navigation View Items.
         */
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();

                if (menuItem.getItemId() == R.id.nav_item_product) {
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView, frg = new ProductsFragment()).addToBackStack("products fragment commit").commit();

                }

                if (menuItem.getItemId() == R.id.nav_item_home) {
                    FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                    xfragmentTransaction.replace(R.id.containerView, new PrimaryFragment()).addToBackStack("home fragment commit").commit();
                }

                if (menuItem.getItemId() == R.id.nav_item_order) {
                    FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                    xfragmentTransaction.replace(R.id.containerView, new TabFragment()).addToBackStack("orders fragment commit").commit();
                }

                if (menuItem.getItemId() == R.id.nav_item_settings) {
                    Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(i);
                }

                if (menuItem.getItemId() == R.id.nav_item_taxonomy) {
                    FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                    xfragmentTransaction.replace(R.id.containerView, new TaxonomyFragment()).addToBackStack("taxonomies fragment commit").commit();
                }


                return true;
            }

        });


        /**
         * Setup Drawer Toggle of the Toolbar
         */


        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name,
                R.string.app_name);

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();

        presentShowcaseView(500);


    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            this.finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        frg.onActivityResult(requestCode, resultCode, data);

    }

    private void presentShowcaseView(int withDelay) {
        // sequence example
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, "tyui");

        sequence.setConfig(config);

        sequence.addSequenceItem(getNavButtonView(toolbar),
                "Use this button to navigate between orders and products", "GOT IT");


        sequence.start();
    }

    private ImageButton getNavButtonView(Toolbar toolbar) {
        for (int i = 0; i < toolbar.getChildCount(); i++)
            if (toolbar.getChildAt(i) instanceof ImageButton)
                return (ImageButton) toolbar.getChildAt(i);

        return null;
    }


}