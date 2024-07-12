package ru.rds.data.database.types;

import java.sql.Types;
import ru.rds.data.database.ColumnType;

/**
 * Описание типа BIGINT
 *
 * @author RDS
 * @version 1
 * @since 1.0.0
 */
public class BigIntColumnType extends ColumnType {

	public BigIntColumnType() {
		setTypeCode(Types.BIGINT);
		setTypeName("bigint");
		setNumeric(true);
	}

}
