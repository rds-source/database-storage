package ru.rds.data.database.types;

import java.sql.Types;
import ru.rds.data.database.ColumnType;

/**
 * Описание типа JSONB
 *
 * @author RDS
 * @version 1
 * @since 1.0.0
 */
public class JsonbColumnType extends ColumnType {

	public JsonbColumnType() {
		setTypeCode(Types.OTHER);
		setTypeName("jsonb");
		setBinary(true);
	}

}
