package recode360.spreeadminapp.Activities.pos;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;

import recode360.spreeadminapp.R;
import recode360.spreeadminapp.adapter.pos.CartAdapter;
import recode360.spreeadminapp.models.Product;

public class OrderPosActivity extends AppCompatActivity implements CartAdapter.EditPlayerAdapterCallback {

    private ArrayList<Product> items;
    private int array[];
    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private Toolbar toolbar;
    private TextView totalPriceView;
    private TextView totalQtyView;
    private TextView subTotalView;
    private int done = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_pos);

        Intent intent = this.getIntent();
        items = (ArrayList<Product>) intent.getSerializableExtra("products");


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Cart Summary");

        totalPriceView = (TextView) findViewById(R.id.cart_item_price);
        totalQtyView = (TextView) findViewById(R.id.cart_item_no);



        totalPriceView.setText("$" + (intent.getStringExtra("price")));
        totalQtyView.setText(Integer.toString(intent.getIntExtra("quantity", 0)));


        recyclerView = (RecyclerView) findViewById(R.id.cart_pos_recycler_view);
        adapter = new CartAdapter(this, items);

        adapter.setPriceTotal(new BigDecimal(intent.getStringExtra("price")));
        adapter.setQtyTotal(intent.getIntExtra("quantity", 0));

        // use a linear layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.setCallback(this);

        TextView cartView = (TextView) findViewById(R.id.cart_checkout_text);

        cartView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Going to cart_menu...", Snackbar.LENGTH_SHORT).show();
                launchPaymentActivity();

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


    @Override
    public void deletePressed() {
        totalPriceView.setText("$" + adapter.getPriceTotal().toString());
        totalQtyView.setText(Integer.toString(adapter.getQtyTotal()));
    }


    private void launchPaymentActivity() {

        Bundle information = new Bundle();

        ArrayList<Product> cart_products = (ArrayList<Product>) adapter.getProductList();
        information.putSerializable("products", cart_products);
        Intent intent = new Intent(OrderPosActivity.this, OrderPosAddressActivity.class);
        intent.putExtras(information);
        intent.putExtra("quantity", adapter.getQtyTotal());
        intent.putExtra("price", adapter.getPriceTotal().toString());
        startActivity(intent);

    }

}
