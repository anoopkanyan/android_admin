package recode360.spreeadminapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;

import java.util.List;

import recode360.spreeadminapp.R;
import recode360.spreeadminapp.app.AppController;
import recode360.spreeadminapp.models.Packages;

/**
 * Adapter class to bind data(product details) to the RecycleView
 */

public class PackagesRecyclerViewAdapter extends RecyclerView.Adapter<PackagesViewHolders> {

    private List<Packages> packageList;
    private Context context;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public PackagesRecyclerViewAdapter(Context context, List<Packages> packageList) {
        this.packageList = packageList;
        this.context = context;
    }

    @Override
    public PackagesViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.packages_list, null);
        PackagesViewHolders rcv = new PackagesViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(PackagesViewHolders holder, final int position) {


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                packageList.get(position).getName();

                Log.d("The PACKAGE IS",packageList.get(position).getName());
                SharedPreferences spppp = context.getSharedPreferences("package_details", 0);
                SharedPreferences.Editor editors = spppp.edit();
                editors.putString("length", packageList.get(position).getDimension_length());
                editors.putString("width", packageList.get(position).getDimension_width());
                editors.putString("height", packageList.get(position).getDimension_height());
                editors.putString("distance_unit", packageList.get(position).getDimension_unit());
                editors.putString("template", packageList.get(position).getName());
                editors.commit();

                //Intent returnIntent = new Intent();
                //returnIntent.putExtra("result", packageList.get(position).getName());
                ((Activity) context).finish();

            }
        });


        holder.packageName.setText(packageList.get(position).getName().substring(5));
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        holder.packagePhoto.setImageUrl(packageList.get(position).getPhoto(), imageLoader);
    }

    @Override
    public int getItemCount() {
        return this.packageList.size();
    }
}