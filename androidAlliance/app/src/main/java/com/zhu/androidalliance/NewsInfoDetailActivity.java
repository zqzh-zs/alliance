package com.zhu.androidalliance;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.zhu.androidalliance.enums.BehaviorType;
import com.zhu.androidalliance.pojo.dataObject.NewsInfo;
import com.zhu.androidalliance.pojo.dataObject.NewsBehavior;
import com.zhu.androidalliance.utils.NewsBehaviorTracker;
import com.zhu.androidalliance.utils.NewsTypeConvert;

public class NewsInfoDetailActivity extends AppCompatActivity {

    private ImageView ivNewsImage;
    private TextView tvTitle, tvAuthor, tvTime, tvDescription;
    private WebView webViewContent;  // 替换TextView为WebView

    private NewsInfo newsInfo;
    private NewsBehavior newsBehavior; // 当前新闻的行为记录

    private ImageView ivBack, ivShare, ivLike;
    private boolean isLiked = false; // 点赞状态

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        // 初始化视图组件
        initViews();
        initListeners();

        // 获取传递的动态数据
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("newsInfo")) {
            newsInfo = (NewsInfo) intent.getSerializableExtra("newsInfo");
            if (newsInfo != null) {
                // 获取或创建与当前新闻关联的Behavior对象
                newsBehavior = NewsBehaviorTracker.getBehavior(newsInfo.getId());

                // 填充数据到视图
                populateData(newsInfo);
            }
        }
    }

    private void initListeners() {
        // 返回按钮点击事件
        ivBack.setOnClickListener(v -> {
            trackBehavior(BehaviorType.BACK);
            finish();
        });

        // 分享按钮点击事件
        ivShare.setOnClickListener(v -> {
            shareNews();
            trackBehavior(BehaviorType.SHARE);
        });

        // 点赞按钮点击事件
        ivLike.setOnClickListener(v -> {
            toggleLikeStatus();
            trackBehavior(BehaviorType.LIKE);
        });
    }

    private void toggleLikeStatus() {
        isLiked = !isLiked;
        updateLikeStatus();
    }

    private void updateLikeStatus() {
        ivLike.setImageResource(isLiked ?
                R.drawable.ic_favorite_red :
                R.drawable.ic_favorite_white);
    }

    private void shareNews() {
        if (newsInfo == null) return;

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, newsInfo.getTitle());
        shareIntent.putExtra(Intent.EXTRA_TEXT, newsInfo.getTitle() + "\n\n" + newsInfo.getSummary());
        startActivity(Intent.createChooser(shareIntent, "分享动态"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 记录浏览行为
        trackBehavior(BehaviorType.VIEW);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 页面暂停时保存行为数据
        if (newsBehavior != null) {
            NewsBehaviorTracker.track(newsBehavior);
        }
    }

    private void initViews() {
        ivNewsImage = findViewById(R.id.ivNewsImage);
        tvTitle = findViewById(R.id.tvTitle);
        tvAuthor = findViewById(R.id.tvAuthor);
        tvTime = findViewById(R.id.tvTime);
        tvDescription = findViewById(R.id.tvDescription);
        webViewContent = findViewById(R.id.webViewContent);  // 初始化WebView

        // 初始化新添加的导航栏视图
        ivBack = findViewById(R.id.ivBack);
        ivShare = findViewById(R.id.ivShare);
        ivLike = findViewById(R.id.ivLike);

        // 初始化WebView设置
        setupWebView();

        // 设置返回按钮
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("动态详情");
        }
    }

    private void setupWebView() {
        WebSettings webSettings = webViewContent.getSettings();
        webSettings.setJavaScriptEnabled(true);  // 启用JavaScript支持
        webSettings.setDomStorageEnabled(true);  // 启用DOM存储
        webSettings.setLoadWithOverviewMode(true);  // 缩放至屏幕大小
        webSettings.setUseWideViewPort(true);  // 使用宽视口
        webSettings.setBuiltInZoomControls(false);  // 禁用缩放控件
        webSettings.setDisplayZoomControls(false);

        // 设置自适应字体
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        webSettings.setTextZoom(100);  // 100% 文本大小

        // 设置WebView客户端
        webViewContent.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                // 显示加载状态（可选）
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // 页面加载完成（可选）
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                // 错误处理（可选）
            }
        });

        // 设置Chrome客户端处理进度等
        webViewContent.setWebChromeClient(new WebChromeClient());
    }

    private void populateData(NewsInfo newsInfo) {
        // 设置标题
        tvTitle.setText(newsInfo.getTitle());

        // 设置作者和时间
        tvAuthor.setText("发布人：" + newsInfo.getAuthor());
        tvTime.setText("发布时间：" + newsInfo.getCreateTime());

        // 设置简介
        tvDescription.setText(newsInfo.getSummary());

        // 设置富文本内容到WebView
        if (!TextUtils.isEmpty(newsInfo.getContent())) {
            // 添加CSS样式确保移动端适配
            String htmlContent = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "<style>\n" +
                    "body { margin:0; padding:0; font-family: sans-serif; }\n" +
                    "img { max-width:100% !important; height:auto !important; display:block; }\n" +
                    "iframe { max-width:100% !important; }\n" +
                    "table { width:100% !important; }\n" +
                    "* { max-width:100% !important; }\n" +
                    "</style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    newsInfo.getContent() +
                    "</body>\n" +
                    "</html>";

            // 加载HTML内容
            webViewContent.loadDataWithBaseURL(
                    null,
                    htmlContent,
                    "text/html",
                    "UTF-8",
                    null
            );
        }

        // 设置图片（使用Glide库加载图片）
        if (!TextUtils.isEmpty(newsInfo.getNewsImage())) {
            Glide.with(this)
                    .load(newsInfo.getNewsImage())
                    .placeholder(R.drawable.ic_placeholder) // 占位图
                    .error(R.drawable.ic_error) // 错误图
                    .into(ivNewsImage);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        trackBehavior(BehaviorType.BACK);
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        if (webViewContent != null) {
            webViewContent.destroy();
        }
        // 页面销毁前确保行为数据被发送
        if (newsBehavior != null) {
            NewsBehaviorTracker.track(newsBehavior);
        }
        super.onDestroy();
    }

    private void trackBehavior(BehaviorType type) {
        if (newsBehavior == null || newsInfo == null) return;
        // 更新行为计数
        NewsTypeConvert.convert(type, newsBehavior);
    }
}