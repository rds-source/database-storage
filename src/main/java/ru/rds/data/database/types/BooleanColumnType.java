package ru.rds.data.database.types;

import java.sql.Types;
import ru.rds.data.database.ColumnType;

/**
 * Описание типа BOOLEAN
 *
 * @author RDS
 * @version 1
 * @since 1.0.0
 */
public class BooleanColumnType extends ColumnType {

	public BooleanColumnType() {
		setTypeCode(Types.BOOLEAN);
		setTypeName("boolean");
		setBoollable(true);
	}

}
