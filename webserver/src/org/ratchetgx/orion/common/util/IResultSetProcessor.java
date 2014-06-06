package org.ratchetgx.orion.common.util;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract interface IResultSetProcessor
{
  public abstract void process(ResultSet rs)
    throws SQLException;
}

