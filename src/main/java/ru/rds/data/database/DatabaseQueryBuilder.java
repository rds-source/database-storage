package ru.rds.data.database;

import java.util.List;
import ru.rds.data.storage.ElementsSelectionCondition;
import ru.rds.data.storage.ElementsSort;
import ru.rds.data.storage.SelectionType;

/**
 * Конструктор SQL-запросов
 * <p>
 * Различные СУБД обладают различными особенностями при работе с ними в виде SQL-запросов. Для того, чтобы обеспечить корректную поддержку той или иной СУБД при работе с классом {@link DatabaseStorage},
 * необходимо создать реализауию данного конструктора для конкретной СУБД
 *
 * @author RDS
 * @version 1
 * @since 1.0.0
 */
public interface DatabaseQueryBuilder {

	String queryForGetColumns(String tableName);

	String queryForCreateSpace(String tableName, String tableComments, List<Column> tableColumns);

	String queryForCreateSpaceIndex(String spaceName, List<String> propertyNames, String indexName, boolean unique);

	String queryForCreateSpaceJsonIndex(String spaceName, String propertyName, String indexName, boolean unique);

	String queryForDeleteSpace(String spaceName);

	default String queryForSelectRows(String tableName, List<Column> tableColumns) {
		return queryForSelectRows(tableName, tableColumns, null, null, null, 0, 0);
	}

	String queryForSelectRows(String tableName, List<Column> tableColumns, List<ElementsSort> sorts, List<ElementsSelectionCondition> selectionConditions, SelectionType selectionType, int offset, int limit);

	String queryForCountRows(String tableName, List<Column> tableColumns, List<ElementsSelectionCondition> selectionConditions, SelectionType selectionType);

	String queryForCreateRow(String tableName, List<String> columnNames);

	String queryForUpdateRow(String tableName, List<String> columnNames, List<ElementsSelectionCondition> selectionConditions, SelectionType selectionType);

	String queryForDeleteRow(String tableName, List<ElementsSelectionCondition> selectionConditions, SelectionType selectionType);

}
