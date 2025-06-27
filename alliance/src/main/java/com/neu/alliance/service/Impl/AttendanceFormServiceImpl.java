package com.neu.alliance.service.Impl;

import com.neu.alliance.dto.AttendanceFormDTO;
import com.neu.alliance.entity.AttendanceForm;
import com.neu.alliance.mapper.AttendanceFormMapper;
import com.neu.alliance.service.AttendanceFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AttendanceFormServiceImpl implements AttendanceFormService {

    @Autowired
    private AttendanceFormMapper attendanceFormMapper;

    @Override
    public boolean submitAttendanceForm(AttendanceFormDTO attendanceFormDTO) {
        // 将 DTO 转换为实体类
        AttendanceForm attendanceForm = new AttendanceForm();
        attendanceForm.setName(attendanceFormDTO.getName());
        attendanceForm.setOrganization(attendanceFormDTO.getOrganization());
        attendanceForm.setPhone(attendanceFormDTO.getPhone());
        attendanceForm.setEmail(attendanceFormDTO.getEmail());
        attendanceForm.setTrip(attendanceFormDTO.getTrip());
        attendanceForm.setTime(attendanceFormDTO.getTime());

        // 调用 Mapper 执行插入操作
        int rows = attendanceFormMapper.insertAttendanceForm(attendanceForm);

        // 判断插入是否成功
        return rows > 0;
    }
}