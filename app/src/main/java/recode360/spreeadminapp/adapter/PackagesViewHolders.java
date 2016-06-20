package recode360.spreeadminapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import recode360.spreeadminapp.R;
import recode360.spreeadminapp.models.Packages;

/**
 * holds references to View components of each item i.e our products
 */
public class PackagesViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView packageName;
    public NetworkImageView packagePhoto;
    private List<Packages> packageList;


    public PackagesViewHolders(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        packageName = (TextView) itemView.findViewById(R.id.package_name);
        packagePhoto = (NetworkImageView) itemView
                .findViewById(R.id.package_photo);
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(view.getContext(), "Clicked Position = " + getPosition(), Toast.LENGTH_SHORT).show();

    }
}
