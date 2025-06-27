package com.neu.alliance.common.utils;



import com.neu.alliance.entity.MeetingStatus;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(MeetingStatus.class)
public class MeetingStatusTypeHandler extends BaseTypeHandler<MeetingStatus> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, MeetingStatus parameter, JdbcType jdbcType) throws SQLException, SQLException {
        ps.setString(i, parameter.getCode());  // 存入code
    }

    @Override
    public MeetingStatus getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String code = rs.getString(columnName);
        return code == null ? null : MeetingStatus.fromCode(code);
    }

    @Override
    public MeetingStatus getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String code = rs.getString(columnIndex);
        return code == null ? null : MeetingStatus.fromCode(code);
    }

    @Override
    public MeetingStatus getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String code = cs.getString(columnIndex);
        return code == null ? null : MeetingStatus.fromCode(code);
    }
}
