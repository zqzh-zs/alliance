package com.zhu.androidalliance.pojo.response;

import com.zhu.androidalliance.pojo.dataObject.NewsInfo;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsListData {
    private int total;
    private List<NewsInfo> list;
    private int pageNum;
    private int pageSize;
    private int totalPages;

}