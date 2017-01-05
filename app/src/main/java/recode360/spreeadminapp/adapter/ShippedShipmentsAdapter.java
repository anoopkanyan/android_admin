package recode360.spreeadminapp.adapter;


/**
 * adapter  class for shipments under the shipped state
 */


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import recode360.spreeadminapp.R;
import recode360.spreeadminapp.models.Address;
import recode360.spreeadminapp.models.Shipments;

public class ShippedShipmentsAdapter extends RecyclerView.Adapter<ShippedShipmentsAdapter.ViewHolder> {
    private List<Shipments> shipments;
    private Context context;
    private Address shipAddress;

    public ShippedShipmentsAdapter(List<Shipments> shipments, Address shipAddress, Context context) {
        this.shipments = shipments;
        this.shipAddress = shipAddress;
        this.context = context;
    }

    public ShippedShipmentsAdapter(List<Shipments> shipments, Context context) {
        this.shipments = shipments;
        this.context = context;
    }


    @Override
    public ShippedShipmentsAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_shipped_shipments, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ShippedShipmentsAdapter.ViewHolder viewHolder, final int i) {

        viewHolder.shipment_number.setText((shipments.get(i).getNumber()));
        viewHolder.shipment_state.setText(shipments.get(i).getState());
        viewHolder.shipment_stock_location_name.setText(shipments.get(i).getStock_location_name());
        viewHolder.order_id.setText(shipments.get(i).getOrder_id());
        viewHolder.label_price.setText(shipments.get(i).getCost());

        viewHolder.track_shipment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //open the tracking url in a web browser
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(shipments.get(i).getTracking()));
                context.startActivity(browserIntent);

                /*
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Label downloaded successfully.Check your downloads folder.  ")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do nothing here
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                */

            }
        });


        viewHolder.reprint_label_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //download the label which has already been paid for
                //open the tracking url in a web browser


                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(shipments.get(i).getLabel_url()));
                context.startActivity(browserIntent);

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Label downloaded successfully.Check your downloads folder.  ")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do nothing here
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

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
        private TextView order_id;
        private TextView label_price;
        private Button track_shipment_button;
        private Button reprint_label_button;

        public ViewHolder(View view) {
            super(view);

            shipment_number = (TextView) view.findViewById(R.id.shipmentNumber);
            shipment_state = (TextView) view.findViewById(R.id.shipmentState);
            shipment_stock_location_name = (TextView) view.findViewById(R.id.stockLocation);
            order_id = (TextView) view.findViewById(R.id.orderId);
            label_price = (TextView) view.findViewById(R.id.labelPrice);
            track_shipment_button = (Button) view.findViewById(R.id.trackShipmentButton);
            reprint_label_button = (Button) view.findViewById(R.id.reprintLabelButton);
        }
    }

}
