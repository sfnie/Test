package org.ratchetgx.orion.common.util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract interface IPreparedResultSetProcessor {

    public abstract void processPreparedStatement(PreparedStatement pstmt)
            throws SQLException;

    public abstract void processResultSet(ResultSet rs) throws SQLException;
}
