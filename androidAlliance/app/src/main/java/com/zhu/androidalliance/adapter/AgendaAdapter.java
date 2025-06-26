package com.zhu.androidalliance.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zhu.androidalliance.R;
import com.zhu.androidalliance.pojo.dataObject.AgendaItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AgendaAdapter extends RecyclerView.Adapter<AgendaAdapter.ViewHolder>{

    private final List<AgendaItem> agendaItems;

    public AgendaAdapter(List<AgendaItem> agendaItems) {
        this.agendaItems = agendaItems != null ? agendaItems : new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_agenda, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AgendaItem item = agendaItems.get(position);

        // 格式化时间
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        holder.tvTime.setText(sdf.format(item.getStartTime()));

        holder.tvTitle.setText(item.getTitle());
        holder.tvDescription.setText(item.getDescription());
    }

    @Override
    public int getItemCount() {
        return agendaItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime;
        TextView tvTitle;
        TextView tvDescription;

        ViewHolder(View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }
    }
}
