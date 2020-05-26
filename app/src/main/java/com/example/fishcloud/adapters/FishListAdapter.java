package com.example.fishcloud.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.fishcloud.DownloadImageTask;
import com.example.fishcloud.R;
import com.example.fishcloud.data.model.Fish;


import java.io.IOException;
import java.net.URL;
import java.util.List;

public class FishListAdapter extends RecyclerView.Adapter<FishListAdapter.MyViewHolder> {
    private Context context;
    private List<Fish> fishes;


    protected static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView length;
        TextView width;
        TextView weight;
        TextView location;
        TextView date;


        public MyViewHolder(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.fish_image);
            length = (TextView) v.findViewById(R.id.length);
            width = (TextView) v.findViewById(R.id.width);
            weight = (TextView) v.findViewById(R.id.weight);
            location = (TextView) v.findViewById(R.id.location);
            date = (TextView) v.findViewById(R.id.time);


        }

    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public FishListAdapter(Context context, List<Fish> fishes) {
        this.context = context;
        this.fishes = fishes;


    }

    // Create new views (invoked by the layout manager)
    @Override
    public FishListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_items, parent, false);

        return new FishListAdapter.MyViewHolder(itemView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(FishListAdapter.MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Fish f = fishes.get(position);
        new DownloadImageTask(holder.image).execute(f.getImgURL());
        holder.length.setText(Integer.toString(f.getLength()));
        holder.width.setText(Integer.toString(f.getWidth()));
        holder.weight.setText(String.valueOf(f.getWeight()));
        holder.location.setText(f.getLocation());
        holder.date.setText(f.getDate());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return fishes.size();
    }
}
