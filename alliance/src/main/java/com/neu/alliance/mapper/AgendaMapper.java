package com.neu.alliance.mapper;

import com.neu.alliance.entity.AgendaItem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 竺专属
 * Date: 2025-06-29
 * Time: 19:38
 */
@Mapper
public interface AgendaMapper {
    List<AgendaItem> getAgendaItemsByMeetingId(Integer meetingId);
}
