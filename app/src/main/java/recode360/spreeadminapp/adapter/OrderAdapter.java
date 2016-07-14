package recode360.spreeadminapp.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import recode360.spreeadminapp.Activities.OrdersActivity;
import recode360.spreeadminapp.R;
import recode360.spreeadminapp.models.Orders;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Orders> ordersList;
    private Activity activity;


    public OrderAdapter(List<Orders> ordersList, Activity activity) {
        this.ordersList = ordersList;
        this.activity = activity;
    }


    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    @Override
    public void onBindViewHolder(final OrderViewHolder orderViewHolder, int i) {
        final Orders order = ordersList.get(i);

        orderViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //first check if the order's state i.e ship,pending or cart and start a new activity correspondingly
                Intent i = new Intent(activity, OrdersActivity.class);
                i.putExtra("order_no", order.getNumber());
                i.putExtra("shipment", order.getShipment_state());
                activity.startActivity(i);

            }
        });

        orderViewHolder.vNumber.setText((order.getNumber()));
        orderViewHolder.vState.setText(order.getState().substring(0, 1).toUpperCase() + order.getState().substring(1).toLowerCase());
        orderViewHolder.vDate.setText(order.getUpdated_at().toString().substring(0, 10));

        if (order.getPayment_state().equals("balance_due")) {
            orderViewHolder.vPayment.setTextColor(Color.RED);
            orderViewHolder.vPayment.setText("Balance Due");
        } else if (order.getPayment_state().equals("paid")) {
            orderViewHolder.vPayment.setTextColor(Color.parseColor("#ff33a93c"));
            orderViewHolder.vPayment.setText("Paid");
        }

        if (order.getShipment_state().equals("pending")) {
            orderViewHolder.vShipment.setTextColor(Color.RED);
            orderViewHolder.vShipment.setText("Pending Shipment");
        } else if (order.getShipment_state().equals("ready")) {
            //if the order is in ready state, text color is set green to indicate it's still active
            orderViewHolder.vShipment.setTextColor(Color.parseColor("#FF5722"));
            orderViewHolder.vShipment.setText("Ready to Ship");
        } else if (order.getShipment_state().equals("shipped")) {
            //show normal text color if the order has been shipped already
            orderViewHolder.vShipment.setTextColor((activity.getResources().getColor(R.color.colorPrimary)));
            orderViewHolder.vShipment.setText("Shipped");
        }

        orderViewHolder.vDisplayTotal.setText(order.getDisplay_total());

    }

    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.orders_card_view, viewGroup, false);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });
        return new OrderViewHolder(itemView);
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        protected TextView vNumber;
        protected TextView vState;
        protected TextView vDate;
        protected TextView vTitle;
        protected TextView vPayment;
        protected TextView vDisplayTotal;
        protected TextView vShipment;

        public OrderViewHolder(View v) {
            super(v);
            vNumber = (TextView) v.findViewById(R.id.orderNumber);
            vState = (TextView) v.findViewById(R.id.orderState);
            vDate = (TextView) v.findViewById(R.id.orderDate);
            vTitle = (TextView) v.findViewById(R.id.title);
            vPayment = (TextView) v.findViewById(R.id.orderPaymentState);
            vDisplayTotal = (TextView) v.findViewById(R.id.orderDisplayTotal);
            vShipment = (TextView) v.findViewById(R.id.orderShipmentState);
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


}