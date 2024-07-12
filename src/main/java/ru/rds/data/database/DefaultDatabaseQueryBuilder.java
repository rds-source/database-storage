package ru.rds.data.database;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.rds.data.database.exceptions.CreateTableException;
import ru.rds.data.storage.ElementsSelectionCondition;
import ru.rds.data.storage.ElementsSort;
import ru.rds.data.storage.SelectionType;

/**
 * Основная реализация {@link DatabaseQueryBuilder}, которая подходит для большинства реляционных СУБД
 *
 * @author RDS
 * @version 1
 * @since 1.0.0
 */
public class DefaultDatabaseQueryBuilder implements DatabaseQueryBuilder {

	private static final Logger logger = LoggerFactory.getLogger(DefaultDatabaseQueryBuilder.class);

	// Формирование строки вида <SELECT column1, column2, ... FROM tableName>
	private String buildSelectQueryPart(String tableName, List<Column> tableColumns) {
		if (tableName != null) {
			StringBuffer sb = new StringBuffer("SELECT");
			if (tableColumns != null && !tableColumns.isEmpty()) {
				Optional<String> columnNames = tableColumns.stream()
				                                           .map(Column::getName)
				                                           .reduce((s, s2) -> s + ", " + s2);
				columnNames.ifPresent(s -> sb.append(" ").append(s));
			} else {
				sb.append("*");
			}
			sb.append(" FROM ").append(tableName);
			return sb.toString();
		} else {
			logger.warn("Отсутствует название Таблицы");
			return null;
		}
	}

	/*
	Формирование строки вида <SELECT COUNT(column1, column2, ...) FROM tableName>.
	Если список столбцов пуст или равен <NULL>, то строка будет вида <SELECT COUNT(*) FROM tableName>
	 */
	private String buildCountQueryPart(String tableName, List<Column> tableColumns) {
		if (tableName != null) {
			StringBuffer sb = new StringBuffer("SELECT COUNT");
			if (tableColumns != null && !tableColumns.isEmpty()) {
				Optional<String> columnNames = tableColumns.stream()
				                                           .map(Column::getName)
				                                           .reduce((s, s2) -> s + ", " + s2);
				columnNames.ifPresent(s -> sb.append(" (").append(s).append(")"));
			} else {
				sb.append("(*)");
			}
			sb.append(" FROM ").append(tableName);
			return sb.toString();
		} else {
			logger.warn("Отсутствует название Таблицы");
			return null;
		}
	}

	/*
	Формирование строки вида <LIMIT limit OFFSET offset>
	Если limit <= 0, то будет возвращен <NULL>.
	Если offset <=0, то строка будет иметь вид <LIMIT limit>
	 */
	private String buildLimitsQueryPart(int offset, int limit) {
		if (limit > 0) {
			String queryPart = "LIMIT " + limit;
			if (offset > 0) {
				queryPart = queryPart + " OFFSET " + offset;
			}
			return queryPart;
		}
		return null;
	}

	/*
	Формирование строки вида <ORDER BY columnName direction, ...>
	 */
	private String buildSortQueryPart(List<ElementsSort> sorts) {
		if (sorts != null && !sorts.isEmpty()) {
			Optional<String> sortValue = sorts.stream()
			                                  .map(elementsSort -> elementsSort.getSpacePropertyName() + " " + elementsSort.getDirection().toString())
			                                  .reduce((s, s2) -> s + ", " + s2);
			return "ORDER BY " + sortValue.get();
		}
		return null;
	}

