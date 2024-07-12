package ru.rds.data.database;

import java.sql.Types;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.rds.data.database.types.*;
import ru.rds.data.storage.SpacePropertyType;

/**
 * Тип Колонки ({@link Column}).
 * <p>
 * Тип позволяет понять какого рода данные будут храниться (или хранятся) в качестве значений в БД.
 *
 * @author RDS
 * @version 1
 * @since 1.0.0
 */
public class ColumnType implements SpacePropertyType {

	private static final Logger logger = LoggerFactory.getLogger(ColumnType.class);

	// ================================================================================ //
	// Основные типы данных, которые используются в БД                                  //
	// ================================================================================ //
	public static final ColumnType SMALLINT          = new SmallIntColumnType();
	public static final ColumnType INTEGER           = new IntegerColumnType();
	public static final ColumnType BIGINT            = new BigIntColumnType();
	public static final ColumnType INT8              = new Int8ColumnType();
	public static final ColumnType INT4              = new Int4ColumnType();
	public static final ColumnType FLOAT             = new FloatColumnType();
	public static final ColumnType VARCHAR           = new VarcharColumnType();
	public static final ColumnType CHARACTER_VARYING = new CharacterVaryingColumnType();
	public static final ColumnType TEXT              = new TextColumnType();
	public static final ColumnType TIMESTAMP         = new TimestampColumnType();
	public static final ColumnType BOOLEAN           = new BooleanColumnType();
	public static final ColumnType BOOL              = new BoolColumnType();
	public static final ColumnType BYTEA             = new ByteaColumnType();
	public static final ColumnType JSONB             = new JsonbColumnType();
	// ================================================================================ //

	// Перечень всех поддерживаемых типов в виде списка
	private static final List<ColumnType>        VALUES     = new ArrayList<>();
	// То же самое, только в виде Map, где в качестве key выступает название типа
	private static final Map<String, ColumnType> VALUES_MAP = new HashMap<>();

	static {
		VALUES.add(SMALLINT);
		VALUES.add(INTEGER);
		VALUES.add(BIGINT);
		VALUES.add(INT4);
		VALUES.add(INT8);
		VALUES.add(FLOAT);
		VALUES.add(VARCHAR);
		VALUES.add(CHARACTER_VARYING);
		VALUES.add(TEXT);
		VALUES.add(TIMESTAMP);
		VALUES.add(BOOLEAN);
		VALUES.add(BOOL);
		VALUES.add(BYTEA);
		VALUES.add(JSONB);

		VALUES_MAP.put("SMALLINT", SMALLINT);
		VALUES_MAP.put("INTEGER", INTEGER);
		VALUES_MAP.put("BIGINT", BIGINT);
		VALUES_MAP.put("INT4", INT4);
		VALUES_MAP.put("INT8", INT8);
		VALUES_MAP.put("FLOAT", FLOAT);
		VALUES_MAP.put("VARCHAR", VARCHAR);
		VALUES_MAP.put("CHARACTER VARYING", CHARACTER_VARYING);
		VALUES_MAP.put("TEXT", TEXT);
		VALUES_MAP.put("TIMESTAMP", TIMESTAMP);
		VALUES_MAP.put("BOOLEAN", BOOLEAN);
		VALUES_MAP.put("BOOL", BOOL);
		VALUES_MAP.put("BYTEA", BYTEA);
		VALUES_MAP.put("JSONB", JSONB);
	}

	// Числовое значение SQL-типа (java.sql.Types)
	private Integer typeCode;
	// Строковое представление типа в БД
	private String  typeName;
	// Является ли бинарным
	private boolean binary;
	// Является ли числовым
	private boolean numeric;
	// Является ли true или false
	private boolean boollable;
	// Является ли символьным
	private boolean characters;
	// Является ли датой/временем
	private boolean dateTime;
	// Обладает ли размером
	private boolean sizeable;
	// Значение размера по-умолчанию
	private int     defaultSize;

	public ColumnType() {
	}

	/**
	 * Получение соответствующего числового значения SQL-типа (java.sql.Types)
	 *
	 * @return
	 */
	@Override
	public Integer getTypeCode() {
		return typeCode;
	}

