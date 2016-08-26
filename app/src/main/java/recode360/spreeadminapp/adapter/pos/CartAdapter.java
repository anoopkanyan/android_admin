package recode360.spreeadminapp.adapter.pos;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.List;

import recode360.spreeadminapp.R;
import recode360.spreeadminapp.models.Product;

/**
 * Adapter to handle all the products in a cart.
 */
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {

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


    private BigDecimal priceTotal;
    private int qtyTotal;


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
        public TextView productName, productPrice, productQty, productPriceTotal;
        public ImageView plusButton, minusButton, trashButton;
        // public NetworkImageView productImage;
        // ImageLoader imageLoader = AppController.getInstance().getImageLoader();


        public MyViewHolder(View view) {
            super(view);
            productName = (TextView) view.findViewById(R.id.product_name_pos);
            productPrice = (TextView) view.findViewById(R.id.product_price_pos);
            productQty = (TextView) view.findViewById(R.id.product_quantity_cart);
            productPriceTotal = (TextView) view.findViewById(R.id.product_price_total_pos);

            plusButton = (ImageView) view.findViewById(R.id.plus_button);
            minusButton = (ImageView) view.findViewById(R.id.minus_button);
            trashButton = (ImageView) view.findViewById(R.id.product_trash_cart);
            //   productImage = (NetworkImageView) view.findViewById(R.id.product_image);
        }


    }


    public CartAdapter(Context mContext, List<Product> productList) {
        this.mContext = mContext;
        this.productList = productList;


    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_order_pos, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final Product product = productList.get(position);

        //callback.deletePressed();

        holder.productName.setText(product.getName());
        holder.productPrice.setText("$" + product.getPrice().toString() + "X" + Integer.toString(product.getCart_qty()));
        holder.productPriceTotal.setText("$" + (new BigDecimal(product.getCart_qty())).multiply(product.getPrice()));
        holder.productQty.setText(Integer.toString(product.getCart_qty()));

        //holder.productImage.setImageUrl(product.getImage(), holder.imageLoader);

        //load image and actions for buttons

        holder.plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Product pr = productList.get(position);
                if (pr.getCart_qty() > 0) {
                    pr.setCart_qty(pr.getCart_qty() + 1);
                    productList.remove(position);
                    productList.add(position, pr);
                    priceTotal = priceTotal.add(pr.getPrice());
                    qtyTotal++;
                    holder.productQty.setText(Integer.toString(productList.get(position).getCart_qty()));
                    holder.productPrice.setText("$" + product.getPrice().toString() + "X" + Integer.toString(product.getCart_qty()));
                    holder.productPriceTotal.setText("$" + (new BigDecimal(product.getCart_qty())).multiply(product.getPrice()));

                } else {
                    //passList.add(pr);
                    //pos = passList.indexOf(pr);
                    //array[pos] = 1;
                    pr.setCart_qty(1);
                    productList.remove(position);
                    productList.add(position, pr);
                    qtyTotal++;
                    priceTotal = priceTotal.add(pr.getPrice());
                    holder.productQty.setText(Integer.toString(productList.get(position).getCart_qty()));
                    holder.productPrice.setText("$" + product.getPrice().toString() + "X" + Integer.toString(product.getCart_qty()));
                    holder.productPriceTotal.setText("$" + (new BigDecimal(product.getCart_qty())).multiply(product.getPrice()));

                }

                callback.deletePressed();


            }
        });

        holder.minusButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Product pr = productList.get(position);

                if (pr.getCart_qty() > 0) {
                    pr.setCart_qty(pr.getCart_qty() - 1);
                    productList.remove(position);
                    productList.add(position, pr);
                    qtyTotal--;
                    priceTotal = priceTotal.subtract(pr.getPrice());

                    holder.productQty.setText(Integer.toString(productList.get(position).getCart_qty()));
                    holder.productPrice.setText("$" + product.getPrice().toString() + "X" + Integer.toString(product.getCart_qty()));
                    holder.productPriceTotal.setText("$" + (new BigDecimal(product.getCart_qty())).multiply(product.getPrice()));
                }

                callback.deletePressed();
            }
        });

        holder.trashButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                priceTotal = priceTotal.subtract(product.getPrice().multiply(new BigDecimal(product.getCart_qty())));

                qtyTotal = qtyTotal - product.getCart_qty();

                productList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, productList.size());


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
