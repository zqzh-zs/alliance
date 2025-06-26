package com.zhu.androidalliance.pojo.response;

import com.zhu.androidalliance.pojo.dataObject.Meeting;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeetingListData implements Serializable {
    private int total;
    private List<Meeting> list;

}