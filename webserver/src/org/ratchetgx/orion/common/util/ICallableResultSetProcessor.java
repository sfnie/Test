package org.ratchetgx.orion.common.util;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract interface ICallableResultSetProcessor {

    public abstract void processCallableStatement(CallableStatement cstmt) throws SQLException;

    public abstract void processResultSet(ResultSet rs) throws SQLException;
}
