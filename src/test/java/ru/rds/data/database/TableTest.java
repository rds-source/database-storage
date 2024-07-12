package ru.rds.data.database;

import com.zaxxer.hikari.HikariDataSource;
import java.util.*;
import org.junit.jupiter.api.*;
import ru.rds.data.storage.ElementsSelectionCondition;
import ru.rds.data.storage.SelectionConditionExpression;
import ru.rds.data.storage.SelectionType;

@DisplayName("Тестирование Table")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TableTest {

	private static Table table;

	@BeforeAll
	static void init() {
		HikariDataSource dataSource = new HikariDataSource();
		dataSource.setDriverClassName("org.h2.Driver");
		dataSource.setJdbcUrl("jdbc:h2:mem:test;DATABASE_TO_UPPER=false;INIT=RUNSCRIPT FROM 'classpath:h2init.sql'");
		dataSource.setUsername("sa");
		dataSource.setPassword("sa");

		DatabaseQueryBuilder databaseQueryBuilder = new H2DatabaseQueryBuilder();

		DatabaseStorage databaseStorage = new DatabaseStorage(dataSource, databaseQueryBuilder);
		createTable(databaseStorage);
		table = databaseStorage.getSpace("tasks").orElseGet(() -> null);
		Assertions.assertNotNull(table);
	}

	static void createTable(DatabaseStorage databaseStorage) {
		Column id = new Column();
		id.setName("id");
		id.setUnique(true);
		id.setPrimaryKey(true);
		id.setType(ColumnType.VARCHAR);
		id.setSize(36);
		id.setNullable(false);
		id.setLabel("Идентификатор");

		Column name = new Column();
		name.setName("name");
		name.setType(ColumnType.VARCHAR);
		name.setSize(255);
		name.setNullable(true);
		name.setLabel("Название");

		Column description = new Column();
		description.setName("description");
		description.setType(ColumnType.VARCHAR);
		description.setSize(255);
		description.setNullable(true);
		description.setLabel("Описание");

		Column version = new Column();
		version.setName("version");
		version.setType(ColumnType.INTEGER);
		version.setNullable(true);
		version.setLabel("Версия");

		databaseStorage.createSpace("tasks", "Table of Tasks", Arrays.asList(id, name, description, version));
	}

	@Test
	@DisplayName("Создание новой строки")
	@Order(1)
	void createRow() {
		UUID id = UUID.fromString("80b059a0-0c56-4caa-bd46-3b49c61e576d");

		Row row = new Row(table.getProperties());
		row.setValue("id", id);
		row.setValue("name", "Random name");
		row.setValue("description", "Random description");
		row.setValue("version", 255);

		try {
			table.createElement(row);
			Assertions.assertTrue(true);
		} catch (Exception e) {
			Assertions.fail(e);
		}

		ElementsSelectionCondition condition = new ElementsSelectionCondition();
		condition.setSpacePropertyName("id");
		condition.setSpacePropertyValue(id.toString());

		List<Row> rows = table.getElements(Collections.singletonList(condition), SelectionType.AND);
		Assertions.assertEquals(1, rows.size());

		Row firstRow = rows.get(0);
		Assertions.assertEquals(id.toString(), firstRow.getValue("id").orElseGet(() -> null));
	}

	@Test
	@DisplayName("Обновление значений строки")
	@Order(2)
	void updateRow() {
		Row row = new Row(table.getProperties());
		row.setValue("name", "Updated task name");

		ElementsSelectionCondition condition = new ElementsSelectionCondition();
		condition.setSpacePropertyName("id");
		condition.setSpacePropertyValue(UUID.fromString("80b059a0-0c56-4caa-bd46-3b49c61e576d").toString());

		table.updateElements(row, Collections.singletonList(condition));

		List<Row> rows = table.getElements(Collections.singletonList(condition), SelectionType.AND);
		Assertions.assertEquals(1, rows.size());

		Row firstRow = rows.get(0);
		Assertions.assertEquals("80b059a0-0c56-4caa-bd46-3b49c61e576d", firstRow.getValue("id").orElseGet(() -> null));
		Assertions.assertEquals("Updated task name", firstRow.getValue("name").orElseGet(() -> null));
	}

	@Test
	@DisplayName("Получение количества всех строк")
	@Order(3)
	void countAllRows() {
		long count = table.getElementsCount();
		Assertions.assertEquals(1, count);
	}

	@Test
	@DisplayName("Получение всех строк")
	@Order(4)
	void getAllRows() {
		List<Row> rows = table.getElements();
		Assertions.assertEquals(1, rows.size());
	}

	@Test
	@DisplayName("Получение количества строк (в соответствии с условиями)")
	@Order(5)
	void countRows() {
		ElementsSelectionCondition condition = new ElementsSelectionCondition();
		condition.setSpacePropertyName("version");
		condition.setSpacePropertyValue(255);
		condition.setSelectionConditionExpression(SelectionConditionExpression.EQUAL);

		long count = table.getElementsCount(Collections.singletonList(condition), SelectionType.AND);
		Assertions.assertEquals(1, count);
	}

	@Test
	@DisplayName("Получение строк (в соответствии с условиями)")
	@Order(6)
	void getRows() {
		ElementsSelectionCondition condition = new ElementsSelectionCondition();
		condition.setSpacePropertyName("version");
		condition.setSpacePropertyValue(255);
		condition.setSelectionConditionExpression(SelectionConditionExpression.EQUAL);

		List<Row> rows = table.getElements(Collections.singletonList(condition), SelectionType.AND);
		Assertions.assertEquals(1, rows.size());
	}

	@Test
	@DisplayName("Удаление строки")
	@Order(7)
	void deleteRow() {
		ElementsSelectionCondition condition = new ElementsSelectionCondition();
		condition.setSpacePropertyName("id");
		condition.setSpacePropertyValue(UUID.fromString("80b059a0-0c56-4caa-bd46-3b49c61e576d").toString());

		List<Row> rows = table.getElements(Collections.singletonList(condition), SelectionType.AND);
		Assertions.assertEquals(1, rows.size());

		table.deleteElements(Collections.singletonList(condition));

		rows = table.getElements(Collections.singletonList(condition), SelectionType.AND);
		Assertions.assertEquals(0, rows.size());
	}

}
