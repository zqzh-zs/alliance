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
    private Context mContext; // 保存上下文引用

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context; // 在 Fragment 附加时获取上下文
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null; // 分离时清空引用，避免内存泄漏
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // 初始化视图
        rvFeaturedMeetings = view.findViewById(R.id.rvFeaturedMeetings);
        rvLatestNews = view.findViewById(R.id.rvLatestNews);

        // 设置推荐会议列表
        setupFeaturedMeetings();

        // 设置最新动态列表
        setupLatestNews();

        return view;
    }

    private void setupFeaturedMeetings() {
        // 配置横向布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );
        rvFeaturedMeetings.setLayoutManager(layoutManager);

        // 添加间距
        int spacing = getResources().getDimensionPixelSize(R.dimen.conference_spacing);
        rvFeaturedMeetings.addItemDecoration(new HorizontalSpaceItemDecoration(spacing));

        // 创建适配器
        featuredMeetingAdapter = new MeetingAdapter(getFeaturedMeetings(), meeting -> {
            // 会议点击事件
            openMeetingDetail(meeting);
        });

        rvFeaturedMeetings.setAdapter(featuredMeetingAdapter);
    }

    private void setupLatestNews() {
        // 配置垂直布局管理器
        rvLatestNews.setLayoutManager(new LinearLayoutManager(requireContext()));

        // 创建适配器
        newsAdapter = new NewsAdapter(getLatestNews());
        newsAdapter.setOnItemClickListener(new NewsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(NewsInfo newsInfo) {
                openNewsDetail(newsInfo);
            }
        });

        rvLatestNews.setAdapter(newsAdapter);
    }

    private List<Meeting> getFeaturedMeetings() {
        List<Meeting> meetings = new ArrayList<>();
        GetListUtil.getAllMeetings(new DataCallback<Meeting>() {
            @Override
            public void onSuccess(List<Meeting> resultMeetings, int total) {
                meetings.addAll(resultMeetings);
                // 在 UI 线程更新适配器
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (featuredMeetingAdapter != null) {
                            featuredMeetingAdapter.updateData(meetings);
                        }
                    });
                }
            }

            @Override
            public void onFailure(String error) {
                // 使用保存的 mContext，并检查是否为 null
                if (mContext != null) {
                    Toast.makeText(mContext, "获取会议列表失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return meetings;
    }

    private List<NewsInfo> getLatestNews() {
        List<NewsInfo> newsList = new ArrayList<>();
        GetListUtil.getLatestNews(new DataCallback<NewsInfo>() {
            @Override
            public void onSuccess(List<NewsInfo> resultNews, int total) {
                newsList.addAll(resultNews);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (newsAdapter != null) {
                            newsAdapter.updateData(newsList);
                        }
                    });
                }
            }

            @Override
            public void onFailure(String error) {
                if (mContext != null) {
                    Toast.makeText(mContext, "获取最新动态列表失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return newsList;
    }
    private void openMeetingDetail(Meeting meeting) {
        Intent intent = new Intent(getActivity(), MeetingDetailActivity.class);
        intent.putExtra("meeting", meeting);
        startActivity(intent);
    }

    private void openNewsDetail(NewsInfo news) {
        Intent intent = new Intent(getActivity(), NewsInfoDetailActivity.class);
        intent.putExtra("newsInfo", news);
        startActivity(intent);
    }
}
