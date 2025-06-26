package com.zhu.androidalliance.pojo.dataObject;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgendaItem implements Serializable {
    private Date startTime;
    private String title;
    private String description;

}