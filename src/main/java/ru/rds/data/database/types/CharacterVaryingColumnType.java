package ru.rds.data.database.types;

import java.sql.Types;
import ru.rds.data.database.ColumnType;

/**
 * Описание типа CHARACTER VARYING
 *
 * @author RDS
 * @version 1
 * @since 1.0.0
 */
public class CharacterVaryingColumnType extends ColumnType {

	public CharacterVaryingColumnType() {
		setTypeCode(Types.VARCHAR);
		setTypeName("character varying");
		setCharacters(true);
		setSizeable(true);
		setDefaultSize(255);
	}

}
