package com.neu.alliance.commom.utils;

import com.neu.alliance.common.utils.MeetingStatusTypeHandler;
import com.neu.alliance.entity.MeetingStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MeetingStatusTypeHandlerTest {

    private MeetingStatusTypeHandler handler;

    @BeforeEach
    void setUp() {
        handler = new MeetingStatusTypeHandler();
    }

    @Test
    void testSetNonNullParameter() throws SQLException {
        PreparedStatement ps = mock(PreparedStatement.class);
        handler.setNonNullParameter(ps, 1, MeetingStatus.APPROVED, null);
        verify(ps, times(1)).setString(1, MeetingStatus.APPROVED.getCode());
    }

    @Test
    void testGetNullableResultByColumnName_withValidCode() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getString("status")).thenReturn("APPROVED");

        MeetingStatus status = handler.getNullableResult(rs, "status");

        assertEquals(MeetingStatus.APPROVED, status);
    }

    @Test
    void testGetNullableResultByColumnName_withNull() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getString("status")).thenReturn(null);

        MeetingStatus status = handler.getNullableResult(rs, "status");

        assertNull(status);
    }

    @Test
    void testGetNullableResultByColumnIndex_withValidCode() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getString(1)).thenReturn("PENDING");

        MeetingStatus status = handler.getNullableResult(rs, 1);

        assertEquals(MeetingStatus.PENDING, status);
    }

    @Test
    void testGetNullableResultByColumnIndex_withNull() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getString(1)).thenReturn(null);

        MeetingStatus status = handler.getNullableResult(rs, 1);

        assertNull(status);
    }

    @Test
    void testGetNullableResultFromCallableStatement_withValidCode() throws SQLException {
        CallableStatement cs = mock(CallableStatement.class);
        when(cs.getString(2)).thenReturn("REJECTED");

        MeetingStatus status = handler.getNullableResult(cs, 2);

        assertEquals(MeetingStatus.REJECTED, status);
    }

    @Test
    void testGetNullableResultFromCallableStatement_withNull() throws SQLException {
        CallableStatement cs = mock(CallableStatement.class);
        when(cs.getString(2)).thenReturn(null);

        MeetingStatus status = handler.getNullableResult(cs, 2);

        assertNull(status);
    }
}
