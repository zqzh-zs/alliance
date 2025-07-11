package com.neu.alliance.service;

import com.neu.alliance.entity.AttendanceForm;
import com.neu.alliance.entity.MeetingListData;
import com.neu.alliance.entity.MeetingTracker;
import com.neu.alliance.entity.MeetingType;

import java.util.List;

public interface MeetingService {

    MeetingListData getApprovedMeetingsByType(MeetingType type, int page, int pageSize);

    boolean submitAttendanceForm(AttendanceForm attendanceForm);

    boolean track(java.util.List<com.neu.alliance.entity.MeetingTracker> meetingTracker);
}
