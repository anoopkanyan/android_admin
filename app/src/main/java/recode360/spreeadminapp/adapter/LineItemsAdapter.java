package recode360.spreeadminapp.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import recode360.spreeadminapp.R;
import recode360.spreeadminapp.app.AppController;
import recode360.spreeadminapp.app.Config;
import recode360.spreeadminapp.models.LineItems;
import recode360.spreeadminapp.models.Orders;


public class LineItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;   //for header
    private static final int TYPE_ITEM = 1;     //for list of cards of LineItems


    private List<LineItems> LineItemsList;
    private Orders order;   //for header of the list
    private Activity activity;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    private EditPlayerAdapterCallback callback;


    public LineItemsAdapter(Orders order, List<LineItems> LineItemsList, Activity activity) {
        this.order = order;
        this.LineItemsList = LineItemsList;
        this.activity = activity;
    }


    @Override
    public int getItemCount() {
        return LineItemsList.size() + 1;
    }


    //    need to override this method
    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int i) {

        if (holder instanceof VHHeader) {
            VHHeader VHheader = (VHHeader) holder;
            VHheader.txtOrderNo.setText(order.getNumber());
            VHheader.txtOrderId.setText(Integer.toString(order.getId()));
            VHheader.txtQuantity.setText(Integer.toString(order.getTotal_quantity()));
            VHheader.txtTotal.setText(order.getDisplay_total());
            VHheader.txtTotal.setText(order.getDisplay_total());
            VHheader.txtItemTotal.setText(order.getDisplay_item_total());


            VHheader.shipmentsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //shipments button clicked, notify the activity about it
                    callback.shipmentsButtonPressed();

                }
            });


            VHheader.billDetailsButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //bill details button clicked, notify the adapter about it
                    callback.billDetailsButtonPressed();
                }
            });


        } else if (holder instanceof LineItemsViewHolder) {

            final LineItems item = LineItemsList.get(i - 1);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                }
            });

            LineItemsViewHolder lineItemsViewHolder = (LineItemsViewHolder) holder;

            lineItemsViewHolder.vName.setText((item.getName()));
            lineItemsViewHolder.vId.setText(Integer.toString(item.getId()));
            lineItemsViewHolder.vQuantity.setText(Integer.toString(item.getQuantity()));
            lineItemsViewHolder.vPrice.setText(item.getSingle_display_amount());
            lineItemsViewHolder.vTotalPrice.setText(item.getDisplay_price());
            lineItemsViewHolder.vSku.setText(item.getSku());

            if (imageLoader == null)
                imageLoader = AppController.getInstance().getImageLoader();

            if (URLUtil.isValidUrl(item.getTemp_img())) {
                lineItemsViewHolder.image.setImageUrl(item.getTemp_img(), imageLoader);
            } else {
                lineItemsViewHolder.image.setImageUrl(Config.URL_STORE + item.getTemp_img(), imageLoader);

            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        if (i == TYPE_HEADER) {
            //inflate the header
            final View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.header_orders_activity, viewGroup, false);


            return new VHHeader(itemView);

        } else if (i == TYPE_ITEM) {
            //inflate the cards for LineItems
            final View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.line_items_card_view, viewGroup, false);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            return new LineItemsViewHolder(itemView);
        }
        throw new RuntimeException("there is no type that matches the type");
    }

    public static class LineItemsViewHolder extends RecyclerView.ViewHolder {
        protected TextView vName;
        protected TextView vId;
        protected TextView vPrice;
        protected TextView vQuantity;
        protected TextView vTotalPrice;
        protected TextView vSku;
        NetworkImageView image;


        public LineItemsViewHolder(View v) {
            super(v);
            vName = (TextView) v.findViewById(R.id.order_line_item_name);
            vId = (TextView) v.findViewById(R.id.ListItemId);
            vQuantity = (TextView) v.findViewById(R.id.ListItemQuantity);
            vPrice = (TextView) v.findViewById(R.id.ListItemPrice);
            vTotalPrice = (TextView) v.findViewById(R.id.ListItemTotalPrice);
            vSku = (TextView) v.findViewById(R.id.ListItemSku);
            image = (NetworkImageView)
                    v.findViewById(R.id.img_thumbnail);

        }
    }


    class VHHeader extends RecyclerView.ViewHolder {
        protected TextView txtOrderNo;
        protected TextView txtOrderId;
        protected TextView txtItemTotal;
        protected TextView txtQuantity;
        protected TextView txtTotal;
        protected Button shipmentsButton;
        protected Button billDetailsButton;

        public VHHeader(View itemView) {
            super(itemView);
            this.txtOrderNo = (TextView) itemView.findViewById(R.id.order_no);
            this.txtOrderId = (TextView) itemView.findViewById(R.id.order_id);
            this.txtItemTotal = (TextView) itemView.findViewById(R.id.item_total);
            this.txtQuantity = (TextView) itemView.findViewById(R.id.quantity);
            this.txtTotal = (TextView) itemView.findViewById(R.id.total);
            this.shipmentsButton = (Button) itemView.findViewById(R.id.shipments_button);
            this.billDetailsButton = (Button) itemView.findViewById(R.id.bill_details_button);


        }
    }


    private View.OnClickListener onClickListener(final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Clicked", "Clicked");

            }
        };
    }


    public void setCallback(EditPlayerAdapterCallback callback) {

        this.callback = callback;
    }


    public interface EditPlayerAdapterCallback {

        public void shipmentsButtonPressed();

        public void billDetailsButtonPressed();

    }

}