	// Формирование строки условий <WHERE ...>
	private String buildSelectionConditionExpression(List<ElementsSelectionCondition> selectionConditions, SelectionType selectionType) {
		if (selectionConditions != null && !selectionConditions.isEmpty()) {
			if (selectionType == null) {
				selectionType = SelectionType.AND;
			}
			String selectionTypeString = selectionType.toString();
			Optional<String> selectionExpression = selectionConditions.stream()
			                                                          .map(selectionCondition -> {
				                                                          StringBuffer sb = new StringBuffer();
				                                                          sb.append(selectionCondition.getSpacePropertyName());

				                                                          Object value = selectionCondition.getSpacePropertyValue();
				                                                          String valueString = String.valueOf(value);
				                                                          if (!(value instanceof Number)) {
					                                                          valueString = "'" + valueString + "'";
				                                                          }
				                                                          switch (selectionCondition.getSelectionConditionExpression()) {
					                                                          case EQUAL:
						                                                          sb.append(" = ").append(valueString);
						                                                          break;
					                                                          case NOT_EQUAL:
						                                                          sb.append(" != ").append(valueString);
						                                                          break;
					                                                          case LIKE:
						                                                          sb.append(" LIKE '%%").append(value).append("%s'");
						                                                          break;
					                                                          case NOT_LIKE:
						                                                          sb.append(" NOT LIKE '%%").append(value).append("%s'");
						                                                          break;
					                                                          case IN:
						                                                          sb.append(" IN (").append(valueString).append(")");
						                                                          break;
					                                                          case GREATER_THAN:
						                                                          sb.append(" > ").append(valueString);
						                                                          break;
					                                                          case GREATER_THAN_OR_EQUAL:
						                                                          sb.append(" >= ").append(valueString);
						                                                          break;
					                                                          case LESS_THEN:
						                                                          sb.append(" < ").append(valueString);
						                                                          break;
					                                                          case LESS_THEN_OR_EQUAL:
						                                                          sb.append(" <= ").append(valueString);
						                                                          break;
				                                                          }
				                                                          return sb.toString();
			                                                          })
			                                                          .reduce((s, s2) -> s + selectionTypeString + s2);
			return "WHERE " + selectionExpression.get();
		}
		return null;
	}

	@Override
	public String queryForGetColumns(String tableName) {
		if (tableName != null && !tableName.isEmpty()) {
			return "SELECT * FROM " + tableName + " LIMIT 0";
		} else {
			logger.error("Отсутствует название Таблицы");
		}
		return null;
	}

	@Override
	public String queryForCreateSpace(String tableName, String tableComments, List<Column> tableColumns) {
		if (tableName != null && !tableName.isEmpty()) {
			StringBuilder query = new StringBuilder();
			query.append("CREATE TABLE IF NOT EXISTS ");
			query.append(tableName);
			if (tableColumns != null && !tableColumns.isEmpty()) {
				query.append(" (");
				// Columns
				for (int pos = 0; pos < tableColumns.size(); pos++) {
					Column property = tableColumns.get(pos);
					String propString = mapColumnToCreationString(property);
					if (propString != null) {
						if (pos != 0) {
							query.append(", ");
						}
						query.append(propString);
					}
				}
				// Keys
				List<Column> primaryKeyProperties = tableColumns.stream()
				                                                .filter(Column::isPrimaryKey)
				                                                .collect(Collectors.toList());
				if (!primaryKeyProperties.isEmpty()) {
					query.append(", PRIMARY KEY (");
					for (int pos = 0; pos < primaryKeyProperties.size(); pos++) {
						if (pos != 0) {
							query.append(", ");
						}
						query.append(primaryKeyProperties.get(pos)
						                                 .getName());
					}
					query.append(")");
				}

				query.append(")");
			}
			// Комментарии для таблицы
			if (tableComments != null) {
				query.append("; ");
				query.append("COMMENT ON TABLE ");
				query.append(tableName);
				query.append(" is '");
				query.append(tableComments);
				query.append("'");
			}
			// Комментарии для столбцов
			if (tableColumns != null && !tableColumns.isEmpty()) {
				query.append("; ");
				tableColumns.forEach(tableColumn -> {
					if (tableColumn.getDescription() != null) {
						query.append("COMMENT ON COLUMN ");
						query.append(tableName);
						query.append(".");
						query.append(tableColumn.getName());
						query.append(" is '");
						query.append(tableColumn.getDescription());
						query.append("';");
					}
				});
			}

			return query.toString();
		} else {
			logger.error("Отсутствует название Таблицы");
		}
		return null;
	}

