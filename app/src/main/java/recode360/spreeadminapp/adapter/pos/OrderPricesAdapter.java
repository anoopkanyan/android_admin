package recode360.spreeadminapp.adapter.pos;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import recode360.spreeadminapp.R;
import recode360.spreeadminapp.models.Product;

/**
 * Adapter to handle all the products in a cart.
 */
public class OrderPricesAdapter extends RecyclerView.Adapter<OrderPricesAdapter.MyViewHolder> {

    private Context mContext;

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    private List<Product> productList;
    private List<Product> passList;
    private int[] array;


    public int[] getArray() {
        return array;
    }

    public void setArray(int[] array) {
        this.array = array;
    }

    public List<Product> getPassList() {
        return passList;
    }

    public void setPassList(List<Product> passList) {
        this.passList = passList;
    }


    private Float priceTotal = 0.0f;
    private int qtyTotal = 0;


    public int getQtyTotal() {
        return qtyTotal;
    }

    public void setQtyTotal(int qtyTotal) {
        this.qtyTotal = qtyTotal;
    }

    private EditPlayerAdapterCallback callback;

    public Float getPriceTotal() {
        return priceTotal;
    }

    public void setPriceTotal(Float priceTotal) {
        this.priceTotal = priceTotal;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView productName, productPrice, productPriceTotal;


        public MyViewHolder(View view) {
            super(view);
            productName = (TextView) view.findViewById(R.id.product_name_pos);
            productPrice = (TextView) view.findViewById(R.id.product_price_pos);
            productPriceTotal = (TextView) view.findViewById(R.id.product_price_total_pos);

        }


    }


    public OrderPricesAdapter(Context mContext, List<Product> productList) {
        this.mContext = mContext;
        this.productList = productList;


    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_order_prices, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final Product product = productList.get(position);

        priceTotal = priceTotal + (product.getPrice().floatValue() * product.getCart_qty());
        qtyTotal = qtyTotal + product.getCart_qty();

        callback.deletePressed();

        holder.productName.setText(product.getName());
        holder.productPrice.setText("$" + product.getPrice().toString() + "X" + Integer.toString(product.getCart_qty()));
        holder.productPriceTotal.setText("$" + product.getCart_qty() * product.getPrice().floatValue());

    }


    @Override
    public int getItemCount() {
        return productList.size();
    }


    public void setCallback(EditPlayerAdapterCallback callback) {

        this.callback = callback;
    }


    public interface EditPlayerAdapterCallback {
        public void deletePressed();
    }

}

