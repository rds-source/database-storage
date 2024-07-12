package ru.rds.data.database.types;

import java.sql.Types;
import ru.rds.data.database.ColumnType;

/**
 * Описание типа INTEGER
 *
 * @author RDS
 * @version 1
 * @since 1.0.0
 */
public class Int4ColumnType extends ColumnType {

	public Int4ColumnType() {
		setTypeCode(Types.INTEGER);
		setTypeName("int4");
		setNumeric(true);
	}

}
