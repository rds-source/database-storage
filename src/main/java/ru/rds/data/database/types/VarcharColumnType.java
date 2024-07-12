package ru.rds.data.database.types;

import java.sql.Types;
import ru.rds.data.database.ColumnType;

/**
 * Описание типа VARCHAR
 *
 * @author RDS
 * @version 1
 * @since 1.0.0
 */
public class VarcharColumnType extends ColumnType {

	public VarcharColumnType() {
		setTypeCode(Types.VARCHAR);
		setTypeName("varchar");
		setCharacters(true);
		setSizeable(true);
		setDefaultSize(255);
	}

}
