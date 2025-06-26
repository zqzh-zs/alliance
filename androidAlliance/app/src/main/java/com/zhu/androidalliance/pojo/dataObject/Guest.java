package com.zhu.androidalliance.pojo.dataObject;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Guest implements Serializable {
    private String name;
    private String title;
    private String organization;
}