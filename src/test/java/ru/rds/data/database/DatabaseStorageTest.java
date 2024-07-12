package ru.rds.data.database;

import com.zaxxer.hikari.HikariDataSource;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.*;

@DisplayName("Тестирование DatabaseStorage")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DatabaseStorageTest {

	static DatabaseStorage databaseStorage;

	@BeforeAll
	static void init() {
		HikariDataSource dataSource = new HikariDataSource();
		dataSource.setDriverClassName("org.h2.Driver");
		dataSource.setJdbcUrl("jdbc:h2:mem:test;DATABASE_TO_UPPER=false;INIT=RUNSCRIPT FROM 'classpath:h2init.sql'");
		dataSource.setUsername("sa");
		dataSource.setPassword("sa");

		DatabaseQueryBuilder databaseQueryBuilder = new H2DatabaseQueryBuilder();

		databaseStorage = new DatabaseStorage(dataSource, databaseQueryBuilder);
	}

	@Test
	@DisplayName("Создание новой Таблицы")
	@Order(1)
	void createTable() {
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

		Optional<Table> tasksTable = databaseStorage.getSpace("tasks");
		Assertions.assertNotNull(tasksTable.orElseGet(() -> null));
	}

	@Test
	@DisplayName("Получение сведений обо всех Таблицах")
	@Order(2)
	void getAllTable() {
		List<Table> tables = databaseStorage.getSpaces();
		Assertions.assertFalse(tables.isEmpty());
	}

	@Test
	@DisplayName("Получение сведений о Таблице")
	@Order(3)
	void getTable() {
		Optional<Table> tasksTable = databaseStorage.getSpace("tasks");
		Assertions.assertNotNull(tasksTable.orElseGet(() -> null));
	}

	@Test
	@DisplayName("Получение сведений о Столбцах Таблицы")
	@Order(4)
	void getTableColumns() {
		List<Column> columns = databaseStorage.getSpaceProperties("tasks");
		Assertions.assertEquals(4, columns.size());
	}

	@Test
	@DisplayName("Удаление Таблицы")
	@Order(5)
	void deleteTable() {
		databaseStorage.deleteSpace("tasks");
		Optional<Table> tasksTable = databaseStorage.getSpace("tasks");
		Assertions.assertNull(tasksTable.orElseGet(() -> null));
	}

}
