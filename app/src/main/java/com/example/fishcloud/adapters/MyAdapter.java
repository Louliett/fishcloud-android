package com.example.fishcloud.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fishcloud.R;
import com.example.fishcloud.interfaces.RecyclerViewClickListener;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private Context context;
    private static RecyclerViewClickListener itemListener;
    private Integer[] mDataset;


    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView image;

        public MyViewHolder(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.menu_icon);
            v.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            itemListener.recyclerViewListClicked(v, this.getLayoutPosition());
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(Context context, RecyclerViewClickListener itemListener, Integer[] myDataset) {
        this.context = context;
        this.itemListener = itemListener;
        mDataset = myDataset;


    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.menu_icons, parent, false);

        return new MyViewHolder(itemView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Integer imageId = mDataset[position];
        holder.image.setImageResource(imageId);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}