package com.zhu.androidalliance;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.zhu.androidalliance.fragment.HomeFragment;
import com.zhu.androidalliance.fragment.MeetingListFragment;
import com.zhu.androidalliance.fragment.NewsListFragment;
import com.zhu.androidalliance.utils.NewsDataTracker;


import java.util.ArrayList;
import java.util.List;



public class MainActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigation;
    private MaterialToolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NewsDataTracker.initialize(this);

        // 初始化视图
        viewPager = findViewById(R.id.viewPager);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        toolbar = findViewById(R.id.toolbar);


        // 设置工具栏
        setSupportActionBar(toolbar);

        // 配置ViewPager
        setupViewPager();

        // 底部导航与ViewPager联动
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                viewPager.setCurrentItem(0);
            } else if (itemId == R.id.nav_conference) {
                viewPager.setCurrentItem(1);
            } else if (itemId == R.id.nav_news) {
                viewPager.setCurrentItem(2);
            }
            return true;
        });

        // 监听ViewPager页面变化
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        bottomNavigation.setSelectedItemId(R.id.nav_home);
                        break;
                    case 1:
                        bottomNavigation.setSelectedItemId(R.id.nav_conference);
                        break;
                    case 2:
                        bottomNavigation.setSelectedItemId(R.id.nav_news);
                        break;
                }

            }
        });



    }

    private void setupViewPager() {
        // 创建Fragment适配器
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new HomeFragment());         // 首页
        fragments.add(new MeetingListFragment());// 会议列表
        fragments.add(new NewsListFragment());     // 动态列表

        FragmentAdapter adapter = new FragmentAdapter(
                getSupportFragmentManager(),
                getLifecycle(),
                fragments
        );

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3); // 缓存所有页面
        viewPager.setUserInputEnabled(false); // 禁用滑动切换（使用底部导航）
    }


    // 首页快速入口点击事件
    public void navigateToMeetings(View view) {
        viewPager.setCurrentItem(1);
    }

    public void navigateToNews(View view) {
        viewPager.setCurrentItem(2);
    }

    public void navigateToPublicWelfare(View view) {
        // 打开公益行动页面
        startActivity(new Intent(this, PublicWelfareActivity.class));
    }


    // Fragment适配器
    private static class FragmentAdapter extends FragmentStateAdapter {
        private final List<Fragment> fragments;

        public FragmentAdapter(FragmentManager fm, Lifecycle lifecycle, List<Fragment> fragments) {
            super(fm, lifecycle);
            this.fragments = fragments;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragments.get(position);
        }

        @Override
        public int getItemCount() {
            return fragments.size();
        }
    }
}