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
                i.putExtra("shipment",order.getShipment_state());
                activity.startActivity(i);

            }
        });

        orderViewHolder.vNumber.setText((order.getNumber()));
        orderViewHolder.vState.setText(order.getState());
        orderViewHolder.vQuantity.setText(Integer.toString(order.getTotal_quantity()));

        if (order.getPayment_state().equals("balance_due")) {
            orderViewHolder.vPayment.setTextColor(Color.RED);
            orderViewHolder.vPayment.setText(order.getPayment_state());
        } else if (order.getPayment_state().equals("paid")) {
            orderViewHolder.vPayment.setTextColor(Color.parseColor("#ff33a93c"));
            orderViewHolder.vPayment.setText(order.getPayment_state());
        }

        if (order.getShipment_state().equals("pending")) {
            orderViewHolder.vShipment.setTextColor(Color.RED);
            orderViewHolder.vShipment.setText("shipment " + order.getShipment_state());
        } else if (order.getShipment_state().equals("ready")) {
            orderViewHolder.vShipment.setTextColor(Color.parseColor("#ff33a93c"));
            orderViewHolder.vShipment.setText("shipment " + order.getShipment_state());
        } else if (order.getShipment_state().equals("shipped")) {
            orderViewHolder.vShipment.setTextColor(Color.parseColor("#ff33a93c"));
            orderViewHolder.vShipment.setText(order.getShipment_state());
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
        protected TextView vQuantity;
        protected TextView vTitle;
        protected TextView vPayment;
        protected TextView vDisplayTotal;
        protected TextView vShipment;

        public OrderViewHolder(View v) {
            super(v);
            vNumber = (TextView) v.findViewById(R.id.orderNumber);
            vState = (TextView) v.findViewById(R.id.orderState);
            vQuantity = (TextView) v.findViewById(R.id.orderQuantity);
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