package recode360.spreeadminapp.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import recode360.spreeadminapp.R;
import recode360.spreeadminapp.models.Licenses;

public class LicensesAdapter extends RecyclerView.Adapter<LicensesAdapter.MyViewHolder> {

    private List<Licenses> licensesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, description;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            description = (TextView) view.findViewById(R.id.description);
        }
    }


    public LicensesAdapter(List<Licenses> licensesList) {
        this.licensesList = licensesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.licenses_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Licenses licence = licensesList.get(position);
        holder.title.setText(licence.getTitle());
        holder.description.setText(licence.getDescription());
    }

    @Override
    public int getItemCount() {
        return licensesList.size();
    }

}
