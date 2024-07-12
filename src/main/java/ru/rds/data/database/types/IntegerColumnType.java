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
public class IntegerColumnType extends ColumnType {

	public IntegerColumnType() {
		setTypeCode(Types.INTEGER);
		setTypeName("integer");
		setNumeric(true);
	}

}
