# Database Storage

Реализация [Storage API](https://github.com/rds-source/storage-api) для организации хранения и обработки информации в базах данных.

### Основы
* [DatabaseStorage](src/main/java/ru/rds/data/database/DatabaseStorage.java) - реализация [Storage](https://github.com/rds-source/storage-api/blob/master/src/main/java/ru/rds/data/storage/Storage.java) - реализует механизмы управления Таблицами;
* [Table](src/main/java/ru/rds/data/database/Table.java) - реализация [Space](https://github.com/rds-source/storage-api/blob/master/src/main/java/ru/rds/data/storage/Space.java) - описывает Таблицу БД и реализует механизмы управления Строками;
* [Column](src/main/java/ru/rds/data/database/Column.java) - реализация [SpaceProperty](https://github.com/rds-source/storage-api/blob/master/src/main/java/ru/rds/data/storage/SpaceProperty.java) - описывает Колонку Таблицы БД;
* [Row](src/main/java/ru/rds/data/database/Row.java) - реализация [Element](https://github.com/rds-source/storage-api/blob/master/src/main/java/ru/rds/data/storage/Element.java) - описывает Строку Таблицы и реализует механизмы управления её данными;

### Примеры использования
Пример создания таблицы:
```java
// Создание DataSource
HikariDataSource dataSource = new HikariDataSource();
dataSource.setDriverClassName("org.h2.Driver");
dataSource.setJdbcUrl("jdbc:h2:mem:test;DATABASE_TO_UPPER=false;INIT=RUNSCRIPT FROM 'classpath:h2init.sql'");
dataSource.setUsername("sa");
dataSource.setPassword("sa");
// Создание DatabaseQueryBuilder
DatabaseQueryBuilder databaseQueryBuilder = new H2DatabaseQueryBuilder();
// Создание DatabaseStorage
DatabaseStorage databaseStorage = new DatabaseStorage(dataSource, databaseQueryBuilder);

// Создание колонок Таблицы
Column id = new Column();
id.setName("id");
id.setUnique(true);
id.setPrimaryKey(true);
id.setType(ColumnType.VARCHAR);
id.setSize(36);
id.setNullable(false);
id.setDescription("Идентификатор");

Column name = new Column();
name.setName("name");
name.setType(ColumnType.VARCHAR);
name.setSize(255);
name.setNullable(true);
name.setDescription("Название");

// Создание Таблицы
databaseStorage.createSpace("tasks", "Table of Tasks", Arrays.asList(id, name));
```
Пример получения записей таблицы:
```java
...
// Получение Таблицы
Table table = databaseStorage.getSpace("tasks").orElseGet(() -> null);
// Получение всех строк Таблицы
List<Row> rows = table.getElements();
```

### Требования
- [JDK 8](https://openjdk.org/projects/jdk/8/)

### Зависимости
- [Storage API](https://github.com/rds-source/storage-api)
- [SLF4J API](https://github.com/qos-ch/slf4j)