	private String mapColumnToCreationString(Column column) {
		if (column != null) {
			if (column.getName() != null) {
				StringBuilder propertyBuilder = new StringBuilder();
				propertyBuilder.append(column.getName());
				// Определяем тип
				ColumnType columnType = column.getType();
				if (columnType == null) {
					CreateTableException exception = new CreateTableException(String.format("Тип Столба <%s> равен <NULL>", column.getName()));
					logger.error(exception.getMessage(), exception);
					return null;
				} else {
					// Определяем тип SQL
					/*Optional<String> sqlType = ColumnType.sqlTypeOf(columnType.getTypeCode());*/
					String sqlType = columnType.getTypeName();
					if (sqlType == null || sqlType.isEmpty()) {
						CreateTableException exception = new CreateTableException(String.format("SQL-тип Столбца <%s> равен <NULL>", column.getName()));
						logger.error(exception.getMessage(), exception);
						return null;
					} else {
						propertyBuilder.append(" ");
						propertyBuilder.append(sqlType);
						// Проверяем, обладает ли размером
						if (columnType.isSizeable()) {
							int size = columnType.getDefaultSize();
							if (column.getSize() > 0) {
								size = column.getSize();
							}
							propertyBuilder.append("(");
							propertyBuilder.append(String.valueOf(size));
							propertyBuilder.append(")");
						}
						// Проверяем, может ли быть NULL
						if (!column.isNullable()) {
							propertyBuilder.append(" NOT NULL");
						}
						// Проверяем на уникальность
						if (column.isUnique()) {
							propertyBuilder.append(" UNIQUE");
						}
					}
				}
				// ============================
				/*if (property.getType() != 0) {
					// Проверяем, нужно ли заменить тип данных
					int propertyType = property.getType();
					if (SQL_TYPES_TO_REPLACE.containsKey(propertyType)) {
						propertyType = SQL_TYPES_TO_REPLACE.get(propertyType);
					}
					// Идем дальше
					String type = SpacePropertyTypes.getName(propertyType);
					if (type != null) {
						propertyBuilder.append(" ");
						propertyBuilder.append(type);
						if (PROPERTIES_LIST_WITH_SIZE.contains(property.getType()) && property.getSize() > 0) {
							propertyBuilder.append("(");
							propertyBuilder.append(String.valueOf(property.getSize()));
							propertyBuilder.append(")");
						}
					}
					if (property.getType() == SpacePropertyTypes.BIT && property.getSize() == 1) {
						propertyBuilder.append(" ");
						propertyBuilder.append(SpacePropertyTypes.getName(SpacePropertyTypes.BOOLEAN));
					} else {
						String type = SpacePropertyTypes.getName(property.getType());
						if (type != null) {
							propertyBuilder.append(" ");
							propertyBuilder.append(type);
							if (PROPERTIES_LIST_WITH_SIZE.contains(property.getType()) && property.getSize() > 0) {
								propertyBuilder.append("(");
								propertyBuilder.append(String.valueOf(property.getSize()));
								propertyBuilder.append(")");
							}
						}
					}
				}
				// Почему-то это требование не соблюдается в оригнальной БД
				if (!property.isNullable()) {
					propertyBuilder.append(" NOT NULL");
				}*/
				return propertyBuilder.toString();
			}
		}
		return null;
	}

	@Override
	public String queryForCreateSpaceIndex(String tableName, List<String> columnNames, String indexName, boolean unique) {
		if (tableName != null && !tableName.isEmpty()) {
			if (columnNames != null && !columnNames.isEmpty()) {
				if (indexName != null && !indexName.isEmpty()) {
					StringBuilder query = new StringBuilder();
					query.append("CREATE");
					if (unique) {
						query.append(" UNIQUE");
					}
					query.append(" INDEX IF NOT EXISTS ");
					query.append(indexName);
					query.append(" ON ");
					query.append(tableName);
					query.append(" (");
					for (int pos = 0; pos < columnNames.size(); pos++) {
						if (pos != 0) {
							query.append(", ");
						}
						query.append(columnNames.get(pos));
					}
					query.append(")");
					return query.toString();
				}
			}
		} else {
			logger.error("Отсутствует название Таблицы");
		}
		return null;
	}

	@Override
	public String queryForCreateSpaceJsonIndex(String spaceName, String propertyName, String indexName, boolean unique) {
		if (spaceName != null && !spaceName.isEmpty()) {
			if (propertyName != null && !propertyName.isEmpty()) {
				if (indexName != null && !indexName.isEmpty()) {
					StringBuilder query = new StringBuilder();
					query.append("CREATE");
					if (unique) {
						query.append(" UNIQUE");
					}
					query.append(" INDEX IF NOT EXISTS ");
					query.append(indexName);
					query.append(" ON ");
					query.append(spaceName);
					query.append(" USING GIN (");
					query.append(propertyName);
					query.append(" jsonb_path_ops)");
					return query.toString();
				}
			}
		} else {
			logger.error("Отсутствует название Таблицы");
		}
		return null;
	}

