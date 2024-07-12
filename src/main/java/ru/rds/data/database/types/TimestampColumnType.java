package ru.rds.data.database.types;

import java.sql.Types;
import ru.rds.data.database.ColumnType;

/**
 * Описание типа TIMESTAMP
 *
 * @author RDS
 * @version 1
 * @since 1.0.0
 */
public class TimestampColumnType extends ColumnType {

	public TimestampColumnType() {
		setTypeCode(Types.TIMESTAMP);
		setTypeName("timestamp");
		setDateTime(true);
	}

}
