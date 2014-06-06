package org.ratchetgx.orion.common.util;

import java.sql.CallableStatement;
import java.sql.SQLException;

public interface ICallableStatementProcessor {

    public abstract void processBefore(CallableStatement cstmt)
            throws SQLException;

    public abstract void processAfter(CallableStatement cstmt)
            throws SQLException;
}
