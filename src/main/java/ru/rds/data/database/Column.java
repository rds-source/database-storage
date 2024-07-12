package ru.rds.data.database;

import ru.rds.data.storage.SpaceProperty;

/**
 * Колонка - реализауия {@link SpaceProperty} при хранении данных в БД
 *
 * @author RDS
 * @version 1
 * @since 1.0.0
 */
public class Column implements SpaceProperty<ColumnType> {

	// Название
	private String     name;
	// Метка
	private String     label;
	// Внутренний тип
	private ColumnType type;
	// Имя Java-класса, который является типом хранимого значения
	private String     className;
	// Размер
	private int        size;
	// Является ли автоинкрементным
	private boolean    autoincrement;
	// Является ли валютой
	private boolean    currency;
	// Чувствителен ли в регистру
	private boolean    caseSensitive;
	// Может ли не хранить значения
	private boolean    nullable;
	// Является ли уникальным
	private boolean    unique;
	// Доступен ли для поиска
	private boolean    searchable;
	// Определен ли доступ для записи
	private boolean    definitelyWritable;
	// Является ли только для чтения
	private boolean    readOnly;
	// Является ли подписанным
	private boolean    signed;
	// Является ли перезаписываемым
	private boolean    writable;
	// Является ли первичным ключом
	private boolean    primaryKey;
	// Дополнительное описание
	private String     comments;
	// Порядковая позиция
	private int        ordinalPosition;

	/**
	 * Получение названия
	 *
	 * @return
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * Установление названия
	 *
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Получение метки
	 *
	 * @return
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Установление метки
	 *
	 * @param label
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Получение внутреннего типа
	 *
	 * @return
	 */
	@Override
	public ColumnType getType() {
		return type;
	}

	/**
	 * Установление внутреннего типа
	 *
	 * @param type
	 */
	public void setType(ColumnType type) {
		this.type = type;
	}

	/**
	 * Получение имени Java-класса, который является типом хранимого значения
	 *
	 * @return
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * Установление имени Java-класса, который является типом хранимого значения
	 *
	 * @param className
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * Получение размера
	 *
	 * @return
	 */
	@Override
	public int getSize() {
		return size;
	}

	/**
	 * Установление размера
	 *
	 * @param size
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * Является ли автоинкрементным
	 *
	 * @return
	 */
	public boolean isAutoincrement() {
		return autoincrement;
	}

	/**
	 * Является ли автоинкрементным
	 *
	 * @param autoincrement
	 */
	public void setAutoincrement(boolean autoincrement) {
		this.autoincrement = autoincrement;
	}

	/**
	 * Является ли валютой
	 *
	 * @return
	 */
	public boolean isCurrency() {
		return currency;
	}

	/**
	 * Является ли валютой
	 *
	 * @param currency
	 */
	public void setCurrency(boolean currency) {
		this.currency = currency;
	}

	/**
	 * Чувствителен ли в регистру
	 *
	 * @return
	 */
	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	/**
	 * Чувствителен ли в регистру
	 *
	 * @param caseSensitive
	 */
	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	/**
	 * Может ли не хранить значения
	 *
	 * @return
	 */
	@Override
	public boolean isNullable() {
		return nullable;
	}

	/**
	 * Может ли не хранить значения
	 *
	 * @param nullable
	 */
	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	/**
	 * Является ли уникальным
	 *
	 * @return
	 */
	@Override
	public boolean isUnique() {
		return unique;
	}

	/**
	 * Является ли уникальным
	 *
	 * @param unique
	 */
	public void setUnique(boolean unique) {
		this.unique = unique;
	}

	/**
	 * Доступен ли для поиска
	 *
	 * @return
	 */
	public boolean isSearchable() {
		return searchable;
	}

	/**
	 * Доступен ли для поиска
	 *
	 * @param searchable
	 */
	public void setSearchable(boolean searchable) {
		this.searchable = searchable;
	}

	/**
	 * Определен ли доступ для записи
	 *
	 * @return
	 */
	public boolean isDefinitelyWritable() {
		return definitelyWritable;
	}

	/**
	 * Определен ли доступ для записи
	 *
	 * @param definitelyWritable
	 */
	public void setDefinitelyWritable(boolean definitelyWritable) {
		this.definitelyWritable = definitelyWritable;
	}

	/**
	 * Является ли только для чтения
	 *
	 * @return
	 */
	public boolean isReadOnly() {
		return readOnly;
	}

	/**
	 * Является ли только для чтения
	 *
	 * @param readOnly
	 */
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	/**
	 * Является ли подписанным
	 *
	 * @return
	 */
	public boolean isSigned() {
		return signed;
	}

	/**
	 * Является ли подписанным
	 *
	 * @param signed
	 */
	public void setSigned(boolean signed) {
		this.signed = signed;
	}

	/**
	 * Является ли перезаписываемым
	 *
	 * @return
	 */
	public boolean isWritable() {
		return writable;
	}

	/**
	 * Является ли перезаписываемым
	 *
	 * @param writable
	 */
	public void setWritable(boolean writable) {
		this.writable = writable;
	}

	/**
	 * Является ли первичным ключом
	 *
	 * @return
	 */
	@Override
	public boolean isPrimaryKey() {
		return primaryKey;
	}

	/**
	 * Является ли первичным ключом
	 *
	 * @param primaryKey
	 */
	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	/**
	 * Получение дополнительного описания
	 *
	 * @return
	 */
	public String getComments() {
		return comments;
	}

	/**
	 * Установление дополнительного комментария
	 *
	 * @param comments
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}

	/**
	 * Получение дополнительного описания
	 *
	 * @return
	 */
	@Override
	public String getDescription() {
		return getComments();
	}

	/**
	 * Установление порядковой позиции
	 *
	 * @param ordinalPosition
	 */
	public void setOrdinalPosition(int ordinalPosition) {
		this.ordinalPosition = ordinalPosition;
	}

	/**
	 * Получение порядковой позиции
	 *
	 * @return
	 */
	@Override
	public int getOrdinalPosition() {
		return ordinalPosition;
	}

}
