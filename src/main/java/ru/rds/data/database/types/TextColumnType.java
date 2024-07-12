package ru.rds.data.database.types;

import java.sql.Types;
import ru.rds.data.database.ColumnType;

/**
 * Описание типа TEXT
 *
 * @author RDS
 * @version 1
 * @since 1.0.0
 */
public class TextColumnType extends ColumnType {

	public TextColumnType() {
		setTypeCode(Types.VARCHAR);
		setTypeName("text");
		setCharacters(true);
	}

}
