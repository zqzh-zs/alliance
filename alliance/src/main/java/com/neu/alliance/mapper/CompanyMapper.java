package com.neu.alliance.mapper;

import com.neu.alliance.entity.Company;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Title: CompanyMapper
 * @Author 曦
 * @Date 2025/6/17 12:18
 * @description:
 */
@Mapper
public interface CompanyMapper {
    /*
    * 根据企业名称查询企业
     */
    Company selectByCompanyName(String companyName);

    /**
     * 检查企业名称是否存在
     */
    int countByCompanyName(String companyName);

    /**
     * 插入新企业（返回自增ID）
     */
    int insert(Company company);

}
