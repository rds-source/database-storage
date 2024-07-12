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
public class Int8ColumnType extends ColumnType {

	public Int8ColumnType() {
		setTypeCode(Types.BIGINT);
		setTypeName("int8");
		setNumeric(true);
	}

}
