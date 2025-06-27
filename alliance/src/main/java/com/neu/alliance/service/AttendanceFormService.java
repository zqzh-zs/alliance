package com.neu.alliance.service;

import com.neu.alliance.dto.AttendanceFormDTO;

public interface AttendanceFormService {
    boolean submitAttendanceForm(AttendanceFormDTO attendanceFormDTO);
}
