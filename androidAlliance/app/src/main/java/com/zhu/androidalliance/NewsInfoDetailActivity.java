package com.zhu.androidalliance;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.zhu.androidalliance.enums.BehaviorType;
import com.zhu.androidalliance.pojo.dataObject.NewsInfo;
import com.zhu.androidalliance.pojo.dataObject.NewsTracker;
import com.zhu.androidalliance.utils.DateFormatUtil;
import com.zhu.androidalliance.utils.NewsDataTracker;
import com.zhu.androidalliance.utils.NewsTypeConvert;

public class NewsInfoDetailActivity extends AppCompatActivity {

    private ImageView ivNewsImage;
    private TextView tvTitle, tvAuthor, tvTime, tvDescription;
    private WebView webViewContent;
    private ProgressBar pbLoading; // 新增加载进度条

    private NewsInfo newsInfo;
    private NewsTracker newsTracker;
    private ImageView ivBack, ivShare, ivLike;
    private boolean isLiked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        initViews();
        initListeners();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("newsInfo")) {
            newsInfo = (NewsInfo) intent.getSerializableExtra("newsInfo");
            if (newsInfo != null) {
                newsTracker = NewsDataTracker.getBehavior(newsInfo.getId());
                populateData(newsInfo);
            }
        }
    }

    private void initListeners() {
        ivBack.setOnClickListener(v -> {
            trackBehavior(BehaviorType.BACK);
            finish();
        });

        ivShare.setOnClickListener(v -> {
            shareNews();
            trackBehavior(BehaviorType.SHARE);
        });

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
        trackBehavior(BehaviorType.VIEW);
        if (webViewContent != null) {
            webViewContent.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webViewContent != null) {
            webViewContent.onPause();
            webViewContent.pauseTimers();
        }
        if (newsTracker != null) {
            NewsDataTracker.track(newsTracker);
        }
    }

    private void initViews() {
        ivNewsImage = findViewById(R.id.ivNewsImage);
        tvTitle = findViewById(R.id.tvTitle);
        tvAuthor = findViewById(R.id.tvAuthor);
        tvTime = findViewById(R.id.tvTime);
        tvDescription = findViewById(R.id.tvDescription);
        webViewContent = findViewById(R.id.webViewContent);
        pbLoading = findViewById(R.id.pbLoading); // 初始化进度条

        ivBack = findViewById(R.id.ivBack);
        ivShare = findViewById(R.id.ivShare);
        ivLike = findViewById(R.id.ivLike);

        setupWebView();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("动态详情");
        }
    }

    private void setupWebView() {
        WebSettings webSettings = webViewContent.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        webSettings.setTextZoom(100);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW); // 允许混合内容

        // 增强的WebViewClient实现
        webViewContent.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                showLoading(); // 显示加载状态
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                hideLoading(); // 隐藏加载状态
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                handleError(); // 处理加载错误
            }
        });

        // 增强的WebChromeClient实现，处理进度和渲染崩溃
        webViewContent.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                pbLoading.setProgress(newProgress); // 更新进度条
                if (newProgress == 100) {
                    hideLoading();
                }
            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                // 打印WebView控制台日志（用于调试）
                return super.onConsoleMessage(consoleMessage);
            }


        });
    }

    private void populateData(NewsInfo newsInfo) {
        tvTitle.setText(newsInfo.getTitle());
        tvAuthor.setText("发布人：" + newsInfo.getAuthor());
        tvTime.setText("发布时间：" + DateFormatUtil.format(newsInfo.getCreateTime()));
        tvDescription.setText(newsInfo.getSummary());

        if (!TextUtils.isEmpty(newsInfo.getContent())) {
            // 增强的HTML样式，确保更好的移动端适配
            String htmlContent = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\">\n" +
                    "<style>\n" +
                    "body { margin:0; padding:0; font-size:16px; line-height:1.6; color:#333; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif; }\n" +
                    "p { margin-bottom:16px; }\n" +
                    "h1, h2, h3, h4, h5, h6 { color:#222; margin-top:24px; margin-bottom:12px; }\n" +
                    "img { max-width:100% !important; height:auto !important; display:block; margin:16px 0; border-radius:8px; }\n" +
                    "a { color:#007AFF; text-decoration:none; }\n" +
                    "blockquote { border-left:4px solid #eee; padding:0 16px; color:#666; }\n" +
                    "ul, ol { margin-left:20px; margin-bottom:16px; }\n" +
                    "li { margin-bottom:8px; }\n" +
                    "table { width:100% !important; border-collapse:collapse; margin:16px 0; }\n" +
                    "th, td { border:1px solid #eee; padding:8px; text-align:left; }\n" +
                    "th { background-color:#f5f5f5; }\n" +
                    "pre { background-color:#f8f8f8; padding:16px; overflow-x:auto; border-radius:4px; }\n" +
                    "</style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    newsInfo.getContent() +
                    "</body>\n" +
                    "</html>";

            webViewContent.loadDataWithBaseURL(
                    null,
                    htmlContent,
                    "text/html",
                    "UTF-8",
                    null
            );
        }

        if (!TextUtils.isEmpty(newsInfo.getNewsImage())) {
            Glide.with(this)
                    .load(newsInfo.getNewsImage())
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_error)
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
            // 先从父容器中移除WebView
            ViewGroup parent = (ViewGroup) webViewContent.getParent();
            if (parent != null) {
                parent.removeView(webViewContent);
            }
            // 停止所有可能的操作
            webViewContent.stopLoading();
            webViewContent.getSettings().setJavaScriptEnabled(false);
            webViewContent.clearHistory();
            webViewContent.clearFormData();
            webViewContent.clearCache(true);
            webViewContent.clearView();
            webViewContent.removeAllViews();
            // 销毁WebView
            webViewContent.destroy();
            webViewContent = null;
        }
        super.onDestroy();
    }

    private void trackBehavior(BehaviorType type) {
        if (newsTracker == null || newsInfo == null) return;
        NewsTypeConvert.convert(type, newsTracker);
    }

    // ================= 新增辅助方法 =================

    private void showLoading() {
        if (pbLoading != null) {
            pbLoading.setVisibility(View.VISIBLE);
        }
        // 可以在此处显示加载动画
    }

    private void hideLoading() {
        if (pbLoading != null) {
            pbLoading.setVisibility(View.GONE);
        }
    }

    private void handleError() {
        hideLoading();
        // 显示错误信息
        webViewContent.loadData("<html><body style='text-align:center; padding:20px;'>" +
                "<img src='android.resource://" + getPackageName() + "/" + R.drawable.ic_error + "' style='display:block; margin:0 auto 16px;' />" +
                "<h3>加载失败</h3>" +
                "<p>抱歉，无法加载内容，请稍后重试</p>" +
                "</body></html>", "text/html", "UTF-8");
    }

    private void showRenderCrashMessage() {
        hideLoading();
        // 显示渲染进程崩溃提示
        webViewContent.loadData("<html><body style='text-align:center; padding:20px;'>" +
                "<img src='android.resource://" + getPackageName() + "/" + R.drawable.ic_error + "' style='display:block; margin:0 auto 16px;' />" +
                "<h3>页面加载异常</h3>" +
                "<p>渲染进程意外崩溃，正在尝试重新加载</p>" +
                "</body></html>", "text/html", "UTF-8");
    }
}