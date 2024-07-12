package ru.rds.data.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.rds.data.database.common.AssertChecker;
import ru.rds.data.database.exceptions.CreateRowException;
import ru.rds.data.database.exceptions.DeleteRowException;
import ru.rds.data.database.exceptions.UpdateRowException;
import ru.rds.data.storage.*;

/**
 * Таблица - реализация {@link Space} при хранении данных в БД.
 * <p>
 * Объект данного класса правильнее всего получать следующим путем:
 * <p><code>
 * Table table = database.getSpace("my_table")
 * </code>
 * <p>
 * Крайне не рекомендуется создавать объекты данного класса вручную, т.к. при таком создании высока вероятность неправильно работы всех алгоритмов, которые используют данные, полученные при использовании правильным путем.
 *
 * @author RDS
 * @version 1
 * @since 1.0.0
 */
public class Table implements Space<Column, Row, TableSection> {

	private static final Logger logger = LoggerFactory.getLogger(Table.class);

	// Название
	private String               name;
	// Дополнительное описание
	private String               description;
	// Название каталога
	private String               catalogue;
	// Название схемы
	private String               schema;
	// Является ли эта Таблица системной, а не Пользовательской
	private boolean              system;
	// Список Колонок
	private List<Column>         columns;
	// Список Индексов
	private List<Index>          indexes;
	// Любая реализация DataSource ()
	private DataSource           dataSource;
	private DatabaseQueryBuilder queryBuilder;

	/**
	 * В случае ручного создания экземпляра класса рекомендуется ознакомиться с реализацией метода {@link DatabaseStorage#getSpace(String)}.
	 * Все переданные параметры будут использоваться при работе класса, эти данные не формируются самостоятельно - они всегда должны устанавливаться "извне".
	 *
	 * @param dataSource   тот же DataSource, что и при создании {@link DatabaseStorage} - обязательный аргумент
	 * @param queryBuilder реализация {@link DatabaseQueryBuilder} для конкретной СУБД - обязательный аргумент
	 * @param name         название Таблицы - обязательный параметр
	 * @param description  дополнительное описание Таблицы - необязательный аргумент
	 * @param catalogue    название каталога Таблицы - необязательный аргумент
	 * @param schema       название схемы Таблицы - необязательный аргумент
	 * @param system       системная или нет Таблица - необязательный аргумент
	 * @param columns      перечень Колонок Таблицы - обязательный аргумент
	 * @param indexes      перечень Индексов Таблицы - необязательный аргумент
	 */
	public Table(DataSource dataSource, DatabaseQueryBuilder queryBuilder, String name, String description, String catalogue, String schema, boolean system, List<Column> columns, List<Index> indexes) {
		this.dataSource = dataSource;
		this.queryBuilder = queryBuilder;
		this.name = name;
		this.description = description;
		this.catalogue = catalogue;
		this.schema = schema;
		this.system = system;
		this.columns = columns;
		this.indexes = indexes;

		AssertChecker.notNull(this.dataSource, "<DataSource> не должен быть равен <NULL>");
		AssertChecker.notNull(this.queryBuilder, "<DatabaseQueryBuilder> не должен быть равен <NULL>");
		AssertChecker.notNull(this.name, "<name> не должен быть равен <NULL>");
		AssertChecker.notNull(this.name, "<List<Column>> не должен быть равен <NULL>");
	}

	public Table(DataSource dataSource, DatabaseQueryBuilder queryBuilder, String name, List<Column> columns) {
		this(dataSource, queryBuilder, name, null, null, null, false, columns, Collections.emptyList());
	}

	public Table(DataSource dataSource, DatabaseQueryBuilder queryBuilder, String name, List<Column> columns, List<Index> indexes) {
		this(dataSource, queryBuilder, name, null, null, null, false, columns, indexes);
	}

	/**
	 * Получение {@link DataSource}, которое использует в работе данная Таблица
	 *
	 * @return
	 */
	public DataSource getDataSource() {
		return dataSource;
	}

