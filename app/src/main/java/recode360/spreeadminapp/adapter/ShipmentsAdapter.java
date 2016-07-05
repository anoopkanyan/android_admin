package recode360.spreeadminapp.adapter;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import recode360.spreeadminapp.Activities.CreateLabelActivity;
import recode360.spreeadminapp.R;
import recode360.spreeadminapp.models.Address;
import recode360.spreeadminapp.models.Shipments;

public class ShipmentsAdapter extends RecyclerView.Adapter<ShipmentsAdapter.ViewHolder> {
    private List<Shipments> shipments;
    private Context context;
    private Address shipAddress;

    public ShipmentsAdapter(List<Shipments> shipments, Address shipAddress, Context context) {
        this.shipments = shipments;
        this.shipAddress = shipAddress;
        this.context = context;
    }

    @Override
    public ShipmentsAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_ready_shipments, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ShipmentsAdapter.ViewHolder viewHolder, final int i) {

        viewHolder.shipment_number.setText((shipments.get(i).getNumber()));
        viewHolder.shipment_state.setText(shipments.get(i).getState());
        viewHolder.shipment_stock_location_name.setText(shipments.get(i).getStock_location_name());
        viewHolder.order_id.setText(shipments.get(i).getOrder_id());

        viewHolder.create_label_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /// button click event
                Log.e("Button is working man", "working");

                Intent intent = new Intent(context, CreateLabelActivity.class);
                intent.putExtra("ShipAddress", shipAddress);
                intent.putExtra("shipment_number",shipments.get(i).getNumber());


                // intent.putExtra("key", value); //put Order and shipment details
                context.startActivity(intent);


            }
        });
    }

    @Override
    public int getItemCount() {
        return shipments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView shipment_number;
        private TextView shipment_state;
        private TextView shipment_stock_location_name;
        private Button create_label_button;
        private TextView order_id;

        public ViewHolder(View view) {
            super(view);

            shipment_number = (TextView) view.findViewById(R.id.shipmentNumber);
            shipment_state = (TextView) view.findViewById(R.id.shipmentState);
            shipment_stock_location_name = (TextView) view.findViewById(R.id.stockLocation);
            order_id = (TextView) view.findViewById(R.id.orderId);
            create_label_button = (Button) view.findViewById(R.id.labelButton);
        }
    }

}
