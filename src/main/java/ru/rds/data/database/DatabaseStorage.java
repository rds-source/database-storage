package ru.rds.data.database;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.rds.data.database.common.AssertChecker;
import ru.rds.data.database.common.Pair;
import ru.rds.data.database.exceptions.CreateTableException;
import ru.rds.data.database.exceptions.DeleteTableException;
import ru.rds.data.storage.Storage;

/**
 * База Данных - реализация {@link Storage} при хранении данных в Базе Данных (далее - БД).
 *
 * @author RDS
 * @version 1
 * @since 1.0.0
 */
public class DatabaseStorage implements Storage<Table, Column> {

	private static final Logger logger = LoggerFactory.getLogger(DatabaseStorage.class);

	// Любая нужная реализация DataSource
	private DataSource           dataSource;
	// Нужен для генерации всех SQL-запросов, чтобы не привязываться к какой-либо конкретной СУБД
	private DatabaseQueryBuilder queryBuilder;

	/**
	 * При создании экземпляра класса стоит правильно подобрать нужную реализацию {@link DataSource} и соответствующую реализауию {@link DatabaseQueryBuilder} применительно к конкретной СУБД
	 *
	 * @param dataSource
	 * @param queryBuilder
	 */
	public DatabaseStorage(DataSource dataSource, DatabaseQueryBuilder queryBuilder) {
		this.dataSource = dataSource;
		this.queryBuilder = queryBuilder;
		AssertChecker.notNull(this.dataSource, "<DataSource> не должен быть равен <NULL>");
		AssertChecker.notNull(this.dataSource, "<DatabaseQueryBuilder> не должен быть равен <NULL>");
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public DatabaseQueryBuilder getQueryBuilder() {
		return queryBuilder;
	}

	/**
	 * Получение перечня всех Таблиц
	 *
	 * @return
	 */
	@Override
	public List<Table> getSpaces() {
		ArrayList<Table> tables = new ArrayList<>();
		try {
			Connection connection = getDataSource().getConnection();
			DatabaseMetaData databaseMetaData = connection.getMetaData();
			ResultSet resultSet = databaseMetaData.getTables(null, null, "%s", null);
			while (resultSet.next()) {
				Table table = mapTable(resultSet, connection);
				tables.add(table);
			}
			if (!connection.isClosed()) {
				connection.close();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return tables;
	}

	/**
	 * Получение Таблицы по её имени
	 *
	 * @param name имя Таблицы ({@link Table})
	 * @return
	 */
	@Override
	public Optional<Table> getSpace(String name) {
		AssertChecker.notNull(name, "Название Таблицы не должно быть <NULL>");
		try {
			Connection connection = getDataSource().getConnection();
			DatabaseMetaData databaseMetaData = connection.getMetaData();
			ResultSet resultSet = databaseMetaData.getTables(null, null, name, null);
			while (resultSet.next()) {
				Table table = mapTable(resultSet, connection);
				return Optional.ofNullable(table);
			}
			resultSet.close();
			connection.close();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}

	/*private ResultSet findTables(String tableNamePattern) throws SQLException
	{
		Connection connection = getDataSource().getConnection();
		DatabaseMetaData databaseMetaData = connection.getMetaData();
		ResultSet resultSet = databaseMetaData.getTables(null, null, tableNamePattern, null);
		return resultSet;
	}*/

	/*
	Формирование объекта {@link Table} на основе данных, полученных из {@link ResultSet},
	который в свою очередь быд получен путем вызова метода {@link DatabaseMetaData#getTables(String, String, String, String[])}
	 */
	private Table mapTable(ResultSet resultSet, Connection connection) {
		try {
			String catalogue = resultSet.getString(1);
			String schema = resultSet.getString(2);
			String name = resultSet.getString(3);
			String type = resultSet.getString(4);
			Pair<List<Column>, List<Index>> pair = findColumnsAndIndexes(name, connection);
			List<Column> columns = pair.getFirst();
			List<Index> indexes = pair.getSecond();
			boolean isSystem = type != null && type.contains("SYSTEM");

			return new Table(getDataSource(), getQueryBuilder(), name, null, catalogue, schema, isSystem, columns, indexes);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * Получение перечня Столбцов для указанной Таблицы
	 *
	 * @param name имя Таблицы ({@link Table})
	 * @return никогда не возвращает NULL
	 */
	@Override
	public List<Column> getSpaceProperties(String name) {
		AssertChecker.notNull(name, "Название Таблицы не должно быть <NULL>");
		return findColumnsAndIndexes(name, null).getFirst();
	}

	/*
	Поиск столбцов и индексов Таблицы
	(сделано в рамках одного метода, чтобы не "гонять" лишний раз одни и те же данные)
	 */
	private Pair<List<Column>, List<Index>> findColumnsAndIndexes(String tableName, Connection connection) {
		Map<String, Column> columns = new HashMap<>();
		Map<String, Index> indexes = new HashMap<>();
		try {
			if (connection == null) {
				connection = dataSource.getConnection();
			}
			DatabaseMetaData databaseMetaData = connection.getMetaData();
			/*
			Поиск всех столбцов
			(старый вариант, рабочий, но в нем нельзя получить некоторые параметры, которые можно получить,
			использовав другой вариант, который реализован сразу после этого)
			 */
			/*ResultSet resultSet = databaseMetaData.getColumns(null, null, tableName, null);
			while (resultSet.next()) {
				Column column = mapColumn(resultSet);
				if (column != null) {
					columns.put(column.getName(), column);
				}
			}
			resultSet.close();*/
			Statement statement = connection.createStatement();
			/*
			По факту выполняем пустой SELECT, который даст нам набор колонок + их типовая составляющая для Java (именно это отсутствует,
			если использовать databaseMetaData.getColumns(null, null, tableName, null))
			 */
			ResultSet resultSet = statement.executeQuery(getQueryBuilder().queryForGetColumns(tableName));
			mapColumns(resultSet).forEach(column -> columns.put(column.getName(), column));
			resultSet.close();
			statement.close();
			// Поиск id-столбцов
			resultSet = databaseMetaData.getPrimaryKeys(null, null, tableName);
			while (resultSet.next()) {
				String columnName = resultSet.getString("COLUMN_NAME");
				Column column = columns.get(columnName);
				if (column != null) {
					column.setPrimaryKey(true);
				}
			}
			resultSet.close();
			// Поиск индексов
			resultSet = databaseMetaData.getIndexInfo(null, null, tableName, true, true);
			while (resultSet.next()) {
				String indexName = resultSet.getString("INDEX_NAME");
				if (indexName != null) {
					String columnName = resultSet.getString("COLUMN_NAME");
					if (columnName != null) {
						// Ищем такой столбец среди наших (а он должен быть там)
						Column column = columns.get(columnName);
						if (column != null) {
							// Заодно проставляем признак уникальности для него
							column.setUnique(true);
							// Собираем индекс (при этом помним, что индекс с таким именем уже может быть)
							Index index = indexes.get(indexName);
							if (index == null) {
								index = new Index();
								index.setName(indexName);
								index.setUnique(true);
							}
							index.addColumn(column);
						}
					}
				}
			}
			resultSet.close();
			if (!connection.isClosed()) {
				connection.close();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return new Pair<>(new ArrayList<>(columns.values()), new ArrayList<>(indexes.values()));
	}

	/* Формирование списка Колонок из предварительно полученного ResultSet */
	private List<Column> mapColumns(ResultSet resultSet) {
		List<Column> columns = new ArrayList<>();
		try {
			ResultSetMetaData metaData = resultSet.getMetaData();
			if (metaData != null) {
				int columnCount = metaData.getColumnCount();
				for (int index = 1; index <= columnCount; index++) {
					Column column = new Column();
					column.setOrdinalPosition(index);
					column.setName(metaData.getColumnName(index));
					column.setLabel(metaData.getColumnLabel(index));
					column.setType(ColumnType.of(metaData.getColumnType(index), metaData.getColumnTypeName(index))
					                         .orElse(ColumnType.VARCHAR));
					column.setClassName(metaData.getColumnClassName(index));
					column.setSize(metaData.getColumnDisplaySize(index));
					column.setAutoincrement(metaData.isAutoIncrement(index));
					column.setCurrency(metaData.isCurrency(index));
					column.setCaseSensitive(metaData.isCaseSensitive(index));
					column.setNullable(metaData.isNullable(index) == 1);
					column.setSearchable(metaData.isSearchable(index));
					column.setDefinitelyWritable(metaData.isDefinitelyWritable(index));
					column.setReadOnly(metaData.isReadOnly(index));
					column.setSigned(metaData.isSigned(index));
					column.setWritable(metaData.isWritable(index));

					columns.add(column);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return columns;
	}

	/*private Column mapColumn(ResultSet resultSet)
	{
		try {
			String name = resultSet.getString(4);
			int dataType = resultSet.getInt(5);
			String typeName = resultSet.getString(6);
			int size = resultSet.getInt(7);
			int nullable = resultSet.getInt(11);
			String remarks = resultSet.getString(12);
			int sqlDataType = resultSet.getInt(14);
			int ordinalPosition = resultSet.getInt(17);

			ColumnType columnType = ColumnType.of(typeName).orElseGet(() -> null);

			Column column = new Column();
			column.setName(name);
			column.setDataType(dataType);
			column.setTypeName(typeName);
			column.setSize(size);
			column.setNullable(nullable == 1);
			column.setRemarks(remarks);
			column.setSqlDataType(sqlDataType);
			column.setOrdinalPosition(ordinalPosition);
			column.setType(columnType);
			return column;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}*/

	/**
	 * Создание Таблицы
	 *
	 * @param tableName имя Таблицы ({@link Table})
	 * @param comments  дополнительное описание Таблицы ({@link Table})
	 * @param columns   список Колонок ({@link Column}) таблицы ({@link Table})
	 * @throws CreateTableException возникает в случае, если по каким-то причинам создать Таблицу не удалось
	 */
	@Override
	public void createSpace(String tableName, String comments, List<Column> columns) {
		createTable(tableName, comments, columns, createIndexes(tableName, columns));
	}

	/*
	Формирование Индексов на основе списка Колонок.
	Формируются только Индексы, которые нужны для Колонок с уникальными значениями и значениями PrimaryKey.
	Формирование каки-либо Пользовательских индексов пока-что не предусмотрено (пока незачем)
	*/
	private List<Index> createIndexes(String tableName, List<Column> columns) {
		// Сперва собираем индексы для уникальных колонок
		/*List<TableIndex> indexes = properties.stream()
											 .filter(TableColumn::isUnique)
											 .map(tableColumn -> TableIndex.builder()
																		   .name("index_" + spaceName + "_" + tableColumn.getName())
																		   .columns(List.of(tableColumn))
																		   .unique(true)
																		   .build())
											 .collect(Collectors.toList());
		// Далее собираем индексы для колонок типа JSON
		indexes.addAll(properties.stream()
								 .filter(tableColumn -> tableColumn.getType() != null)
								 .filter(tableColumn -> tableColumn.getType()
																   .equals(ColumnType.JSONB))
								 .map(tableColumn -> TableIndex.builder()
															   .name("index_js_" + spaceName + "_" + tableColumn.getName())
															   .onJson(true)
															   .columns(List.of(tableColumn))
															   .build())
								 .collect(Collectors.toList()));*/
		return columns.stream()
		              .filter(tableColumn -> tableColumn.isUnique() || ColumnType.JSONB.equals(tableColumn.getType()))
		              .map(tableColumn -> createTableIndex(tableName, tableColumn))
		              .collect(Collectors.toList());
	}

	protected Index createTableIndex(String tableName, Column column) {
		boolean isOnJson = column.getType() != null && column.getType().equals(ColumnType.JSONB);
		String indexName = "index_";
		if (isOnJson) {
			indexName += "js_";
		}
		indexName += tableName + "_" + column.getName();

		Index index = new Index();
		index.setName(indexName);
		index.setOnJson(isOnJson);
		index.setColumns(Collections.singletonList(column));
		index.setUnique(column.isUnique());
		return index;
	}

	/**
	 * Создание новой Таблицы ({@link Table})
	 *
	 * @param table объект, описывающий Таблицу
	 * @throws CreateTableException возникает в случае, если по каким-то причинам создать Таблицу не удалось
	 */
	@Override
	public void createSpace(Table table) {
		AssertChecker.notNull(table, "Экземпляр <Table> не должен быть <NULL>");
		createTable(table.getName(), table.getDescription(), table.getColumns(), table.getIndexes());
	}

	/* Создание новой Таблицы */
	protected void createTable(String tableName, String comments, List<Column> columns, List<Index> indexes) throws CreateTableException {
		AssertChecker.notNull(tableName, "Название Таблицы не должно быть <NULL>");

		String query = getQueryBuilder().queryForCreateSpace(tableName, comments, columns);
		logger.trace("SQL-запрос: " + query);
		if (query != null) {
			// Пробуем создать Таблицу
			try (Connection connection = getDataSource().getConnection(); Statement statement = connection.createStatement();) {
				statement.execute(query);
			} catch (Exception e) {
				CreateTableException exception = new CreateTableException(String.format("Не удалось создать Таблицу <%s>", tableName), e);
				logger.error(exception.getMessage(), exception);
				throw exception;
			}
			// Далее - создаем индексы для Таблицы
			if (indexes != null && !indexes.isEmpty()) {
				for (Index index : indexes) {
					String indexQuery = null;
					if (index.isOnJson()) {
						// Индексы для JSON
						Optional<Column> indexColumn = index.getColumns()
						                                    .stream()
						                                    .findFirst();
						if (indexColumn.isPresent()) {
							indexQuery = getQueryBuilder().queryForCreateSpaceJsonIndex(tableName, indexColumn.get().getName(), index.getName(), index.isUnique());
						}
					} else {
						// Обычные индексы
						List<String> columnNames = index.getColumns()
						                                .stream()
						                                .map(Column::getName)
						                                .collect(Collectors.toList());
						indexQuery = getQueryBuilder().queryForCreateSpaceIndex(tableName, columnNames, index.getName(), index.isUnique());
					}
					logger.trace("SQL-запрос: " + indexQuery);
					if (indexQuery != null) {
						try (Connection connection = getDataSource().getConnection(); Statement statement = connection.createStatement();) {
							statement.execute(indexQuery);
						} catch (Exception e) {
							CreateTableException exception = new CreateTableException(String.format("Не удалось создать индекс Таблицы <%s>", tableName), e);
							logger.error(exception.getMessage(), exception);
							throw exception;
						}
					} else {
						logger.warn(String.format("Создание индексов для Таблицы <%s> пропущено, т.к. SQL-запрос равен <NULL>", tableName));
					}
				}
			} else {
				logger.warn(String.format("Индексы для Таблицы <%s> отсутствуют", tableName));
			}
		} else {
			CreateTableException exception = new CreateTableException(String.format("Не удалось создать Таблицу <%s>: SQL-запрос равен <NULL> или пустой", tableName));
			logger.error(exception.getMessage(), exception);
			throw exception;
		}
	}

	/**
	 * Удаление Таблицы
	 *
	 * @param tableName имя Таблицы ({@link Table})
	 * @throws DeleteTableException возникает в случае, если по каким-то причинам удалить Таблицу не удалось
	 */
	@Override
	public void deleteSpace(String tableName) {
		if (tableName != null && !tableName.isEmpty()) {
			String query = getQueryBuilder().queryForDeleteSpace(tableName);
			logger.trace("SQL-запрос: " + query);
			if (query != null) {
				try (Connection connection = getDataSource().getConnection(); Statement statement = connection.createStatement();) {
					statement.execute(query);
				} catch (Exception e) {
					DeleteTableException exception = new DeleteTableException(String.format("Не удалось удалить Таблицу <%s>", tableName), e);
					logger.error(exception.getMessage(), exception);
					throw exception;
				}
			} else {
				DeleteTableException exception = new DeleteTableException(String.format("Не удалось удалить Таблицу <%s>: SQL-запрос равен <NULL>", tableName));
				logger.error(exception.getMessage(), exception);
				throw exception;
			}
		}
	}

}
