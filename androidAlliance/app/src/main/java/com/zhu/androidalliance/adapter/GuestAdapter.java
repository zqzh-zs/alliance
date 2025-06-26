package com.zhu.androidalliance.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.zhu.androidalliance.R;
import com.zhu.androidalliance.pojo.dataObject.Guest;

import java.util.ArrayList;
import java.util.List;

public class GuestAdapter extends RecyclerView.Adapter<GuestAdapter.ViewHolder> {

    private final List<Guest> guests;

    public GuestAdapter(List<Guest> guests) {
        this.guests = guests != null ? guests : new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_guest, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Guest guest = guests.get(position);
        holder.tvName.setText(guest.getName());
        holder.tvTitle.setText(guest.getTitle());
        holder.tvOrganization.setText(guest.getOrganization());
    }

    @Override
    public int getItemCount() {
        return guests.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvTitle;
        TextView tvOrganization;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvOrganization = itemView.findViewById(R.id.tvOrganization);
        }
    }
}
