package com.neu.alliance.mapper;

import com.neu.alliance.entity.Guest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 竺专属
 * Date: 2025-06-29
 * Time: 19:31
 */
@Mapper
public interface GuestMapper {
    List<Guest> getGuestsByMeetingId(Integer meetingId);
}
