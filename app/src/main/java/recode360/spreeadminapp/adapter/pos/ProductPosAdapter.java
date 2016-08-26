package recode360.spreeadminapp.adapter.pos;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import recode360.spreeadminapp.R;
import recode360.spreeadminapp.app.AppController;
import recode360.spreeadminapp.models.Product;

/**
 * Created by anoopkumar on 07/07/16.
 */

public class ProductPosAdapter extends RecyclerView.Adapter<ProductPosAdapter.MyViewHolder> {

    private Context mContext;

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    private List<Product> productList;

    public List<Product> getPassList() {
        return passList;
    }

    public void setPassList(List<Product> passList) {
        this.passList = passList;
    }

    public int[] getArray() {
        return array;
    }

    public void setArray(int[] array) {
        this.array = array;
    }

    private List<Product> passList;
    private int[] array;

    private BigDecimal priceTotal = BigDecimal.ZERO;
    private int qtyTotal = 0;


    public int getQtyTotal() {
        return qtyTotal;
    }

    public void setQtyTotal(int qtyTotal) {
        this.qtyTotal = qtyTotal;
    }

    private EditPlayerAdapterCallback callback;

    public BigDecimal getPriceTotal() {
        return priceTotal;
    }

    public void setPriceTotal(BigDecimal priceTotal) {
        this.priceTotal = priceTotal;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView productName, productPrice, productQty;
        public ImageView plusButton, minusButton;
        public NetworkImageView productImage;
        ImageLoader imageLoader = AppController.getInstance().getImageLoader();


        public MyViewHolder(View view) {
            super(view);
            productName = (TextView) view.findViewById(R.id.product_name);
            productPrice = (TextView) view.findViewById(R.id.product_price);
            productQty = (TextView) view.findViewById(R.id.product_quantity);

            plusButton = (ImageView) view.findViewById(R.id.cart_plus_img);
            minusButton = (ImageView) view.findViewById(R.id.cart_minus_img);
            productImage = (NetworkImageView) view.findViewById(R.id.product_image);
        }


    }


    public ProductPosAdapter(Context mContext, List<Product> productList) {
        this.mContext = mContext;
        this.productList = productList;
        passList = new ArrayList<Product>();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_product_pos, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.setIsRecyclable(false);

        final Product product = productList.get(position);
        holder.productName.setText(product.getName());
        holder.productPrice.setText("$ " + product.getPrice().toString());
        holder.productImage.setImageUrl(product.getImage(), holder.imageLoader);
        holder.productQty.setText(Integer.toString(productList.get(position).getCart_qty()));

        //load image and actions for buttons

        array = new int[productList.size()];

        holder.plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Product pr = productList.get(position);
                if (pr.getCart_qty() > 0) {
                    passList.remove(pr);
                    pr.setCart_qty(pr.getCart_qty() + 1);
                    productList.remove(position);
                    productList.add(position, pr);
                    if (passList.size() > 0) {
                        passList.add(position, pr);
                    } else {
                        passList.add(pr);
                    }

                    priceTotal = priceTotal.add(pr.getPrice());
                    qtyTotal++;
                    holder.productQty.setText(Integer.toString(productList.get(position).getCart_qty()));
                } else {
                    //passList.add(pr);
                    //pos = passList.indexOf(pr);
                    //array[pos] = 1;
                    pr.setCart_qty(1);
                    productList.remove(position);
                    productList.add(position, pr);
                    passList.add(pr);
                    qtyTotal++;
                    priceTotal = priceTotal.add(pr.getPrice());
                    holder.productQty.setText(Integer.toString(productList.get(position).getCart_qty()));

                }

                callback.deletePressed();


            }
        });

        holder.minusButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Product pr = productList.get(position);

                if (pr.getCart_qty() > 1) {
                    passList.remove(pr);
                    pr.setCart_qty(pr.getCart_qty() - 1);
                    productList.remove(position);
                    productList.add(position, pr);
                    passList.add(pr);
                    qtyTotal--;
                    priceTotal = priceTotal.subtract(pr.getPrice());

                    holder.productQty.setText(Integer.toString(productList.get(position).getCart_qty()));
                } else if (pr.getCart_qty() == 1) {

                    pr.setCart_qty(pr.getCart_qty() - 1);
                    productList.remove(position);
                    productList.add(position, pr);
                    passList.remove(pr);
                    qtyTotal--;
                    priceTotal = priceTotal.subtract(pr.getPrice());

                    holder.productQty.setText(Integer.toString(productList.get(position).getCart_qty()));

                }

                callback.deletePressed();
            }
        });

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
