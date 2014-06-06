package org.ratchetgx.orion.common.util;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract interface IPreparedStatementProcessor
{
  public abstract void process(PreparedStatement pstmt)
    throws SQLException;
}

