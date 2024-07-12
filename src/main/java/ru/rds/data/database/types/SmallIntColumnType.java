package ru.rds.data.database.types;

import java.sql.Types;
import ru.rds.data.database.ColumnType;

/**
 * Описание типа SMALLINT
 *
 * @author RDS
 * @version 1
 * @since 1.0.0
 */
public class SmallIntColumnType extends ColumnType {

	public SmallIntColumnType() {
		setTypeCode(Types.SMALLINT);
		setTypeName("smallint");
		setNumeric(true);
	}

}
