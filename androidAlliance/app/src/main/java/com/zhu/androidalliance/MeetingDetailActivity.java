package com.zhu.androidalliance;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.zhu.androidalliance.adapter.AgendaAdapter;
import com.zhu.androidalliance.adapter.GuestAdapter;
import com.zhu.androidalliance.enums.MeetingType;
import com.zhu.androidalliance.pojo.dataObject.Meeting;
import com.zhu.androidalliance.utils.MeetingDataTracker;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MeetingDetailActivity extends AppCompatActivity {

    private ImageView ivMeetingImage;
    private TextView tvMeetingTitle;
    private TextView tvMeetingType;
    private TextView tvMeetingDate;
    private TextView tvMeetingLocation;
    private TextView tvMeetingOrganizer;
    private TextView tvMeetingSummary;
    private RecyclerView rvAgenda;
    private RecyclerView rvGuests;
    private ExtendedFloatingActionButton fabRegister;

    private Meeting meeting;
    private AgendaAdapter agendaAdapter;
    private GuestAdapter guestAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_detail);

        // 获取传递的会议对象
        meeting = (Meeting) getIntent().getSerializableExtra("meeting");
        if (meeting == null) {
            Toast.makeText(this, "会议信息加载失败", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        MeetingDataTracker.initialize(this);

        initViews();
        setupToolbar();
        bindData();
        setupAgendaList();
        setupGuestList();
    }

    private void initViews() {
        ivMeetingImage = findViewById(R.id.ivMeetingImage);
        tvMeetingTitle = findViewById(R.id.tvMeetingTitle);
        tvMeetingType = findViewById(R.id.tvMeetingType);
        tvMeetingDate = findViewById(R.id.tvMeetingDate);
        tvMeetingLocation = findViewById(R.id.tvMeetingLocation);
        tvMeetingOrganizer = findViewById(R.id.tvMeetingOrganizer);
        tvMeetingSummary = findViewById(R.id.tvMeetingSummary);
        rvAgenda = findViewById(R.id.rvAgenda);
        rvGuests = findViewById(R.id.rvGuests);
        fabRegister = findViewById(R.id.fabRegister);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void bindData() {
        // 加载会议图片
        if (meeting.getImageUrl() != null && !meeting.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(meeting.getImageUrl())
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_error)
                    .into(ivMeetingImage);
        }

        tvMeetingTitle.setText(meeting.getTitle());
        tvMeetingType.setText(meeting.getType().getDisplayName());

        // 设置会议类型标签背景色
        int bgColor = getTypeColor(meeting.getType());
        tvMeetingType.setBackgroundColor(bgColor);

        // 格式化日期时间
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日 HH:mm", Locale.getDefault());
        String dateStr = sdf.format(meeting.getStartTime()) + " - " +
                sdf.format(meeting.getEndTime());
        tvMeetingDate.setText(dateStr);

        tvMeetingLocation.setText(meeting.getLocation());
        tvMeetingOrganizer.setText(meeting.getOrganizer());
        tvMeetingSummary.setText(meeting.getSummary());

        // 注册按钮点击事件
        fabRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MeetingDetailActivity.this, AttendanceFormActivity.class);
            intent.putExtra("meetingId", meeting.getId());
            startActivity(intent);
        });
    }

    private int getTypeColor(MeetingType type) {
        switch (type) {
            case SEMINAR:
                return ContextCompat.getColor(this, R.color.color_seminar);
            case STANDARD:
                return ContextCompat.getColor(this, R.color.color_workshop);
            case TRAINING:
                return ContextCompat.getColor(this, R.color.color_conference);
            case TOOL_DEV:
                return ContextCompat.getColor(this, R.color.color_forum);
            case PUBLIC_WELFARE:
                return ContextCompat.getColor(this, R.color.bar);
            default:
                return ContextCompat.getColor(this, R.color.color_primary);
        }
    }

    private void setupAgendaList() {
        rvAgenda.setLayoutManager(new LinearLayoutManager(this));
        agendaAdapter = new AgendaAdapter(meeting.getAgendaItems());
        rvAgenda.setAdapter(agendaAdapter);
    }

    private void setupGuestList() {
        rvGuests.setLayoutManager(new LinearLayoutManager(this));
        guestAdapter = new GuestAdapter(meeting.getGuests());
        rvGuests.setAdapter(guestAdapter);
    }


}
