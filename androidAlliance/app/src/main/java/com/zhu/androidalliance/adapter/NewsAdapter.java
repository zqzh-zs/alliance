package com.zhu.androidalliance.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.zhu.androidalliance.R;
import com.zhu.androidalliance.pojo.dataObject.NewsInfo;
import com.zhu.androidalliance.utils.DateFormatUtil;
import com.zhu.androidalliance.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.SimpleFormatter;

public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<NewsInfo> dataList;
    private OnItemClickListener itemClickListener;

    public NewsAdapter(List<NewsInfo> dataList) {
        this.dataList = dataList;
    }
    // 新增：清空数据方法
    public void clearData() {
        dataList.clear();
        notifyDataSetChanged();
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public void updateData(List<NewsInfo> newData) {
        dataList.clear();
        dataList.addAll(newData);
        notifyDataSetChanged();
    }

    public void appendData(List<NewsInfo> moreData) {
        int startPosition = dataList.size();
        dataList.addAll(moreData);
        notifyItemRangeInserted(startPosition, moreData.size());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NewsViewHolder) {
            NewsViewHolder viewHolder = (NewsViewHolder) holder;
            NewsInfo newsInfo = dataList.get(position);

            viewHolder.tvTitle.setText(newsInfo.getTitle());
            viewHolder.tvDescription.setText(newsInfo.getContent());
            viewHolder.tvTime.setText(DateFormatUtil.format(newsInfo.getCreateTime()));
            viewHolder.tvAuthor.setText(newsInfo.getAuthor());

            // 使用Glide加载图片
            if (!TextUtils.isEmpty(newsInfo.getNewsImage())) {
                ImageLoader.loadImage(viewHolder.itemView.getContext(),newsInfo.getNewsImage(),viewHolder.image,R.drawable.ic_placeholder);

            } else {
                viewHolder.image.setImageResource(R.drawable.ic_placeholder);
            }

            // 点击事件
            viewHolder.itemView.setOnClickListener(v -> {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(newsInfo);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvTime, tvAuthor;
        ImageView image;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.itemTrendTitle);
            tvDescription = itemView.findViewById(R.id.itemTrendDescription);
            tvTime = itemView.findViewById(R.id.itemTrendTime);
            tvAuthor = itemView.findViewById(R.id.itemTrendAuthor);
            image = itemView.findViewById(R.id.itemTrendImage);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(NewsInfo newsInfo);
    }
}