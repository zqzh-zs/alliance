package com.zhu.androidalliance.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.zhu.androidalliance.MeetingDetailActivity;
import com.zhu.androidalliance.R;
import com.zhu.androidalliance.adapter.MeetingAdapter;

import com.zhu.androidalliance.callback.DataCallback;
import com.zhu.androidalliance.decoration.VerticalSpaceItemDecoration;
import com.zhu.androidalliance.enums.MeetingType;
import com.zhu.androidalliance.pojo.dataObject.Meeting;


import com.zhu.androidalliance.utils.GetListUtil;


import java.util.ArrayList;
import java.util.List;

public class MeetingListFragment extends Fragment {


    TabLayout tabLayout;
    private RecyclerView rvMeetings;
    private ProgressBar progressBar;
    private ProgressBar pbLoadMore;
    private TextView tvEmptyState;
    private MeetingAdapter meetingAdapter;

    private List<Meeting> allMeetings = new ArrayList<>();
    MeetingType currentType = MeetingType.SEMINAR; // 默认会议类型
    int currentPage = 1;
    private int pageSize = 10;
    boolean hasMore = true;
    boolean isLoading = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meeting_list, container, false);

        // 初始化视图
        tabLayout = view.findViewById(R.id.tabLayout);
        rvMeetings = view.findViewById(R.id.rvMeetings);
        progressBar = view.findViewById(R.id.progressBar);
        pbLoadMore = view.findViewById(R.id.pbLoadMore);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);

        // 配置会议列表
        setupMeetingList();

        // 配置分类标签
        setupTabs();

        // 初始加载会议数据
        loadMeetings(false);

        return view;
    }

    private void setupMeetingList() {
        // 配置布局管理器
        rvMeetings.setLayoutManager(new LinearLayoutManager(requireContext()));

        // 添加间距
        int spacing = getResources().getDimensionPixelSize(R.dimen.list_item_spacing);
        rvMeetings.addItemDecoration(new VerticalSpaceItemDecoration(spacing));

        // 创建适配器
        meetingAdapter = new MeetingAdapter(new ArrayList<>(), meeting -> {
            // 会议点击事件
            openMeetingDetail(meeting);
        });

        rvMeetings.setAdapter(meetingAdapter);

        // 添加滚动监听实现上拉加载
        rvMeetings.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager == null) return;

                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && hasMore && dy > 0) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= pageSize) {
                        loadNextPage();
                    }
                }
            }
        });
    }

    private void setupTabs() {
        // 添加会议分类标签
        for (MeetingType type : MeetingType.values()) {
            tabLayout.addTab(tabLayout.newTab().setText(type.getDisplayName()));
        }

        // 标签选择监听
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // 切换分类时重新加载数据
                MeetingType selectedType = MeetingType.values()[tab.getPosition()];
                currentType = selectedType;
                currentPage = 1;
                hasMore = true;
                loadMeetings(true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    void loadMeetings(boolean clearData) {
        if(isLoading) return;
        if (clearData) {
            meetingAdapter.clearData();
        }

        // 显示加载状态
        showLoading(true, false);
        isLoading = true;

        // 获取会议数据
        GetListUtil.getMeetingsByType(currentPage, pageSize, currentType, new DataCallback<Meeting>() {
            @Override
            public void onSuccess(List<Meeting> meetings, int total) {
                allMeetings.addAll(meetings);
                hasMore = (currentPage * pageSize) < total;

                requireActivity().runOnUiThread(() -> {
                    meetingAdapter.appendData(meetings);
                    showLoading(false, false);

                    // 显示空状态
                    boolean isEmpty = meetingAdapter.getItemCount() == 0;
                    tvEmptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);

                    isLoading = false;
                });
            }

            @Override
            public void onFailure(String error) {
                requireActivity().runOnUiThread(() -> {
                    showLoading(false, false);
                    Toast.makeText(requireContext(), "加载失败: " + error, Toast.LENGTH_SHORT).show();

                    // 显示错误状态
                    boolean isEmpty = meetingAdapter.getItemCount() == 0;
                    tvEmptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
                    if (isEmpty) {
                        tvEmptyState.setText("加载失败，点击重试");
                        tvEmptyState.setOnClickListener(v -> loadMeetings(true));
                    }

                    isLoading = false;
                });
            }
        });
    }

    void loadNextPage() {
        if(isLoading || !hasMore) return;
        if (!isLoading && hasMore) {
            currentPage++;
            pbLoadMore.setVisibility(View.VISIBLE);
            loadMeetings(false);
        }
    }


    void showLoading(boolean isLoading, boolean isLoadMore) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            rvMeetings.setVisibility(View.GONE);
            tvEmptyState.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            rvMeetings.setVisibility(View.VISIBLE);
            pbLoadMore.setVisibility(isLoadMore ? View.VISIBLE : View.GONE);
        }
    }

    void openMeetingDetail(Meeting meeting) {
        Intent intent = new Intent(getActivity(), MeetingDetailActivity.class);
        intent.putExtra("meeting", meeting);
        startActivity(intent);
    }
}