	@Override
	public String queryForDeleteSpace(String spaceName) {
		if (spaceName != null && !spaceName.isEmpty()) {
			StringBuilder query = new StringBuilder();
			query.append("DROP TABLE IF EXISTS ");
			query.append(spaceName);
			return query.toString();
		} else {
			logger.error("Отсутствует название Таблицы");
		}
		return null;
	}

	@Override
	public String queryForSelectRows(String tableName, List<Column> tableColumns, List<ElementsSort> sorts, List<ElementsSelectionCondition> selectionConditions, SelectionType selectionType, int offset, int limit) {
		String selectPart = buildSelectQueryPart(tableName, tableColumns);
		String selectionExpressionQueryPart = buildSelectionConditionExpression(selectionConditions, selectionType);
		String sortQueryPart = buildSortQueryPart(sorts);
		String limitPart = buildLimitsQueryPart(offset, limit);
		if (selectPart != null) {
			if (selectionExpressionQueryPart != null) {
				selectPart += " " + selectionExpressionQueryPart;
			}
			if (sortQueryPart != null) {
				selectPart += " " + sortQueryPart;
			}
			if (limitPart != null) {
				selectPart += " " + limitPart;
			}
		}
		return selectPart;
	}

	@Override
	public String queryForCountRows(String tableName, List<Column> tableColumns, List<ElementsSelectionCondition> selectionConditions, SelectionType selectionType) {
		String countPart = buildCountQueryPart(tableName, tableColumns);
		String selectionExpressionQueryPart = buildSelectionConditionExpression(selectionConditions, selectionType);
		if (countPart != null) {
			if (selectionExpressionQueryPart != null) {
				countPart += " " + selectionExpressionQueryPart;
			}
		}
		return countPart;
	}

	@Override
	public String queryForCreateRow(String tableName, List<String> columnNames) {
		if (tableName != null && !tableName.isEmpty()) {
			if (columnNames != null && !columnNames.isEmpty()) {
				StringBuffer sb = new StringBuffer("INSERT INTO ").append(tableName).append(" (");
				Optional<String> optional = columnNames.stream()
				                                       .reduce((s, s2) -> s + ", " + s2);
				optional.ifPresent(sb::append);
				sb.append(") VALUES (");
				for (int i = 1; i <= columnNames.size(); i++) {
					sb.append("?");
					if (i < columnNames.size()) {
						sb.append(",");
					}
				}
				sb.append(")");
				return sb.toString();
			} else {
				logger.warn("Отсутствует данные для вставки");
			}
		} else {
			logger.warn("Отсутствует название Таблицы");
		}
		return null;
	}

	@Override
	public String queryForUpdateRow(String tableName, List<String> columnNames, List<ElementsSelectionCondition> selectionConditions, SelectionType selectionType) {
		if (tableName != null && !tableName.isEmpty()) {
			if (columnNames != null && !columnNames.isEmpty()) {
				StringBuffer sb = new StringBuffer("UPDATE ").append(tableName).append(" SET ");
				sb.append(columnNames.stream()
				                     .map(name -> String.format("%s = ?", name))
				                     .reduce((s1, s2) -> s1 + ", " + s2)
				                     .get());
				String selectionExpressionQueryPart = buildSelectionConditionExpression(selectionConditions, selectionType);
				if (selectionExpressionQueryPart != null) {
					sb.append(" ").append(selectionExpressionQueryPart);
				}
				return sb.toString();
			} else {
				logger.warn("Отсутствует данные для вставки");
			}
		} else {
			logger.warn("Отсутствует название Таблицы");
		}
		return null;
	}

	@Override
	public String queryForDeleteRow(String tableName, List<ElementsSelectionCondition> selectionConditions, SelectionType selectionType) {
		if (tableName != null && !tableName.isEmpty()) {
			StringBuffer sb = new StringBuffer("DELETE FROM ").append(tableName);
			String selectionExpressionQueryPart = buildSelectionConditionExpression(selectionConditions, selectionType);
			if (selectionExpressionQueryPart != null) {
				sb.append(" ").append(selectionExpressionQueryPart);
			}
			return sb.toString();
		} else {
			logger.warn("Отсутствует название Таблицы");
		}
		return null;
	}

}
