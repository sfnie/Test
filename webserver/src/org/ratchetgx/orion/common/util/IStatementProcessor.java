package org.ratchetgx.orion.common.util;

import java.sql.SQLException;
import java.sql.Statement;

public abstract interface IStatementProcessor {
	public abstract void process(Statement paramStatement) throws SQLException;
}
