package com.neu.alliance.entity;



import com.neu.alliance.dto.MeetingDTO;

import java.io.Serializable;
import java.util.List;

public class MeetingListData implements Serializable {

    private int total;
    private List<MeetingDTO> list;

    public MeetingListData() {
    }

    public MeetingListData(int total, List<MeetingDTO> list) {
        this.total = total;
        this.list = list;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<MeetingDTO> getList() {
        return list;
    }

    public void setList(List<MeetingDTO> list) {
        this.list = list;
    }
}