	/**
	 * Получение {@link DatabaseQueryBuilder}, которое использует в своей работе данная Таблица
	 *
	 * @return
	 */
	public DatabaseQueryBuilder getQueryBuilder() {
		return queryBuilder;
	}

	/**
	 * Установление имени Таблицы
	 *
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Получение имени Таблицы
	 *
	 * @return
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * Получение дополнительного описания Таблицы
	 *
	 * @return
	 */
	@Override
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Получение каталога СУБД Таблицы
	 *
	 * @return
	 */
	public String getCatalogue() {
		return catalogue;
	}

	/**
	 * Установление каталога СУБД Таблицы
	 *
	 * @param catalogue
	 */
	public void setCatalogue(String catalogue) {
		this.catalogue = catalogue;
	}

	/**
	 * Получение схемы СУБД Таблицы
	 *
	 * @return
	 */
	public String getSchema() {
		return schema;
	}

	/**
	 * Установление схемы СУБД Таблицы
	 *
	 * @param schema
	 */
	public void setSchema(String schema) {
		this.schema = schema;
	}

	/**
	 * Устанавливает признак того, является ли данная Таблица системной
	 *
	 * @param system
	 */
	public void setSystem(boolean system) {
		this.system = system;
	}

	/**
	 * Является ли данная Таблица системной, а не созданной Пользователем
	 *
	 * @return
	 */
	@Override
	public boolean isSystem() {
		return system;
	}

	/**
	 * Получение списка Колонок (аналог метода {@link #getProperties()})
	 *
	 * @return
	 */
	public List<Column> getColumns() {
		return columns;
	}

	/**
	 * Устанавливает перечень Колонок. Эквивалентен вызову метода {@link #setProperties(List)}
	 *
	 * @param columns
	 */
	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	/**
	 * Устанавливает перечень Колонок
	 *
	 * @param columns
	 */
	public void setProperties(List<Column> columns) {
		setColumns(columns);
	}

	/**
	 * Получение списка Колонок
	 *
	 * @return
	 */
	@Override
	public List<Column> getProperties() {
		return getColumns();
	}

	/**
	 * Получение списка Колонок, являющимися primaryKey
	 *
	 * @return
	 */
	public List<Column> getIdColumns() {
		return getColumns().stream()
		                   .filter(Column::isPrimaryKey)
		                   .collect(Collectors.toList());
	}

	/**
	 * Получение списка Индексов
	 *
	 * @return
	 */
	public List<Index> getIndexes() {
		return indexes;
	}

	/**
	 * Установление списка Индексов
	 *
	 * @param indexes
	 */
	public void setIndexes(List<Index> indexes) {
		this.indexes = indexes;
	}

