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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.Data;


public class NewsListFragment extends Fragment {
    private final ExecutorService searchExecutor = Executors.newSingleThreadExecutor();
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
    String currentKeyword = "";

    // 数据存储
    List<NewsInfo> allNewsList = new ArrayList<>(); // 存储所有已加载的数据
    List<NewsInfo> filteredList = new ArrayList<>(); // 存储过滤后的数据

    private final Handler handler = new Handler(Looper.getMainLooper());
    private static final int SEARCH_DELAY = 500;

    private final Runnable searchRunnable = new Runnable() {
        @Override
        public void run() {
            applyLocalSearch();
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
                applyLocalSearch(); // 改为本地搜索
            });

            // 设置清除图标点击监听
            clearIcon.setOnClickListener(v -> {
                searchEditText.setText("");
                currentKeyword = "";
                applyLocalSearch(); // 清除搜索条件
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

        newsAdapter = new NewsAdapter(filteredList); // 使用过滤后的列表
        rvNews.setAdapter(newsAdapter);
        newsAdapter.setOnItemClickListener(this::openNewsDetail);
        rvNews.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager == null || isLoading || !hasMore) return;

                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                int totalItemCount = layoutManager.getItemCount();

                // 提前3项加载更多
                if (lastVisibleItem >= totalItemCount - 3) {
                    loadNextPage();
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

                    // 移除之前的搜索请求
                    handler.removeCallbacks(searchRunnable);

                    // 延迟触发本地搜索
                    handler.postDelayed(searchRunnable, SEARCH_DELAY);
                }
            });

            // 设置输入法搜索按钮监听
            searchEditText.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideKeyboard();
                    applyLocalSearch(); // 改为本地搜索
                    return true;
                }
                return false;
            });
        }
    }

    // 新增：应用本地搜索
    public void applyLocalSearch() {
        if (TextUtils.isEmpty(currentKeyword)) {
            // 没有关键词时显示所有数据
            filteredList.clear();
            filteredList.addAll(allNewsList);
        } else {
            // 执行模糊搜索
            filteredList.clear();
            String lowerCaseKeyword = currentKeyword.toLowerCase();

            for (NewsInfo news : allNewsList) {
                boolean matchTitle = news.getTitle() != null &&
                        news.getTitle().toLowerCase().contains(lowerCaseKeyword);

                boolean matchSummary = news.getSummary() != null &&
                        news.getSummary().toLowerCase().contains(lowerCaseKeyword);

                if (matchTitle || matchSummary) {
                    filteredList.add(news);
                }
            }
        }

        // 更新UI
        updateUIAfterSearch();
    }

    // 新增：搜索后更新UI
    public void updateUIAfterSearch() {
        newsAdapter.notifyDataSetChanged();

        // 处理空状态
        boolean isEmpty = filteredList.isEmpty();
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
            tvSearchHint.setText("搜索: " + currentKeyword + " (共 " + filteredList.size() + " 条结果)");
            tvSearchHint.setVisibility(View.VISIBLE);
        } else {
            tvSearchHint.setVisibility(View.GONE);
        }
    }

    private void setupRefresh() {
        swipeRefreshLayout.setOnRefreshListener(this::loadFirstPage);
    }

    private void loadFirstPage() {
        currentPage = 1;
        hasMore = true;
        allNewsList.clear(); // 清空所有数据
        filteredList.clear(); // 清空过滤数据
        newsAdapter.notifyDataSetChanged(); // 通知适配器更新
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
        GetListUtil.getNewsList(currentPage, pageSize, new DataCallback<NewsInfo>() {
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

    public void handleNewsList(List<NewsInfo> newsList, int totalCount) {
        // 将新数据添加到完整列表
        allNewsList.addAll(newsList);

        // 应用当前搜索条件
        applyLocalSearch();

        // 计算是否还有更多数据
        hasMore = (currentPage * pageSize) < totalCount;

        // 更新UI
        if (currentPage == 1) {
            swipeRefreshLayout.setRefreshing(false);
            if (!filteredList.isEmpty()) {
                rvNews.scrollToPosition(0); // 滚动到顶部
            }
        }

        showLoading(false);
        progressBottom.setVisibility(View.GONE);
        isLoading = false;
    }

    public void handleNewsError(String errorMsg) {
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

    public void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        rvNews.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        if (!isLoading) {
            tvEmptyState.setVisibility(filteredList.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }

    public void showEmptyState(boolean isEmpty) {
        tvEmptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    private void openNewsDetail(NewsInfo news) {
        Intent intent = new Intent(getActivity(), NewsInfoDetailActivity.class);
        intent.putExtra("newsInfo", news);
        startActivity(intent);
    }

    // 在Fragment销毁时关闭线程池
    @Override
    public void onDestroy() {
        super.onDestroy();
        searchExecutor.shutdownNow();
    }
}