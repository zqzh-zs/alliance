package com.zhu.androidalliance.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.zhu.androidalliance.NewsInfoDetailActivity;
import com.zhu.androidalliance.R;
import com.zhu.androidalliance.adapter.NewsAdapter;
import com.zhu.androidalliance.decoration.VerticalSpaceItemDecoration;
import com.zhu.androidalliance.pojo.dataObject.NewsInfo;
import com.zhu.androidalliance.utils.GetListUtil;
import com.zhu.androidalliance.callback.DataCallback;

import java.util.ArrayList;
import java.util.List;

public class NewsListFragment extends Fragment {

    // UI 组件

    private RecyclerView rvNews;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar, progressBottom;
    private TextView tvEmptyState, tvSearchHint;
    private NewsAdapter newsAdapter;
    private EditText searchEditText;

    // 分页参数
    private int currentPage = 1;
    private int pageSize = 10;
    private boolean hasMore = true;
    private boolean isLoading = false;
    private String currentKeyword = "";

    private final Handler handler = new Handler(Looper.getMainLooper());
    private static final int SEARCH_DELAY = 500;

    private final Runnable searchRunnable = new Runnable() {
        @Override
        public void run() {
            if (!TextUtils.isEmpty(currentKeyword)) {
                loadFirstPage();
            } else {
                // 当搜索框清空时，重新加载第一页
                loadFirstPage();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_list, container, false);

        // 初始化视图
        initViews(view);

        // 替换搜索栏
        replaceSearchBar(view);

        // 配置列表和事件
        setupNewsList();
        setupSearch();
        setupRefresh();

        // 首次加载数据
        loadFirstPage();

        return view;
    }

    private void replaceSearchBar(View view) {
        ViewGroup appBar = view.findViewById(R.id.appBar);
        View originalSearchBar = view.findViewById(R.id.searchBar);

        if (originalSearchBar != null) {
            int index = appBar.indexOfChild(originalSearchBar);
            appBar.removeView(originalSearchBar);

            // 添加自定义搜索布局
            View customSearch = LayoutInflater.from(requireContext())
                    .inflate(R.layout.custom_search_layout, appBar, false);
            appBar.addView(customSearch, index);

            // 初始化自定义搜索视图
            searchEditText = customSearch.findViewById(R.id.searchEditText);
            ImageView searchIcon = customSearch.findViewById(R.id.searchIcon);
            ImageView clearIcon = customSearch.findViewById(R.id.clearIcon);

            // 设置搜索图标点击监听
            searchIcon.setOnClickListener(v -> {
                hideKeyboard();
                loadFirstPage();
            });

            // 设置清除图标点击监听
            clearIcon.setOnClickListener(v -> {
                searchEditText.setText("");
                currentKeyword = "";
                loadFirstPage();
            });
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) requireContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
    }

    private void initViews(View view) {
        rvNews = view.findViewById(R.id.rvNews);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        progressBar = view.findViewById(R.id.progressBar);
        progressBottom = view.findViewById(R.id.progressBottom);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);
        tvSearchHint = view.findViewById(R.id.tvSearchHint);
    }

    private void setupNewsList() {
        rvNews.setLayoutManager(new LinearLayoutManager(requireContext()));
        int spacing = getResources().getDimensionPixelSize(R.dimen.list_item_spacing);
        rvNews.addItemDecoration(new VerticalSpaceItemDecoration(spacing));

        newsAdapter = new NewsAdapter(new ArrayList<>());
        rvNews.setAdapter(newsAdapter);
        newsAdapter.setOnItemClickListener(this::openNewsDetail);

        // 滚动监听实现上拉加载
        rvNews.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager == null) return;

                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && hasMore) {
                    // 滚动到底部时加载更多
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= pageSize) {
                        loadNextPage();
                    }
                }
            }
        });
    }

    private void setupSearch() {
        if (searchEditText != null) {
            // 设置文本变化监听器
            searchEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    currentKeyword = s.toString().trim();
                    handler.removeCallbacks(searchRunnable);
                    handler.postDelayed(searchRunnable, SEARCH_DELAY);
                }
            });

            // 设置输入法搜索按钮监听
            searchEditText.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideKeyboard();
                    loadFirstPage();
                    return true;
                }
                return false;
            });
        }
    }

    private void setupRefresh() {
        swipeRefreshLayout.setOnRefreshListener(this::loadFirstPage);
    }

    private void loadFirstPage() {
        currentPage = 1;
        hasMore = true;
        newsAdapter.clearData();
        loadNews();
    }

    private void loadNextPage() {
        if (!isLoading && hasMore) {
            currentPage++;
            loadNews();
        }
    }

    private void loadNews() {
        if (getActivity() == null || isDetached()) return;

        if (currentPage == 1) {
            showLoading(true);
        } else {
            progressBottom.setVisibility(View.VISIBLE);
        }

        isLoading = true;

        // 使用GetListUtil获取新闻数据
        GetListUtil.getNewsList(currentPage, pageSize, currentKeyword, new DataCallback<NewsInfo>() {
            @Override
            public void onSuccess(List<NewsInfo> list, int totalCount) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        handleNewsList(list, totalCount);
                    });
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        handleNewsError(errorMsg);
                    });
                }
            }
        });
    }

    private void handleNewsList(List<NewsInfo> newsList, int totalCount) {
        // 计算是否还有更多数据
        hasMore = (currentPage * pageSize) < totalCount;

        // 更新UI
        if (currentPage == 1) {
            newsAdapter.updateData(newsList);
            swipeRefreshLayout.setRefreshing(false);
            if (!newsList.isEmpty()) {
                rvNews.scrollToPosition(0); // 滚动到顶部
            }
        } else {
            newsAdapter.appendData(newsList);
        }

        showLoading(false);
        progressBottom.setVisibility(View.GONE);

        // 处理空状态
        boolean isEmpty = newsList.isEmpty();
        showEmptyState(isEmpty);
        if (isEmpty) {
            if (!TextUtils.isEmpty(currentKeyword)) {
                tvEmptyState.setText("没有找到相关动态");
            } else {
                tvEmptyState.setText("暂无动态数据");
            }
        }

        // 显示搜索提示
        if (!TextUtils.isEmpty(currentKeyword)) {
            tvSearchHint.setText("搜索: " + currentKeyword + " (共 " + totalCount + " 条结果)");
            tvSearchHint.setVisibility(View.VISIBLE);
        } else {
            tvSearchHint.setVisibility(View.GONE);
        }

        isLoading = false;
    }

    private void handleNewsError(String errorMsg) {
        showLoading(false);
        progressBottom.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);

        Toast.makeText(requireContext(), "加载失败: " + errorMsg, Toast.LENGTH_SHORT).show();

        // 显示错误状态
        if (currentPage == 1 && newsAdapter.getItemCount() == 0) {
            tvEmptyState.setText("加载失败，请重试");
            tvEmptyState.setVisibility(View.VISIBLE);
        }

        isLoading = false;
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        rvNews.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        if (!isLoading) {
            tvEmptyState.setVisibility(newsAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        }
    }

    private void showEmptyState(boolean isEmpty) {
        tvEmptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    private void openNewsDetail(NewsInfo news) {
        Intent intent = new Intent(getActivity(), NewsInfoDetailActivity.class);
        intent.putExtra("newsInfo", news);
        startActivity(intent);
    }
}