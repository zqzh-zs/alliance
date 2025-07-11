package com.zhu.androidalliance.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.zhu.androidalliance.MeetingDetailActivity;
import com.zhu.androidalliance.NewsInfoDetailActivity;
import com.zhu.androidalliance.R;
import com.zhu.androidalliance.callback.DataCallback;
import com.zhu.androidalliance.decoration.HorizontalSpaceItemDecoration;
import com.zhu.androidalliance.adapter.MeetingAdapter;
import com.zhu.androidalliance.adapter.NewsAdapter;
import com.zhu.androidalliance.enums.MeetingType;
import com.zhu.androidalliance.pojo.dataObject.Meeting;
import com.zhu.androidalliance.pojo.dataObject.NewsInfo;
import com.zhu.androidalliance.utils.GetListUtil;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView rvFeaturedMeetings;
    private RecyclerView rvLatestNews;
    private MeetingAdapter featuredMeetingAdapter;
    private NewsAdapter newsAdapter;
    private Context mContext;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        rvFeaturedMeetings = view.findViewById(R.id.rvFeaturedMeetings);
        rvLatestNews = view.findViewById(R.id.rvLatestNews);

        setupFeaturedMeetings();
        setupLatestNews();

        return view;
    }


    private void setupFeaturedMeetings() {
        // 配置横向布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                mContext,  // 用mContext代替requireContext()，更安全
                LinearLayoutManager.HORIZONTAL,
                false
        );
        rvFeaturedMeetings.setLayoutManager(layoutManager);

        // 添加间距
        int spacing = getResources().getDimensionPixelSize(R.dimen.conference_spacing);
        rvFeaturedMeetings.addItemDecoration(new HorizontalSpaceItemDecoration(spacing));

        // 初始传入空列表，后续通过load方法更新
        featuredMeetingAdapter = new MeetingAdapter(new ArrayList<>(), meeting -> {
            openMeetingDetail(meeting);
        });
        rvFeaturedMeetings.setAdapter(featuredMeetingAdapter);

        // 加载数据（仅一次）
        loadFeaturedMeetings();
    }

    private void loadFeaturedMeetings() {
        GetListUtil.getAllMeetings(new DataCallback<Meeting>() {
            @Override
            public void onSuccess(List<Meeting> resultMeetings, int total) {
                // 检查Fragment是否还附着在Activity上
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    if (featuredMeetingAdapter != null) {
                        featuredMeetingAdapter.updateData(resultMeetings);
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                if (mContext != null) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(mContext, "获取会议列表失败", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }


    private void setupLatestNews() {
        // 配置垂直布局管理器
        rvLatestNews.setLayoutManager(new LinearLayoutManager(mContext));

        // 初始传入空列表，后续通过load方法更新
        newsAdapter = new NewsAdapter(new ArrayList<>());
        newsAdapter.setOnItemClickListener(this::openNewsDetail);
        rvLatestNews.setAdapter(newsAdapter);

        // 加载数据（仅一次）
        loadLatestNews();
    }

    private void loadLatestNews() {
        GetListUtil.getLatestNews(new DataCallback<NewsInfo>() {
            @Override
            public void onSuccess(List<NewsInfo> resultNews, int total) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    if (newsAdapter != null) {
                        newsAdapter.updateData(resultNews);
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                if (mContext != null) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(mContext, "获取最新动态列表失败", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }


    private void openMeetingDetail(Meeting meeting) {
        if (mContext == null) return;
        Intent intent = new Intent(mContext, MeetingDetailActivity.class);
        intent.putExtra("meeting", meeting);
        startActivity(intent);
    }

    private void openNewsDetail(NewsInfo news) {
        if (mContext == null) return;
        Intent intent = new Intent(mContext, NewsInfoDetailActivity.class);
        intent.putExtra("newsInfo", news);
        startActivity(intent);
    }
}
