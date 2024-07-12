package ru.rds.data.database;

import java.util.ArrayList;
import java.util.List;


/**
 * Описание Индекса для БД
 *
 * @author RDS
 * @version 1
 * @since 1.0.0
 */
public class Index {

	// Название индекса
	private String       name;
	// Признак уникальности
	private boolean      unique;
	// Для JSON типа или нет
	private boolean      onJson;
	// Перечень колонок, входящи в индекс
	private List<Column> columns;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isUnique() {
		return unique;
	}

	public void setUnique(boolean unique) {
		this.unique = unique;
	}

	public boolean isOnJson() {
		return onJson;
	}

	public void setOnJson(boolean onJson) {
		this.onJson = onJson;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	public List<Column> getColumns() {
		if (columns == null) {
			columns = new ArrayList<>();
		}
		return columns;
	}

	public void addColumn(Column column) {
		if (column != null) {
			List<Column> columns = getColumns();
			if (!columns.contains(column)) {
				columns.add(column);
			}
		}
	}

}
