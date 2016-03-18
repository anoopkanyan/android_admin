package recode360.spreeadminapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import recode360.spreeadminapp.R;
import recode360.spreeadminapp.models.Taxonomies;

public class TaxonomiesAdapter extends RecyclerView.Adapter<TaxonomiesAdapter.MyViewHolder> {

    private List<Taxonomies> taxonomiesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView taxonomyTitle;

        public MyViewHolder(View view) {
            super(view);
            taxonomyTitle = (TextView) view.findViewById(R.id.taxonomy_name);
        }
    }


    public TaxonomiesAdapter(List<Taxonomies> taxonomiesList) {
        this.taxonomiesList = taxonomiesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.taxonomies_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Taxonomies taxonomy = taxonomiesList.get(position);
        holder.taxonomyTitle.setText(taxonomy.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //first check if the order's state i.e ship,pending or cart and start a new activity correspondingly
                //Intent i = new Intent(activity, OrdersActivity.class);
                //i.putExtra("order_no", order.getNumber());
                //i.putExtra("shipment", order.getShipment_state());
                //activity.startActivity(i);

            }
        });


    }

    @Override
    public int getItemCount() {
        return taxonomiesList.size();
    }
}
