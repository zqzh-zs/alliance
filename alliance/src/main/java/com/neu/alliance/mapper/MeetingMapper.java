package com.neu.alliance.mapper;



import com.neu.alliance.entity.AttendanceForm;
import com.neu.alliance.entity.Meeting;
import com.neu.alliance.entity.MeetingType;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MeetingMapper {
    /**
     * 插入一条新的会议记录，自动生成ID并回写到实体。
     *
     * @param meeting 会议实体，包含会议名称、时间、地点等信息
     * @return 影响的行数（通常为1）
     */
    @Insert("INSERT INTO meetings (title, summary, start_time, end_time, location, organizer, image_url, type, status, create_time) " +
            "VALUES (#{title}, #{summary}, #{start_time}, #{end_time}, #{location}, #{organizer}, #{imageUrl}, #{type}, #{status}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertMeeting(Meeting meeting);


    /**
     * 查询指定类型且状态为“已通过”的会议，按开始时间降序排序。
     *
     * @param type 会议类型枚举
     * @return 满足条件的会议列表
     */
    @Select("SELECT * FROM meetings WHERE status='APPROVED' AND type=#{type} ORDER BY start_time DESC")
    List<Meeting> selectApprovedByType(@Param("type") MeetingType type);


    /**
     * 查询所有会议，可按状态过滤。
     * 如果status为空则返回所有会议。
     *
     * @param status 会议状态，如PENDING, APPROVED等，传null表示不过滤
     * @return 满足状态条件的会议列表
     */
    @Select({
            "<script>",
            "SELECT * FROM meetings",
            "<where>",
            "  <if test='status != null and status != \"\"'>",
            "    status = #{status}",
            "  </if>",
            "</where>",
            "ORDER BY start_time DESC",
            "</script>"
    })
    List<Meeting> selectAllByStatus(@Param("status") String status);

    /**
     * 根据会议ID查询会议详细信息。
     *
     * @param id 会议ID
     * @return 会议实体对象，找不到时返回null
     */
    @Select("SELECT id, title, summary, start_time, end_time, location, " +
            "organizer, image_url as imageUrl, type, status,create_time " +
            "FROM meetings WHERE id = #{id}")
    Meeting selectMeetingDetail(@Param("id") int id);

    //与移动端进行通信
    @Select("SELECT * FROM meetings WHERE type = #{type} AND status = 'APPROVED' ORDER BY start_time DESC LIMIT #{offset}, #{pageSize}")
    List<Meeting> selectApprovedMeetingsByType(
            @Param("type") String type,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize
    );
    @Select("SELECT COUNT(*) FROM meetings WHERE type = #{type} AND status = 'APPROVED'")
    int countApprovedMeetingsByType(@Param("type") String type);
    @Select({
            "<script>",
            "SELECT * FROM meetings WHERE status = 'APPROVED'",
            "<if test='type != null and type != \"\"'> AND type = #{type} </if>",
            "<if test='keyword != null and keyword != \"\"'> AND title LIKE CONCAT(#{keyword}, '%') </if>",
            "<if test='organizer != null and organizer != \"\"'> AND organizer LIKE CONCAT(#{organizer}, '%') </if>",
            "<if test='startDate != null and startDate != \"\"'> AND DATE(start_time) &gt;= #{startDate} </if>",
            "<if test='endDate != null and endDate != \"\"'> AND DATE(start_time) &lt;= #{endDate} </if>",
            "ORDER BY start_time DESC",
            "LIMIT #{offset}, #{pageSize}",
            "</script>"
    })
    List<Meeting> getApprovedMeetings(
            @Param("type") String type,
            @Param("keyword") String keyword,
            @Param("organizer") String organizer,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize
    );

    @Select({
            "<script>",
            "SELECT COUNT(*) FROM meetings WHERE status = 'APPROVED'",
            "<if test='type != null and type != \"\"'> AND type = #{type} </if>",
            "<if test='keyword != null and keyword != \"\"'> AND title LIKE CONCAT(#{keyword}, '%') </if>",
            "<if test='organizer != null and organizer != \"\"'> AND organizer LIKE CONCAT(#{organizer}, '%') </if>",
            "<if test='startDate != null and startDate != \"\"'> AND DATE(start_time) &gt;= #{startDate} </if>",
            "<if test='endDate != null and endDate != \"\"'> AND DATE(start_time) &lt;= #{endDate} </if>",
            "</script>"
    })
    int countApprovedMeetings(
            @Param("type") String type,
            @Param("keyword") String keyword,
            @Param("organizer") String organizer,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );


    @Select({
            "<script>",
            "SELECT * FROM meetings",
            "WHERE 1=1",
            "<if test='type != null and type != \"\"'> AND type = #{type} </if>",
            "<if test='keyword != null and keyword != \"\"'> AND title LIKE CONCAT('%', #{keyword}, '%') </if>",
            "<if test='organizer != null and organizer != \"\"'> AND organizer LIKE CONCAT('%', #{organizer}, '%') </if>",
            "<if test='startDate != null and startDate != \"\"'> AND start_time &gt;= #{startDate} </if>",
            "<if test='endDate != null and endDate != \"\"'> AND end_time &lt;= #{endDate} </if>",
            "ORDER BY create_time DESC",
            "LIMIT #{offset}, #{pageSize}",
            "</script>"
    })
    List<Meeting> searchMeetings(
            @Param("type") String type,
            @Param("keyword") String keyword,
            @Param("organizer") String organizer,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize
    );

    @Select({
            "<script>",
            "SELECT COUNT(*) FROM meetings",
            "WHERE 1=1",
            "<if test='type != null and type != \"\"'> AND type = #{type} </if>",
            "<if test='keyword != null and keyword != \"\"'> AND title LIKE CONCAT('%', #{keyword}, '%') </if>",
            "<if test='organizer != null and organizer != \"\"'> AND organizer LIKE CONCAT('%', #{organizer}, '%') </if>",
            "<if test='startDate != null and startDate != \"\"'> AND start_time &gt;= #{startDate} </if>",
            "<if test='endDate != null and endDate != \"\"'> AND end_time &lt;= #{endDate} </if>",
            "</script>"
    })
    int countMeetings(
            @Param("type") String type,
            @Param("keyword") String keyword,
            @Param("organizer") String organizer,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    //用于删除会议
    @Delete("DELETE FROM meetings WHERE id = #{id}")
    void deleteById(@Param("id") int id);

    //用于审核会议
    @Update("UPDATE meetings SET status = #{status} WHERE id = #{id}")
    int updateMeetingStatus(@Param("id") int id, @Param("status") String status);

    @Select({
            "<script>",
            "SELECT * FROM meetings WHERE 1=1",
            "<if test='organizer != null and organizer != \"\"'> AND organizer = #{organizer} </if>",
            "<if test='type != null and type != \"\"'> AND type = #{type} </if>",
            "<if test='keyword != null and keyword != \"\"'> AND title LIKE CONCAT('%',#{keyword},'%') </if>",
            "<if test='startDate != null and startDate != \"\"'> AND start_time &gt;= #{startDate} </if>",
            "<if test='endDate != null and endDate != \"\"'> AND end_time &lt;= #{endDate} </if>",
            "ORDER BY start_time DESC",
            "LIMIT #{offset}, #{pageSize}",
            "</script>"
    })
    List<Meeting> searchMeeting(
            @Param("organizer") String organizer,
            @Param("type") String type,
            @Param("keyword") String keyword,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize);


    @Select({
            "<script>",
            "SELECT count(*) FROM meetings WHERE 1=1",
            "<if test='organizer != null and organizer != \"\"'> AND organizer = #{organizer} </if>",
            "<if test='type != null and type != \"\"'> AND type = #{type} </if>",
            "<if test='keyword != null and keyword != \"\"'> AND title LIKE CONCAT('%',#{keyword},'%') </if>",
            "<if test='startDate != null and startDate != \"\"'> AND start_time &gt;= #{startDate} </if>",
            "<if test='endDate != null and endDate != \"\"'> AND end_time &lt;= #{endDate} </if>",
            "</script>"
    })
    int countMeeting(
            @Param("organizer") String organizer,
            @Param("type") String type,
            @Param("keyword") String keyword,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate);


    //用于更新会议
    @Update("""
        UPDATE meetings
        SET 
            title = #{title},
            summary = #{summary},
            start_time = #{start_time},
            end_time = #{end_time},
            location = #{location},
            image_url = #{imageUrl},
            type = #{type}
        WHERE id = #{id}
    """)
    int updateMeeting(Meeting meeting);


}
