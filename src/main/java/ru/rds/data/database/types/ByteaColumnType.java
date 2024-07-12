package ru.rds.data.database.types;

import java.sql.Types;
import ru.rds.data.database.ColumnType;

/**
 * Описание типа BYTEA
 *
 * @author RDS
 * @version 1
 * @since 1.0.0
 */
public class ByteaColumnType extends ColumnType {

	public ByteaColumnType() {
		setTypeCode(Types.BINARY);
		setTypeName("bytea");
		setBinary(true);
	}

}
