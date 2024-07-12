package ru.rds.data.database;

import java.util.*;
import ru.rds.data.storage.Element;

/**
 * Строка - реализация {@link Element} при хранении данных в БД
 *
 * @author RDS
 * @version 1
 * @since 1.0.0
 */
public class Row implements Element<Column> {

	// Перечень Столбцов, из которых состоит Строка
	private List<Column>        columns;
	// Перечень значений Столбцов, где в качестве ключа используется название Столбца
	private Map<String, Object> values;

	public Row(List<Column> columns) {
		this.columns = columns;
		this.values = new HashMap<>();
	}

	@Override
	public List<Column> getSpaceProperties() {
		return this.columns;
	}

	@Override
	public Map<String, Object> getValues() {
		return values;
	}

	public void setValues(Map<String, Object> values) {
		this.values = values;
	}

	public void setValue(String columnName, Object value) {
		getValues().put(columnName, value);
	}

	@Override
	public Optional<Object> getValue(String columnName) {
		return Optional.ofNullable(getValues().get(columnName));
	}

}
