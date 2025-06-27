package com.neu.alliance.mapper;

import com.neu.alliance.entity.AttendanceForm;
import org.apache.ibatis.annotations.Insert;

public interface AttendanceFormMapper {
    @Insert("INSERT INTO attendance_form(name, organization, phone, email, trip, time) " +
            "VALUES (#{name}, #{organization}, #{phone}, #{email}, #{trip}, #{time})")
    int insertAttendanceForm(AttendanceForm attendanceForm);
}
