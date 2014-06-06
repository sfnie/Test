package org.ratchetgx.orion.common.util;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract interface IArrayPreparedStatementProcessor
{
  public abstract void process(PreparedStatement[] paramArrayOfPreparedStatement)
    throws SQLException;
}

