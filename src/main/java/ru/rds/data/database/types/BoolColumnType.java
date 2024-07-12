package ru.rds.data.database.types;

import java.sql.Types;
import ru.rds.data.database.ColumnType;

/**
 * Описание типа BOOL
 *
 * @author RDS
 * @version 1
 * @since 1.0.0
 */
public class BoolColumnType extends ColumnType {

	public BoolColumnType() {
		setTypeCode(Types.BIT);
		setTypeName("bool");
		setBoollable(true);
	}

}
