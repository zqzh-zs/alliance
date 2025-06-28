package com.neu.alliance.controller;

import com.neu.alliance.dto.AttendanceFormDTO;
import com.neu.alliance.service.AttendanceFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/submitAttendance")
public class AttendanceFormController {

    @Autowired
    private AttendanceFormService attendanceFormService;

    @PostMapping
    public String submitAttendanceForm(@RequestBody AttendanceFormDTO attendanceFormDTO) {
        boolean success = attendanceFormService.submitAttendanceForm(attendanceFormDTO);
        if (success) {
            return "提交成功";
        } else {
            return "提交失败";
        }
    }
}