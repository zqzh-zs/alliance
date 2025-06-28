package com.neu.alliance.service.Impl;



import com.neu.alliance.dto.MeetingDTO;
import com.neu.alliance.entity.Meeting;
import com.neu.alliance.entity.MeetingListData;
import com.neu.alliance.entity.MeetingType;
import com.neu.alliance.mapper.MeetingMapper;
import com.neu.alliance.service.MeetingService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MeetingServiceImpl implements MeetingService {

    private final MeetingMapper meetingMapper;

    public MeetingServiceImpl(MeetingMapper meetingMapper) {
        this.meetingMapper = meetingMapper;
    }



    @Override
    public MeetingListData getApprovedMeetingsByType(MeetingType type, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Meeting> meetings = meetingMapper.selectApprovedMeetingsByType(type.name(), offset, pageSize);
        int total = meetingMapper.countApprovedMeetingsByType(type.name());

        List<MeetingDTO> meetingDTOList = meetings.stream()
                .map(MeetingDTO::new)
                .collect(Collectors.toList());

        MeetingListData data = new MeetingListData();
        data.setTotal(total);
        data.setList(meetingDTOList);
        return data;
    }
}
