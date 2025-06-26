package com.zhu.androidalliance.adapter;

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
import com.zhu.androidalliance.pojo.dataObject.Meeting;
import com.zhu.androidalliance.utils.DateFormatUtil;

import java.util.ArrayList;
import java.util.List;

public class MeetingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Meeting> dataList;
    private final OnMeetingClickListener listener;

    private static final RequestOptions glideOptions = new RequestOptions()
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_error)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop();

    public MeetingAdapter(List<Meeting> dataList, OnMeetingClickListener listener) {
        this.dataList = dataList != null ? dataList : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meeting, parent, false);
        return new MeetingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Meeting meeting = dataList.get(position);
        if (holder instanceof MeetingViewHolder) {
            ((MeetingViewHolder) holder).bind(meeting, listener);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void updateData(List<Meeting> newDataList) {
        this.dataList.clear();
        if (newDataList != null) {
            this.dataList.addAll(newDataList);
        }
        notifyDataSetChanged();
    }


    public void appendData(List<Meeting> moreData) {
        if (moreData == null || moreData.isEmpty()) return;

        int startPosition = dataList.size();
        dataList.addAll(moreData);
        notifyItemRangeInserted(startPosition, moreData.size());
    }

    public void clearData() {
        dataList.clear();
        notifyDataSetChanged();
    }

    static class MeetingViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivMeetingImage;
        private final TextView tvMeetingTitle;
        private final TextView tvMeetingType;
        private final TextView tvMeetingDate;
        private final TextView tvMeetingLocation;
        private final TextView tvMeetingOrganizer;
        private final TextView tvMeetingSummary;

        public MeetingViewHolder(@NonNull View itemView) {
            super(itemView);
            ivMeetingImage = itemView.findViewById(R.id.ivMeetingImage);
            tvMeetingTitle = itemView.findViewById(R.id.tvMeetingTitle);
            tvMeetingType = itemView.findViewById(R.id.tvMeetingType);
            tvMeetingDate = itemView.findViewById(R.id.tvMeetingDate);
            tvMeetingLocation = itemView.findViewById(R.id.tvMeetingLocation);
            tvMeetingOrganizer = itemView.findViewById(R.id.tvMeetingOrganizer);
            tvMeetingSummary = itemView.findViewById(R.id.tvMeetingSummary);
        }

        public void bind(Meeting meeting, OnMeetingClickListener listener) {
            // 加载图片
            Glide.with(itemView.getContext())
                    .load(meeting.getImageUrl())
                    .apply(glideOptions)
                    .into(ivMeetingImage);

            tvMeetingTitle.setText(meeting.getTitle());
            tvMeetingType.setText(meeting.getType().getDisplayName());
            tvMeetingDate.setText(DateFormatUtil.formatDateRange(meeting.getStartTime(), meeting.getEndTime()));
            tvMeetingLocation.setText(meeting.getLocation());
            tvMeetingOrganizer.setText(meeting.getOrganizer());
            tvMeetingSummary.setText(meeting.getSummary());

            // 设置点击事件
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onMeetingClick(meeting);
                }
            });
        }
    }

    public interface OnMeetingClickListener {
        void onMeetingClick(Meeting meeting);
    }
}