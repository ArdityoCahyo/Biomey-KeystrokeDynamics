package com.kanjengdev.biomey.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.kanjengdev.biomey.R;
import com.kanjengdev.biomey.model.Contributor;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ContributorAdapter extends RecyclerView.Adapter<ContributorAdapter.ViewHolder> {

    private final Context context ;
    private final List<Contributor> item ;


    public ContributorAdapter(Context context, List<Contributor> item) {
        this.context = context;
        this.item = item;

    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view ;
        LayoutInflater mInflater = LayoutInflater.from(context);
        view = mInflater.inflate(R.layout.item_contributor,parent,false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.name.setText(item.get(position).getName());
        holder.occupation.setText(item.get(position).getOccupation());

        if (item.get(position).getImage() != 0){
            holder.icon.setVisibility(View.GONE);
            holder.image.setImageResource(item.get(position).getImage());
        }

    }

    @Override
    public int getItemCount() {
        return item.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView occupation;
        ImageView image;
        ImageView icon;
        CardView cardView ;

        public ViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name) ;
            occupation = (TextView) itemView.findViewById(R.id.occupation) ;
            image = (ImageView) itemView.findViewById(R.id.image);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            cardView = (CardView) itemView.findViewById(R.id.card_view);

        }

    }

}
