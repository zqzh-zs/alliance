package com.neu.alliance.service;

import com.neu.alliance.entity.MeetingListData;
import com.neu.alliance.entity.MeetingType;

public interface MeetingService {

    MeetingListData getApprovedMeetingsByType(MeetingType type, int page, int pageSize);

}