	/**
	 * Установление соответствующего числового значения SQL-типа (java.sql.Types)
	 *
	 * @param typeCode
	 */
	public void setTypeCode(Integer typeCode) {
		this.typeCode = typeCode;
	}

	/**
	 * Получение строкового представления типа в БД
	 *
	 * @return
	 */
	@Override
	public String getTypeName() {
		return typeName;
	}

	/**
	 * Установление строкового представления типа в БД
	 *
	 * @param typeName
	 */
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	/**
	 * Является ли бинарным
	 *
	 * @return
	 */
	@Override
	public boolean isBinary() {
		return binary;
	}

	/**
	 * Является ли бинарным
	 *
	 * @param binary
	 */
	public void setBinary(boolean binary) {
		this.binary = binary;
	}

	/**
	 * Является ли числовым
	 *
	 * @return
	 */
	@Override
	public boolean isNumeric() {
		return numeric;
	}

	/**
	 * Является ли числовым
	 *
	 * @param numeric
	 */
	public void setNumeric(boolean numeric) {
		this.numeric = numeric;
	}

	/**
	 * Является ли true или false
	 *
	 * @return
	 */
	@Override
	public boolean isBoollable() {
		return boollable;
	}

	/**
	 * Является ли true или false
	 *
	 * @param boollable
	 */
	public void setBoollable(boolean boollable) {
		this.boollable = boollable;
	}

	/**
	 * Является ли символьным
	 *
	 * @return
	 */
	@Override
	public boolean isCharacters() {
		return characters;
	}

	/**
	 * Является ли символьным
	 *
	 * @param characters
	 */
	public void setCharacters(boolean characters) {
		this.characters = characters;
	}

	/**
	 * Является ли датой/временем
	 *
	 * @return
	 */
	@Override
	public boolean isDateTime() {
		return dateTime;
	}

	/**
	 * Является ли датой/временем
	 *
	 * @param dateTime
	 */
	public void setDateTime(boolean dateTime) {
		this.dateTime = dateTime;
	}

	/**
	 * Обладает ли размером
	 *
	 * @return
	 */
	@Override
	public boolean isSizeable() {
		return sizeable;
	}

	/**
	 * Обладает ли размером
	 *
	 * @param sizeable
	 */
	public void setSizeable(boolean sizeable) {
		this.sizeable = sizeable;
	}

	/**
	 * Получение значения размера по-умолчанию
	 *
	 * @return
	 */
	public int getDefaultSize() {
		return defaultSize;
	}

	/**
	 * Установление значения размера по-умолчанию
	 *
	 * @param defaultSize
	 */
	public void setDefaultSize(int defaultSize) {
		this.defaultSize = defaultSize;
	}

	/**
	 * Позволяет получить {@link ColumnType} по его коду {@link Types} и имени
	 * <p>Поддерживаются только следующие типы, перечисленные в данном классе
	 *
	 * @param typeCode значение из {@link Types}
	 * @param typeName строковое значение
	 * @return
	 */
	public static Optional<ColumnType> of(Integer typeCode, String typeName) {
		if (typeCode != null && typeName != null) {
			typeName = handleDatabaseInnerType(typeCode, typeName);
			for (ColumnType type : VALUES) {
				if (Objects.equals(type.getTypeCode(), typeCode)) {
					if (Objects.equals(type.getTypeName().toLowerCase(), typeName.toLowerCase())) {
						return Optional.ofNullable(type);
					}
				}
			}
		}
		logger.error(String.format("Не удалось найти поддерживаемый тип столбца для значений <%s>:<%s>", typeCode, typeName));
		return Optional.empty();
	}

	// Небольшая обработка внутренних типов в БД
	private static String handleDatabaseInnerType(Integer typeCode, String typeName) {
		if (typeCode == 5 && typeName.equalsIgnoreCase("int2")) {
			typeName = "smallint";
		}
		return typeName;
	}

	/**
	 * Позволяет получить {@link ColumnType} по его названию {@link ColumnType}
	 *
	 * @param name название в {@link ColumnType}
	 * @return
	 */
	public static Optional<ColumnType> of(String name) {
		if (name != null && !name.isEmpty()) {
			return Optional.ofNullable(VALUES_MAP.get(name.toUpperCase()));
		}
		return Optional.empty();
	}

}
