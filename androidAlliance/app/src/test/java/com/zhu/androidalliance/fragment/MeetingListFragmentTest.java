package com.zhu.androidalliance.fragment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zhu.androidalliance.R;
import com.zhu.androidalliance.adapter.MeetingAdapter;
import com.zhu.androidalliance.callback.DataCallback;
import com.zhu.androidalliance.enums.MeetingType;
import com.zhu.androidalliance.utils.GetListUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import androidx.fragment.app.testing.FragmentScenario;
@RunWith(MockitoJUnitRunner.class)
public class MeetingListFragmentTest {

    @Mock private GetListUtil mockGetListUtil;
    @Mock private MeetingAdapter mockAdapter;

    private FragmentScenario<MeetingListFragment> scenario;

    @Before
    public void setup() {
        // 初始化Fragment场景
        scenario = FragmentScenario.launchInContainer(MeetingListFragment.class);
    }

    @Test
    public void testInitialDataLoading() {
        scenario.onFragment(fragment -> {
            // 验证初始加载状态
            ProgressBar progressBar = fragment.getView().findViewById(R.id.progressBar);
            assertTrue(progressBar.getVisibility() == View.VISIBLE);
            assertFalse(fragment.isLoading);

            // 模拟数据加载成功
            fragment.loadMeetings(false);
            verify(mockGetListUtil).getMeetingsByType(
                    eq(1), eq(10), eq(MeetingType.SEMINAR), any(DataCallback.class)
            );
        });
    }

    @Test
    public void testTabSwitch() {
        scenario.onFragment(fragment -> {
            // 模拟标签切换
            fragment.tabLayout.selectTab(fragment.tabLayout.getTabAt(1)); // 标准定制

            // 验证类型切换和重置
            assertEquals(MeetingType.STANDARD, fragment.currentType);
            assertEquals(1, fragment.currentPage);
            assertTrue(fragment.hasMore);

            // 验证加载新类型数据
            verify(mockGetListUtil).getMeetingsByType(
                    eq(1), eq(10), eq(MeetingType.STANDARD), any(DataCallback.class)
            );
        });
    }

    @Test
    public void testLoadMore() {
        scenario.onFragment(fragment -> {
            // 模拟滚动到底部
            fragment.currentPage = 1;
            fragment.hasMore = true;
            fragment.isLoading = false;

            // 触发滚动加载
            fragment.loadNextPage();

            // 验证加载下一页
            assertEquals(2, fragment.currentPage);
            verify(mockGetListUtil).getMeetingsByType(
                    eq(2), eq(10), eq(MeetingType.SEMINAR), any(DataCallback.class)
            );
        });
    }

    @Test
    public void testErrorHandling() {
        scenario.onFragment(fragment -> {
            // 模拟加载失败
            fragment.loadMeetings(true);

            // 验证错误状态
            TextView tvEmptyState = fragment.getView().findViewById(R.id.tvEmptyState);
            assertTrue(tvEmptyState.getVisibility() == View.VISIBLE);
            assertEquals("加载失败，点击重试", tvEmptyState.getText());
        });
    }
}