	/* Формирование Строки из предварительно полученного ResultSet */
	private Row mapRow(ResultSet resultSet) {
		Row row = new Row(getProperties());
		getProperties().forEach(column -> {
			try {
				// Получение значения Колонки
				Object value = resultSet.getObject(column.getName());
				row.setValue(column.getName(), value);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		});
		return row;
	}

	/**
	 * Получение Строк в соответствии с критериями. Аналог метода {@link #getElements(List, List, SelectionType, int, int)}
	 *
	 * @param elementsSorts       перечень сортировок, в соответствии с которыми нужно расположить Строки внутри списка
	 * @param selectionConditions перечень критерий для получения Строк (если критериев нет - будут получены все строки)
	 * @param selectionType       способ комбинации критериев для получения Строк (если параметр будет равен NULL - то будет использовано значение по-умолчанию {@link SelectionType#AND})
	 * @param offset              количество Строк, которые нужны пропустить (считая от начала) (будет проигнорировано, если значение limit будет <= 0)
	 * @param limit               максимальное количество Строк, которые стоит предоставить в списке (если значение <= 0 - ограничения limit и offset будут проигнорированы)
	 * @return никогда не возвращает NULL
	 */
	public List<Row> getRows(List<ElementsSort> elementsSorts, List<ElementsSelectionCondition> selectionConditions, SelectionType selectionType, int offset, int limit) {
		return getElements(elementsSorts, selectionConditions, selectionType, offset, limit);
	}

	/**
	 * Получение Строк в соответствии с критериями
	 *
	 * @param elementsSorts       перечень сортировок, в соответствии с которыми нужно расположить Строки внутри списка
	 * @param selectionConditions перечень критерий для получения Строк (если критериев нет - будут получены все строки)
	 * @param selectionType       способ комбинации критериев для получения Строк (если параметр будет равен NULL - то будет использовано значение по-умолчанию {@link SelectionType#AND})
	 * @param offset              количество Строк, которые нужны пропустить (считая от начала) (будет проигнорировано, если значение limit будет <= 0)
	 * @param limit               максимальное количество Строк, которые стоит предоставить в списке (если значение <= 0 - ограничения limit и offset будут проигнорированы)
	 * @return никогда не возвращает NULL
	 */
	@Override
	public List<Row> getElements(List<ElementsSort> elementsSorts, List<ElementsSelectionCondition> selectionConditions, SelectionType selectionType, int offset, int limit) {
		String query = getQueryBuilder().queryForSelectRows(getName(), getProperties(), elementsSorts, selectionConditions, selectionType, offset, limit);
		logger.trace("SQL-query: " + query);
		if (query != null) {
			ArrayList<Row> rows = new ArrayList<>();
			try (Connection connection = getDataSource().getConnection(); Statement statement = connection.createStatement();) {
				ResultSet resultSet = statement.executeQuery(query);
				while (resultSet.next()) {
					rows.add(mapRow(resultSet));
				}
				resultSet.close();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			return rows;
		}
		return Collections.emptyList();
	}

	/**
	 * Получение Секции Строк
	 *
	 * @param sectionable критерии для получения Секции Строк
	 * @return
	 */
	@Override
	public TableSection getSection(Sectionable sectionable) {
		List<Row> rows = getElements(sectionable.getSort(), sectionable.getElementsSelectionConditions(), sectionable.getSelectionType(), sectionable.getSectionNumber() * sectionable.getSectionSize(),
		                             sectionable.getSectionSize());
		long totalRowsCount = getElementsCount(sectionable.getElementsSelectionConditions(), sectionable.getSelectionType());

		return TableSection.of(sectionable, totalRowsCount, rows);
	}

	/**
	 * Плучение количества Строк в соответствии с критериями
	 *
	 * @param selectionConditions перечень условий для отбора Строк ({@link Row})
	 * @param selectionType       способ комбинации условий отбора ({@link SelectionType#AND} или {@link SelectionType#OR})
	 * @return
	 */
	@Override
	public long getElementsCount(List<ElementsSelectionCondition> selectionConditions, SelectionType selectionType) {
		String query = getQueryBuilder().queryForCountRows(getName(), getIdColumns(), selectionConditions, selectionType);
		logger.trace("SQL-query: " + query);
		if (query != null) {
			long count = 0;
			try (Connection connection = getDataSource().getConnection(); Statement statement = connection.createStatement();) {
				ResultSet resultSet = statement.executeQuery(query);
				if (resultSet.next()) {
					count = resultSet.getLong(1);
				}
				resultSet.close();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			return count;
		}
		return 0;
	}

	/**
	 * Создание новой Строки ({@link Row})
	 *
	 * @param row
	 * @return
	 * @throws CreateRowException
	 */
	@Override
	public Row createElement(Row row) {
		List<String> columnNames = getProperties().stream()
		                                          .map(Column::getName)
		                                          .collect(Collectors.toList());
		String query = getQueryBuilder().queryForCreateRow(getName(), columnNames);
		logger.trace("SQL-query: " + query);
		if (query != null) {
			try (Connection connection = getDataSource().getConnection()) {
				PreparedStatement preparedStatement = connection.prepareStatement(query);
				for (int i = 0; i < columnNames.size(); i++) {
					String columnName = columnNames.get(i);
					Optional<Object> columnValue = row.getValue(columnName);
					preparedStatement.setObject(i + 1, columnValue.orElseGet(() -> null));
				}
				preparedStatement.executeUpdate();
				preparedStatement.close();
				return row;
			} catch (CreateRowException e) {
				logger.error(e.getMessage(), e);
				throw e;
			} catch (Exception e) {
				CreateRowException error = new CreateRowException(String.format("Не удалось создать новую строку в таблице <%s>: %s", getName(), e.getMessage()), e);
				logger.error(e.getMessage(), e);
				throw error;
			}
		} else {
			throw new CreateRowException(String.format("Не удалось создать новую строку в таблице <%s>: запрос равен <NULL>", getName()));
		}
	}

	/**
	 * Обновление значений Строки ({@link Row})
	 *
	 * @param row                 строка, содержащая данные для обновления
	 * @param selectionConditions перечень условий для отбора Строк ({@link Row}), в которых нужно будет обновить данные
	 * @param selectionType       тип применения условий отбора ({@link SelectionType#AND} или {@link SelectionType#OR})
	 * @return
	 * @throws UpdateRowException
	 */
	@Override
	public Row updateElements(Row row, List<ElementsSelectionCondition> selectionConditions, SelectionType selectionType) {
		List<String> columnNames = new ArrayList<>(row.getValues().keySet());
		String query = getQueryBuilder().queryForUpdateRow(getName(), columnNames, selectionConditions, selectionType);
		logger.trace("SQL-query: " + query);
		if (query != null) {
			try (Connection connection = getDataSource().getConnection()) {
				PreparedStatement preparedStatement = connection.prepareStatement(query);
				for (int i = 0; i < columnNames.size(); i++) {
					String columnName = columnNames.get(i);
					Optional<Object> columnValue = row.getValue(columnName);
					preparedStatement.setObject(i + 1, columnValue.orElseGet(() -> null));
				}
				preparedStatement.executeUpdate();
				preparedStatement.close();
				connection.close();
				return row;
			} catch (UpdateRowException e) {
				logger.error(e.getMessage(), e);
				throw e;
			} catch (Exception e) {
				UpdateRowException error = new UpdateRowException(String.format("Не удалось обновить строку в таблице <%s>: %s", getName(), e.getMessage()), e);
				logger.error(e.getMessage(), e);
				throw error;
			}
		} else {
			UpdateRowException error = new UpdateRowException(String.format("Не удалось обновить строку в таблице <%s>: запрос равен <NULL>", getName()));
			logger.error(error.getMessage());
			throw error;
		}
	}

	/**
	 * Удаление Строк в соответствии с критериями
	 *
	 * @param selectionConditions перечень условий для отбора Строк ({@link Row}), которые нужно будет удалить
	 * @param selectionType       тип применения условий отбора ({@link SelectionType#AND} или {@link SelectionType#OR})
	 */
	@Override
	public void deleteElements(List<ElementsSelectionCondition> selectionConditions, SelectionType selectionType) {
		String query = getQueryBuilder().queryForDeleteRow(getName(), selectionConditions, selectionType);
		logger.trace("SQL-query: " + query);
		if (query != null) {
			try (Connection connection = getDataSource().getConnection(); Statement statement = connection.createStatement()) {
				statement.execute(query);
			} catch (UpdateRowException e) {
				logger.error(e.getMessage(), e);
				throw e;
			} catch (Exception e) {
				DeleteRowException error = new DeleteRowException(String.format("Не удалось удалить строки в таблице <%s>: %s", getName(), e.getMessage()), e);
				logger.error(e.getMessage(), e);
				throw error;
			}
		} else {
			DeleteRowException error = new DeleteRowException(String.format("Не удалось удалить строки в таблице <%s>: запрос равен <NULL>", getName()));
			logger.error(error.getMessage());
			throw error;
		}
	}

}
