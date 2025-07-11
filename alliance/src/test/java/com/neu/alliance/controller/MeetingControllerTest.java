package com.neu.alliance.controller;

import com.neu.alliance.entity.Meeting;
import com.neu.alliance.entity.MeetingStatus;
import com.neu.alliance.entity.Response;
import com.neu.alliance.mapper.AgendaMapper;
import com.neu.alliance.mapper.GuestMapper;
import com.neu.alliance.mapper.MeetingMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MeetingControllerTest {

    @InjectMocks
    private MeetingController meetingController;

    @Mock
    private MeetingMapper meetingMapper;

    @Mock
    private GuestMapper guestMapper;

    @Mock
    private AgendaMapper agendaMapper;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // -------- createMeeting() 测试 --------
    @Test
    void createMeeting_role1_approvedStatus() {
        Meeting meeting = new Meeting();
        when(meetingMapper.insertMeeting(any())).thenReturn(1);

        Response<String> response = meetingController.createMeeting(meeting, "1");
        assertEquals(200, response.getCode());
        assertEquals("会议创建成功", response.getData());
        assertEquals(MeetingStatus.APPROVED, meeting.getStatus());
    }

    @Test
    void createMeeting_roleOther_pendingStatus() {
        Meeting meeting = new Meeting();
        when(meetingMapper.insertMeeting(any())).thenReturn(1);

        Response<String> response = meetingController.createMeeting(meeting, "2");
        assertEquals(200, response.getCode());
        assertEquals(MeetingStatus.PENDING, meeting.getStatus());
    }

    @Test
    void createMeeting_insertFail() {
        Meeting meeting = new Meeting();
        when(meetingMapper.insertMeeting(any())).thenReturn(0);

        Response<String> response = meetingController.createMeeting(meeting, "1");
        assertEquals(500, response.getCode());
        assertEquals("会议创建失败", response.getMsg());
    }

    @Test
    void createMeeting_nullMeeting() {
        Response<String> response = meetingController.createMeeting(null, "1");
        assertEquals(500, response.getCode());
        assertEquals("参数不能为空", response.getMsg());
    }

    // -------- getMeetingDetail() 测试 --------
    @Test
    void getMeetingDetail_found_withUploadsPrefix() {
        Meeting meeting = new Meeting();
        meeting.setImageUrl("/uploads/image.png");
        when(meetingMapper.selectMeetingDetail(1)).thenReturn(meeting);

        ResponseEntity<?> response = meetingController.getMeetingDetail(1);
        assertEquals(200, ((MeetingController.Result) response.getBody()).getCode());
        Meeting bodyMeeting = (Meeting) ((MeetingController.Result) response.getBody()).getData();
        assertEquals("http://localhost:8080/uploads/image.png", bodyMeeting.getImageUrl());
    }

    @Test
    void getMeetingDetail_found_withoutUploadsPrefix() {
        Meeting meeting = new Meeting();
        meeting.setImageUrl("http://example.com/image.png");
        when(meetingMapper.selectMeetingDetail(1)).thenReturn(meeting);

        ResponseEntity<?> response = meetingController.getMeetingDetail(1);
        assertEquals(200, ((MeetingController.Result) response.getBody()).getCode());
        Meeting bodyMeeting = (Meeting) ((MeetingController.Result) response.getBody()).getData();
        assertEquals("http://example.com/image.png", bodyMeeting.getImageUrl());
    }

    @Test
    void getMeetingDetail_found_imageUrlNull() {
        Meeting meeting = new Meeting();
        meeting.setImageUrl(null);
        when(meetingMapper.selectMeetingDetail(1)).thenReturn(meeting);

        ResponseEntity<?> response = meetingController.getMeetingDetail(1);
        assertEquals(200, ((MeetingController.Result) response.getBody()).getCode());
        Meeting bodyMeeting = (Meeting) ((MeetingController.Result) response.getBody()).getData();
        assertNull(bodyMeeting.getImageUrl());
    }

    @Test
    void getMeetingDetail_notFound() {
        when(meetingMapper.selectMeetingDetail(999)).thenReturn(null);

        ResponseEntity<?> response = meetingController.getMeetingDetail(999);
        assertEquals(404, response.getStatusCodeValue());
    }

    // -------- getApprovedMeetings() 测试 --------
    @Test
    void getApprovedMeetings_success() {
        when(meetingMapper.getApprovedMeetings(any(), any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(new Meeting()));
        when(meetingMapper.countApprovedMeetings(any(), any(), any(), any(), any())).thenReturn(1);

        Map<String, Object> res = meetingController.getApprovedMeetings(1, 10, null, null, null, null, null);
        assertEquals(200, res.get("code"));
        Map<String, Object> data = (Map<String, Object>) res.get("data");
        assertEquals(1, data.get("total"));
        List<?> list = (List<?>) data.get("list");
        assertEquals(1, list.size());
    }

    // -------- search() 测试 --------
    @Test
    void search_success() {
        Integer meetingId = 1;

        // 构造返回的 meeting 列表
        Meeting meeting = new Meeting();
        meeting.setId(meetingId);

        when(meetingMapper.searchMeetings(any(), any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(meeting));
        when(meetingMapper.countMeetings(any(), any(), any(), any(), any()))
                .thenReturn(1);
        when(guestMapper.getGuestsByMeetingId(meetingId))
                .thenReturn(new ArrayList<>());
        when(agendaMapper.getAgendaItemsByMeetingId(meetingId))
                .thenReturn(new ArrayList<>());

        Map<String, Object> res = meetingController.search(1, 10, null, null, null, null, null);

        // ✅ 正确断言逻辑
        assertNotNull(res);
        assertEquals(1, res.get("total"));

        List<?> list = (List<?>) res.get("list");
        assertNotNull(list);
        assertEquals(1, list.size());
    }

    // -------- delete() 测试 --------
    @Test
    void delete_success() {
        doNothing().when(meetingMapper).deleteById(1);
        Map<String, Object> res = meetingController.delete(1);
        assertEquals(200, res.get("code"));
        assertEquals("删除成功", res.get("msg"));
    }

    // -------- approveMeeting() 测试 --------
    @Test
    void approveMeeting_success() {
        when(meetingMapper.updateMeetingStatus(1, MeetingStatus.APPROVED.getCode())).thenReturn(1);

        MeetingController.Result result = meetingController.approveMeeting(1);
        assertEquals(200, result.getCode());
        assertEquals("会议已通过", result.getMsg());
    }

    @Test
    void approveMeeting_fail() {
        when(meetingMapper.updateMeetingStatus(1, MeetingStatus.APPROVED.getCode())).thenReturn(0);

        MeetingController.Result result = meetingController.approveMeeting(1);
        assertEquals(500, result.getCode());
        assertEquals("会议不存在或状态更新失败", result.getMsg());
    }

    // -------- rejectMeeting() 测试 --------
    @Test
    void rejectMeeting_success() {
        when(meetingMapper.updateMeetingStatus(1, MeetingStatus.REJECTED.getCode())).thenReturn(1);

        MeetingController.Result result = meetingController.rejectMeeting(1);
        assertEquals(200, result.getCode());
        assertEquals("会议已驳回", result.getMsg());
    }

    @Test
    void rejectMeeting_fail() {
        when(meetingMapper.updateMeetingStatus(1, MeetingStatus.REJECTED.getCode())).thenReturn(0);

        MeetingController.Result result = meetingController.rejectMeeting(1);
        assertEquals(500, result.getCode());
        assertEquals("会议不存在或状态更新失败", result.getMsg());
    }

    // -------- searchMeeting() 测试 --------
    @Test
    void searchMeeting_success() {
        when(meetingMapper.searchMeeting(anyString(), any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(new Meeting()));
        when(meetingMapper.countMeeting(anyString(), any(), any(), any(), any())).thenReturn(1);

        Map<String, Object> res = meetingController.searchMeeting(1, 10, null, null, null, null, "张三");
        assertEquals(200, res.get("code"));
        assertEquals(1, res.get("total"));
        List<?> list = (List<?>) res.get("list");
        assertEquals(1, list.size());
    }

    // -------- updateMeeting() 测试 --------
    @Test
    void updateMeeting_success() {
        Meeting meeting = new Meeting();
        when(meetingMapper.updateMeeting(any())).thenReturn(1);

        Response<?> response = meetingController.updateMeeting(1, meeting);
        assertEquals(200, response.getCode());
        assertEquals("更新成功", response.getData());
        assertEquals(1, meeting.getId());
    }

    @Test
    void updateMeeting_fail() {
        Meeting meeting = new Meeting();
        when(meetingMapper.updateMeeting(any())).thenReturn(0);

        Response<?> response = meetingController.updateMeeting(1, meeting);
        assertEquals(500, response.getCode());
        assertEquals("更新失败", response.getMsg());
    }
    @Test
    void resultSettersTest() {
        MeetingController.Result result = new MeetingController.Result(0, null, null);

        result.setCode(200);
        result.setMsg("测试消息");
        result.setData("测试数据");

        assertEquals(200, result.getCode());
        assertEquals("测试消息", result.getMsg());
        assertEquals("测试数据", result.getData());
    }

}
