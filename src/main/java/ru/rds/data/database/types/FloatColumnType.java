package ru.rds.data.database.types;

import java.sql.Types;
import ru.rds.data.database.ColumnType;

/**
 * Описание типа FLOAT
 *
 * @author RDS
 * @version 1
 * @since 1.0.0
 */
public class FloatColumnType extends ColumnType {

	public FloatColumnType() {
		setTypeCode(Types.FLOAT);
		setTypeName("float");
		setNumeric(true);
	}

}
