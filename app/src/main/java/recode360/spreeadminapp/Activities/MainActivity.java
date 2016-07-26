package recode360.spreeadminapp.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.github.johnkil.print.PrintConfig;
import com.mikepenz.fastadapter.utils.RecyclerViewCacheUtil;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;

import recode360.spreeadminapp.Fragments.PrimaryFragment;
import recode360.spreeadminapp.Fragments.ProductsFragment;
import recode360.spreeadminapp.Fragments.TabFragment;
import recode360.spreeadminapp.Fragments.pos.CheckoutPosFragment;
import recode360.spreeadminapp.R;
import recode360.spreeadminapp.app.Config;
import recode360.spreeadminapp.models.sessions.AlertDialogManager;
import recode360.spreeadminapp.models.sessions.SessionManager;
import recode360.spreeadminapp.utils.Utils;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class MainActivity extends AppCompatActivity implements SessionManager.LoginCallback {
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    View mHeader;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
    ActionBarDrawerToggle mDrawerToggle;
    private Boolean outStock = false;

    private Toolbar toolbar;

    private ProductsFragment frg;
    //for display of  name and email inside the navigation drawer header
    private TextView name_header;
    private TextView email_header;
    private String nameChar;

    //save our header or result
    private AccountHeader headerResult = null;
    private Drawer result = null;

    AlertDialogManager alert = new AlertDialogManager();

    // Session Manager Class
    SessionManager session;


    private String total_listings;
    private String out_of_stock;    //listings which are currently out of stock
    private int count;
    private int start = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Session class instance
        session = new SessionManager(getApplicationContext());
        session.setCallback(this);
        session.checkLogin();

        setContentView(R.layout.activity_main);

        /**
         *Setup the DrawerLayout and NavigationView
         */

        PrintConfig.initDefault(getAssets(), "icons/material-icon-font.ttf");

        /*
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.shitstuff);
        mHeader = mNavigationView.getHeaderView(0);
        */

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        nameChar = Utils.parseName(Config.USER_FULL_NAME);

        TextDrawable drawable = TextDrawable.builder()
                .buildRound(nameChar, Color.parseColor("#e74c3c"));



        /*
        name_header = (TextView) mHeader.findViewById(R.id.name_header);
        email_header = (TextView) mHeader.findViewById(R.id.email_header);

        name_header.setText(Config.USER_FULL_NAME);
        email_header.setText(Config.USER_EMAIL);

        nameChar = Utils.parseName(Config.USER_FULL_NAME);

        TextDrawable drawable = TextDrawable.builder()
                .buildRound(nameChar, Color.parseColor("#e74c3c"));

        ImageView image = (ImageView) mHeader.findViewById(R.id.image_view_name);
        image.setImageDrawable(drawable);

        */


        /**
         * Lets inflate the very first fragment
         * Here , we are inflating the TabFragment as the first Fragment
         */


        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.containerView, new PrimaryFragment()).commit();


        // Create a few sample profile
        // NOTE you have to define the loader logic too. See the CustomApplication for more details
        final IProfile profile = new ProfileDrawerItem().withName(Config.USER_FULL_NAME).withEmail(Config.USER_EMAIL).withIcon(drawable).withIdentifier(100);


        // Create the AccountHeader
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withSelectionListEnabledForSingleProfile(false)
                .withOnlyMainProfileImageVisible(true)
                .withNameTypeface(Typeface.SANS_SERIF)
                .withEmailTypeface(Typeface.SANS_SERIF)
                .withAlternativeProfileHeaderSwitching(false)
                .withDividerBelowHeader(true)
                .withTranslucentStatusBar(true)
                .withHeaderBackground(R.drawable.yo)
                .addProfiles(
                        profile
                        //don't ask but google uses 14dp for the add account icon in gmail but 20dp for the normal icons (like manage account)

                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        //sample usage of the onProfileChanged listener
                        //if the clicked item has the identifier 1 add a new profile ;)

                        //false if you have not consumed the event and it should close the drawer
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();

        //Create the drawer
        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHasStableIds(true)
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.dashboard).withIcon(R.drawable.ic_home).withIdentifier(1).withSelectable(true).withSelectedTextColor(getResources().getColor(R.color.accent)).withSelectedIconColor(getResources().getColor(R.color.accent)).withIconTintingEnabled(true).withTextColor(getResources().getColor(R.color.colorPrimaryDark)).withIconColor(getResources().getColor(R.color.colorPrimaryDark)).withTypeface(Typeface.SANS_SERIF),
                        new PrimaryDrawerItem().withName(R.string.product).withIcon(R.drawable.ic_products).withIdentifier(2).withSelectable(true).withSelectedTextColor(getResources().getColor(R.color.accent)).withSelectedIconColor(getResources().getColor(R.color.accent)).withIconTintingEnabled(true).withTextColor(getResources().getColor(R.color.colorPrimaryDark)).withIconColor(getResources().getColor(R.color.colorPrimaryDark)).withTypeface(Typeface.SANS_SERIF),
                        new PrimaryDrawerItem().withName(R.string.orders).withIcon(R.drawable.ic_orders).withIdentifier(3).withSelectable(true).withSelectedTextColor(getResources().getColor(R.color.accent)).withSelectedIconColor(getResources().getColor(R.color.accent)).withIconTintingEnabled(true).withTextColor(getResources().getColor(R.color.colorPrimaryDark)).withIconColor(getResources().getColor(R.color.colorPrimaryDark)).withTypeface(Typeface.SANS_SERIF),
                        new PrimaryDrawerItem().withName("Visit Store").withIcon(R.drawable.ic_cart).withIdentifier(4).withSelectable(true).withSelectedTextColor(getResources().getColor(R.color.accent)).withSelectedIconColor(getResources().getColor(R.color.accent)).withIconTintingEnabled(true).withTextColor(getResources().getColor(R.color.colorPrimaryDark)).withIconColor(getResources().getColor(R.color.colorPrimaryDark)).withTypeface(Typeface.SANS_SERIF),
                        new PrimaryDrawerItem().withName("POS").withIcon(R.drawable.ic_bill).withIdentifier(5).withSelectable(true).withSelectedTextColor(getResources().getColor(R.color.accent)).withSelectedIconColor(getResources().getColor(R.color.accent)).withIconTintingEnabled(true).withTextColor(getResources().getColor(R.color.colorPrimaryDark)).withIconColor(getResources().getColor(R.color.colorPrimaryDark)).withTypeface(Typeface.SANS_SERIF),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName("Settings").withIcon(R.drawable.ic_settings).withIdentifier(6).withSelectable(true).withSelectedTextColor(getResources().getColor(R.color.accent)).withSelectedIconColor(getResources().getColor(R.color.accent)).withIconTintingEnabled(true).withTextColor(getResources().getColor(R.color.colorPrimaryDark)).withIconColor(getResources().getColor(R.color.colorPrimaryDark)).withTypeface(Typeface.SANS_SERIF),
                        new PrimaryDrawerItem().withName("Help").withIcon(R.drawable.ic_help).withIdentifier(7).withSelectable(true).withSelectedTextColor(getResources().getColor(R.color.accent)).withSelectedIconColor(getResources().getColor(R.color.accent)).withIconTintingEnabled(true).withTextColor(getResources().getColor(R.color.colorPrimaryDark)).withIconColor(getResources().getColor(R.color.colorPrimaryDark)).withTypeface(Typeface.SANS_SERIF)
                )// add the items we want to use with our Drawer
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        //check if the drawerItem is set.
                        //there are different reasons for the drawerItem to be null
                        //--> click on the header
                        //--> click on the footer
                        //those items don't contain a drawerItem

                        if (drawerItem != null) {
                            Intent intent = null;
                            if (drawerItem.getIdentifier() == 1) {
                                // open dashboard
                                FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                                xfragmentTransaction.replace(R.id.containerView, new PrimaryFragment()).addToBackStack("home_fragment commit").commit();

                            } else if (drawerItem.getIdentifier() == 2) {
                                // open all the listings(products)
                                outStock = false;
                                FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                                xfragmentTransaction.replace(R.id.containerView, new ProductsFragment()).addToBackStack("prodcuts_fragment commit").commit();
                            } else if (drawerItem.getIdentifier() == 3) {
                                //
                                FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                                xfragmentTransaction.replace(R.id.containerView, new TabFragment()).addToBackStack("tab_fragment commit").commit();
                            } else if (drawerItem.getIdentifier() == 4) {

                             
                            } else if (drawerItem.getIdentifier() == 5) {

                                FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                                xfragmentTransaction.replace(R.id.containerView, new CheckoutPosFragment()).addToBackStack("pos_fragment commit").commit();


                            } else if (drawerItem.getIdentifier() == 6) {
                                intent = new Intent(MainActivity.this, SettingsActivity.class);
                                startActivity(intent);

                            } else if (drawerItem.getIdentifier() == 7) {
                                // help

                            }

                        }

                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .build();

        result.addStickyFooterItem(new PrimaryDrawerItem().withName(Config.URL_STORE).withTextColor(getResources().getColor(R.color.colorPrimaryDark)).withTypeface(Typeface.SANS_SERIF).withDisabledTextColor(getResources().getColor(R.color.colorPrimary)).withEnabled(false));

        //if you have many different types of DrawerItems you can magically pre-cache those items to get a better scroll performance
        //make sure to init the cache after the DrawerBuilder was created as this will first clear the cache to make sure no old elements are in
        //RecyclerViewCacheUtil.getInstance().withCacheSize(2).init(result);
        new RecyclerViewCacheUtil<IDrawerItem>().withCacheSize(2).apply(result.getRecyclerView(), result.getDrawerItems());

        //only set the active selection or active profile if we do not recreate the activity
        if (savedInstanceState == null) {
            // set the selection to the item with the identifier 11
            result.setSelection(21, false);

            //set the active profile
            headerResult.setActiveProfile(profile);
        }

        //result.updateBadge(4, new StringHolder(10 + ""));
    }

    private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
            if (drawerItem instanceof Nameable) {
                Log.i("material-drawer", "DrawerItem: " + ((Nameable) drawerItem).getName() + " - toggleChecked: " + isChecked);
            } else {
                Log.i("material-drawer", "toggleChecked: " + isChecked);
            }
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = result.saveInstanceState(outState);
        //add the values which need to be saved from the accountHeader to the bundle
        outState = headerResult.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            //super.onBackPressed();
            this.moveTaskToBack(true);

        }
    }


    /**
     * Setup click events on the Navigation View Items.
     */
        /*
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

                return true;
            }

        });


        /**
         * Setup Drawer Toggle of the Toolbar
         */


        /*
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name,
                R.string.app_name);

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();

        */

    //  presentShowcaseView(500);




    /*
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            this.finish();
        }
    }
    */
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

        // sequence.addSequenceItem(getNavButtonView(toolbar),
        //   "Use this button to navigate between orders and products", "GOT IT");


        sequence.start();
    }

    private ImageButton getNavButtonView(Toolbar toolbar) {
        for (int i = 0; i < toolbar.getChildCount(); i++)
            if (toolbar.getChildAt(i) instanceof ImageButton)
                return (ImageButton) toolbar.getChildAt(i);

        return null;
    }


    @Override
    public void loginSuccess() {


    }


    public Boolean isOutofStock() {

        return outStock;

    }

    public void setOutofStock(Boolean outStock) {

        this.outStock = outStock;

    }

}
